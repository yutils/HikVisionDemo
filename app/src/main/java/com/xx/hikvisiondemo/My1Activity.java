package com.xx.hikvisiondemo;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.xx.hikvisiondemo.databinding.My1Binding;

/**
 * 封装调用
 * 单路播放
 */
public class My1Activity extends Activity {
    CameraManager cameraManager = new CameraManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        My1Binding binding = DataBindingUtil.setContentView(this, R.layout.my1);
        //设置摄像头参数
        CameraDevice cameraDevice = new CameraDevice("192.168.1.65", 8000, "admin", "pw&123456", 2);
        //启动
        cameraManager.initAll(this, cameraDevice, binding.surfaceView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.stopPlay();
        cameraManager.logoutDevice();
        cameraManager.freeSDK();
    }
}
