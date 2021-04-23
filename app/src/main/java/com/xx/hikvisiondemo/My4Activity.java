package com.xx.hikvisiondemo;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.hikvision.CameraDevice;
import com.hikvision.CameraManager;
import com.xx.hikvisiondemo.databinding.My4Binding;

/**
 * 封装调用
 * 四路播放
 * @author yujing 2021年4月23日15:19:26
 */
public class My4Activity extends Activity {
    CameraManager cameraManager1 = new CameraManager();
    CameraManager cameraManager2 = new CameraManager();
    CameraManager cameraManager3 = new CameraManager();
    CameraManager cameraManager4 = new CameraManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        My4Binding binding = DataBindingUtil.setContentView(this, R.layout.my4);
        //设置摄像头参数
        CameraDevice cameraDevice1 = new CameraDevice("192.168.1.65", 8000, "admin", "pw&123456", 1);
        CameraDevice cameraDevice2 = new CameraDevice("192.168.1.66", 8000, "admin", "pw&123456", 1);
        CameraDevice cameraDevice3 = new CameraDevice("192.168.1.65", 8000, "admin", "pw&123456", 1);
        CameraDevice cameraDevice4 = new CameraDevice("192.168.1.66", 8000, "admin", "pw&123456", 1);
        //如果有传过来的数据。就播放传递过来的数据
        CameraDevice[] cameraDevices = (CameraDevice[]) getIntent().getSerializableExtra("data");
        if (cameraDevices.length >= 4) {
            cameraDevice1 = cameraDevices[0];
            cameraDevice2 = cameraDevices[1];
            cameraDevice3 = cameraDevices[2];
            cameraDevice4 = cameraDevices[3];
        }
        //启动
        cameraManager1.initAll(this, cameraDevice1, binding.surfaceView1);
        cameraManager2.initAll(this, cameraDevice2, binding.surfaceView2);
        cameraManager3.initAll(this, cameraDevice3, binding.surfaceView3);
        cameraManager4.initAll(this, cameraDevice4, binding.surfaceView4);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager1.onDestroy();
        cameraManager2.onDestroy();
        cameraManager3.onDestroy();
        cameraManager4.onDestroy();
    }
}
