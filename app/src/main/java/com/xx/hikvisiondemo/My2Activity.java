package com.xx.hikvisiondemo;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.xx.hikvisiondemo.databinding.My1Binding;
import com.xx.hikvisiondemo.databinding.My2Binding;

/**
 * 封装调用
 * 双路播放
 */
public class My2Activity extends Activity {
    CameraManager cameraManager1 = new CameraManager();
    CameraManager cameraManager2 = new CameraManager();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        My2Binding binding = DataBindingUtil.setContentView(this, R.layout.my2);
        //设置摄像头参数
        CameraDevice cameraDevice1 = new CameraDevice("192.168.1.65", 8000, "admin", "pw&123456", 1);
        CameraDevice cameraDevice2 = new CameraDevice("192.168.1.66", 8000, "admin", "pw&123456", 1);
        //启动
        cameraManager1.initAll(this, cameraDevice1, binding.surfaceView1);
        cameraManager2.initAll(this, cameraDevice2, binding.surfaceView2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager1.stopPlay();
        cameraManager1.logoutDevice();
        cameraManager1.freeSDK();

        cameraManager2.stopPlay();
        cameraManager2.logoutDevice();
        cameraManager2.freeSDK();
    }
}
