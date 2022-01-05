package com.xx.hikvisiondemo;

import static android.os.Environment.MEDIA_MOUNTED;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;

import androidx.databinding.DataBindingUtil;

import com.hikvision.CameraDevice;
import com.hikvision.CameraManager;
import com.xx.hikvisiondemo.databinding.My1Binding;
import com.yujing.utils.YImageDialog;
import com.yujing.utils.YToast;

/**
 * 封装调用
 * 单路播放
 *
 * @author yujing 2021年4月23日15:19:26
 */
public class My1Activity extends Activity {
    CameraManager cameraManager = new CameraManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        My1Binding binding = DataBindingUtil.setContentView(this, R.layout.my1);
        //设置摄像头参数
        CameraDevice cameraDevice = null;//= new CameraDevice("192.168.1.67", 8000, "admin", "pw&123456", 0);
        //如果有传过来的数据。就播放传递过来的数据
        CameraDevice[] cameraDevices = (CameraDevice[]) getIntent().getSerializableExtra("data");
        if (cameraDevices.length >= 1) {
            cameraDevice = cameraDevices[0];
        }

        //启动
        cameraManager.initAll(this, cameraDevice, binding.surfaceView);


//        //拍照
//        binding.btTake.setOnClickListener(v -> {
//            long time = System.currentTimeMillis();
//            String path = getFilePath(getApplicationContext()) + "/" + "a.jpg";
//            boolean success = cameraManager.takePicture(path);
//            Toast.makeText(getApplication(), "拍照:" + success + "\n路径:" + path + "\n耗时：" + (System.currentTimeMillis() - time), Toast.LENGTH_LONG).show();
//        });

        //拍照
        binding.btTake.setOnClickListener(v -> {
            //拍照监听
            long time = System.currentTimeMillis();
            Bitmap bitmap = cameraManager.takePicture();
            if (bitmap == null) return;
            YImageDialog.show(bitmap);
            YToast.show("分辨率:" + bitmap.getWidth() + "*" + bitmap.getHeight() + "\n耗时：" + (System.currentTimeMillis() - time));
        });
    }


    public static String getFilePath(Context context) {
        String directoryPath;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {//判断外部存储是否可用
            try {
                directoryPath = context.getExternalFilesDir("").getAbsolutePath();
            } catch (Exception e) {
                directoryPath = context.getFilesDir().getPath();
            }
        } else {//没外部存储就使用内部存储
            directoryPath = context.getFilesDir().getPath();
        }
        return directoryPath;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.onDestroy();
    }
}
