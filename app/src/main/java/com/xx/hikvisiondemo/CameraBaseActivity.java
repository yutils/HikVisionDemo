package com.xx.hikvisiondemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.hikvision.CameraDevice;
import com.hikvision.CameraManager;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

import static com.hikvision.netsdk.PTZPresetCmd.CLE_PRESET;
import static com.hikvision.netsdk.PTZPresetCmd.SET_PRESET;

/**
 * 原始用法
 * 封装调用 请移步My1Activity
 */
public class CameraBaseActivity extends Activity implements Callback, OnTouchListener {
    private SurfaceView surfaceView = null;
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private int m_iLogID = -1; // return by NET_DVR_Login_v30
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime
    private int m_iPort = -1; // play port
    private int m_iStartChan = 0; // start channel no
    private final String TAG = "DemoActivity";
    private boolean m_bStopPlayback = false;
    private Thread thread;
    private boolean isShow = true;
    private Button btnUp;
    private Button btnDown;
    private Button btnLeft;
    private Button btnRight;
    private Button btnZoomIn;
    private Button btnZoomOut;
    private CameraManager h1;
    private App app;

    //设置摄像头参数
    public CameraDevice device = new CameraDevice("192.168.1.65", 8000, "admin", "pw&123456", 0);

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashUtil.getInstance().init(this);
        app = (App) getApplication();
        setContentView(R.layout.base);
        if (!initSdk()) {
            this.finish();
            return;
        }
        //如果有传过来的数据。就播放传递过来的数据
        CameraDevice[] cameraDevices= (CameraDevice[]) getIntent().getSerializableExtra("data");
        if (cameraDevices.length>=1){
            device=cameraDevices[0];
        }
        initActivity();
        // login on the device
        m_iLogID = loginNormalDevice();
        if (m_iLogID < 0) {
            Log.e(TAG, "This device logins failed!");
            return;
        } else {
            System.out.println("m_iLogID=" + m_iLogID);
        }
        // 获取异常回调实例并设置
        ExceptionCallBack exceptionCallBack = getExceptiongCbf();
        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCallBack)) {
            Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
            return;
        }
        //设置默认点
        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                SystemClock.sleep(1000);
                runOnUiThread(() -> {
                    if (isShow)
                        startSinglePreview();
                });
            }
        });
        thread.start();
    }

    // GUI init
    private void initActivity() {
        this.btnZoomOut = findViewById(R.id.btn_ZoomOut);
        this.btnZoomIn = findViewById(R.id.btn_ZoomIn);
        this.btnRight = findViewById(R.id.btn_Right);
        this.btnLeft = findViewById(R.id.btn_Left);
        this.btnDown = findViewById(R.id.btn_Down);
        this.btnUp = findViewById(R.id.btn_Up);
        btnUp.setOnTouchListener(this);
        btnDown.setOnTouchListener(this);
        btnLeft.setOnTouchListener(this);
        btnRight.setOnTouchListener(this);
        btnZoomIn.setOnTouchListener(this);
        btnZoomOut.setOnTouchListener(this);
        this.surfaceView = findViewById(R.id.sf_VideoMonitor);
        surfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort) return;
        Surface surface = holder.getSurface();
        if (surface.isValid()) {
            if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        if (holder.getSurface().isValid()) {
            if (!Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("m_iPort", m_iPort);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        m_iPort = savedInstanceState.getInt("m_iPort");
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * @return true - success;false - fail
     * @fn initeSdk
     * @author zhuzhenlei
     * @brief SDK init
     */
    private boolean initSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (h1 == null) return false;
        Log.d(TAG, "onTouch: ");
        new Thread(() -> {
            switch (v.getId()) {
                case R.id.btn_Up:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        h1.startMove(8, m_iLogID);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        h1.stopMove(8, m_iLogID);
                    }
                    break;
                case R.id.btn_Left:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        h1.startMove(4, m_iLogID);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        h1.stopMove(4, m_iLogID);
                    }
                    break;
                case R.id.btn_Right:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        h1.startMove(6, m_iLogID);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        h1.stopMove(6, m_iLogID);
                    }
                    break;
                case R.id.btn_Down:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        h1.startMove(2, m_iLogID);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        h1.stopMove(2, m_iLogID);
                    }
                    break;
                case R.id.btn_ZoomIn:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        h1.startZoom(1, m_iLogID);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        h1.stopZoom(1, m_iLogID);
                    }
                    break;
                case R.id.btn_ZoomOut:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        h1.startZoom(-1, m_iLogID);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        h1.stopZoom(-1, m_iLogID);
                    }
                    break;
                default:
                    break;
            }
        }).start();
        return false;
    }


    private AlertDialog getDialongView(View view) {
        final AlertDialog.Builder builder6 = new AlertDialog.Builder(CameraBaseActivity.this);
        builder6.setView(view);
        builder6.create();
        AlertDialog dialog = builder6.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        return dialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Cleanup();
        m_iLogID = -1;
        // whether we have logout
        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
            Log.e(TAG, " NET_DVR_Logout is failed!");
            return;
        }
        stopSinglePreview();
    }

    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        Log.i(TAG, "m_iStartChan:" + m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = device.getChannel(); // subStream
        previewInfo.bBlocked = 1;

        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID,
                previewInfo, fRealDataCallBack);

        if (m_iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        isShow = false;
        if (thread != null) {
            thread.interrupt();
        }
        h1 = new CameraManager();
        h1.setLoginId(m_iLogID);
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra("INDEX", -1) != -1) {
            int point = app.preferences.getInt("POINT", 0);
            boolean b = HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, PTZCommand.GOTO_PRESET, point);
        }
    }

    /**
     * @return NULL
     * @fn stopSinglePreview
     * @author zhuzhenlei
     * @brief stop preview
     */
    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }
        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        m_iPlayID = -1;
        stopSinglePlayer();
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }
        if (!Player.getInstance().closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }

    /**
     * @return login ID
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(device.getIp(), device.getPort(),
                device.getUserName(), device.getPassWord(), m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        System.out.println("下面是设备信息************************");
        System.out.println("userId=" + m_iLogID);
        System.out.println("通道开始=" + m_oNetDvrDeviceInfoV30.byStartChan);
        System.out.println("通道个数=" + m_oNetDvrDeviceInfoV30.byChanNum);
        System.out.println("设备类型=" + m_oNetDvrDeviceInfoV30.byDVRType);
        System.out.println("ip通道个数=" + m_oNetDvrDeviceInfoV30.byIPChanNum);
        if (m_iLogID < 0) {
            int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
            Log.e(TAG, "登入设备失败！ code：" + errorCode);
        } else {
            Log.i(TAG, "登入设备成功！");
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }

    /**
     * @return exception instance
     * @fn getExceptiongCbf
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = (iType, iUserID, iHandle) -> System.out.println("recv exception, type:" + iType);
        return oExceptionCbf;
    }

    /**
     * @return callback instance
     * @fn getRealPlayerCbf
     * @brief get realplay callback instance
     */
    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = (iRealHandle, iDataType, pDataBuffer, iDataSize) -> {
            // player channel 1
            CameraBaseActivity.this.processRealData(iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
        };
        return cbf;
    }

    /**
     * @param iDataType   - data type [in]
     * @param pDataBuffer - data buffer [in]
     * @param iDataSize   - data size [in]
     * @param iStreamMode - stream mode [in]
     * @return NULL
     * @fn processRealData
     * @author zhuzhenlei
     * @brief process real data
     */
    public void processRealData(int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) return;
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Log.i(TAG, "getPort succ with: " + m_iPort);
            if (iDataSize > 0) {
                // set stream mode
                if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode)) {
                    Log.e(TAG, "setStreamOpenMode failed");
                    return;
                }
                // open stream
                if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) {
                    Log.e(TAG, "openStream failed");
                    return;
                }
                if (!Player.getInstance().play(m_iPort, surfaceView.getHolder())) {
                    Log.e(TAG, "play failed");
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
            }
        } else {
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer,
                    iDataSize)) {
                // Log.e(TAG, "inputData failed with: " +
                // Player.getInstance().getLastError(m_iPort));
                for (int i = 0; i < 4000 && m_iPlaybackID >= 0
                        && !m_bStopPlayback; i++) {
                    if (Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
                        break;
                    }
                    if (i % 100 == 0) {
                        Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort) + ", i:" + i);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * @return NULL
     * @fn Cleanup
     * @author zhuzhenlei
     * @brief cleanup
     */
    public void Cleanup() {
        // release player resource
        Player.getInstance().freePort(m_iPort);
        m_iPort = -1;
        // release net SDK resource
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }


    public void SetOnclick(View view) {
        View linearLayout = getLayoutInflater().inflate(R.layout.setting_page, null);
        Button button2 = linearLayout.findViewById(R.id.button2);
        Button button1 = linearLayout.findViewById(R.id.button1);
        Button button = linearLayout.findViewById(R.id.button);
        //设置预置点
        final EditText editText = linearLayout.findViewById(R.id.editText);
        final AlertDialog dialog = getDialongView(linearLayout);
        //设置预置点
        button2.setOnClickListener(v -> {
            Integer integer = Integer.valueOf(editText.getText().toString());
            if (integer > 255 || integer < 0) {
                Toast.makeText(CameraBaseActivity.this, "请设置0-255之间", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean b = HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, SET_PRESET,
                    integer);
            if (b) {
                Toast.makeText(CameraBaseActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                app.editor.putInt("POINT", integer).commit();
            } else {
                Toast.makeText(CameraBaseActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "onClick: " + b);
        });
        //清楚预置点
        button1.setOnClickListener(v -> {

            Integer integer = Integer.valueOf(editText.getText().toString());
            if (integer > 255 || integer < 0) {
                Toast.makeText(CameraBaseActivity.this, "请设置0-255之间", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean b = HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, CLE_PRESET,
                    integer);
            if (b) {
                app.editor.remove("POINT").commit();
                Toast.makeText(CameraBaseActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CameraBaseActivity.this, "清除失败", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "onClick: " + b);
            dialog.dismiss();
        });
        //转到预置点
        button.setOnClickListener(v -> {
            Integer integer = Integer.valueOf(editText.getText().toString());
            if (integer > 255 || integer < 0) {
                Toast.makeText(CameraBaseActivity.this, "请设置0-255之间", Toast.LENGTH_SHORT).show();
                return;
            }
            int point = app.preferences.getInt("POINT", 0);
            if (point == 0) {
                Toast.makeText(app, "请先设置预设点", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean b = HCNetSDK.getInstance().NET_DVR_PTZPreset(m_iPlayID, PTZCommand.GOTO_PRESET,
                    point);
            Log.d(TAG, "onClick: " + b);
        });
    }
}
