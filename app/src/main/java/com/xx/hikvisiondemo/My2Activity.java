package com.xx.hikvisiondemo;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.hikvision.CameraDevice;
import com.hikvision.CameraManager;
import com.xx.hikvisiondemo.databinding.My2Binding;

/**
 * 封装调用
 * 双路播放
 * @author yujing 2021年4月23日15:19:26
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
        //如果有传过来的数据。就播放传递过来的数据
        CameraDevice[] cameraDevices= (CameraDevice[]) getIntent().getSerializableExtra("data");
        if (cameraDevices.length>=2){
            cameraDevice1=cameraDevices[0];
            cameraDevice2=cameraDevices[1];
        }
        //启动
        cameraManager1.initAll(this, cameraDevice1, binding.surfaceView1);
        cameraManager2.initAll(this, cameraDevice2, binding.surfaceView2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager1.onDestroy();
        cameraManager2.onDestroy();
    }
}
