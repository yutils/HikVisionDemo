package com.xx.hikvisiondemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.hikvision.CameraDevice;
import com.xx.hikvisiondemo.databinding.MainActivityBinding;

/**
 * 首页
 * @author yujing 2021年4月23日15:19:26
 */
public class MainActivity extends Activity {
    MainActivityBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        //播放1到4路视频
        binding.btPlay1.setOnClickListener(v -> startActivity(new Intent(this, CameraBaseActivity.class).putExtra("data", getData())));
        binding.btPlay2.setOnClickListener(v -> startActivity(new Intent(this, My1Activity.class).putExtra("data", getData())));
        binding.btPlay3.setOnClickListener(v -> startActivity(new Intent(this, My2Activity.class).putExtra("data", getData())));
        binding.btPlay4.setOnClickListener(v -> startActivity(new Intent(this, My4Activity.class).putExtra("data", getData())));
    }

    //    CameraDevice cameraDevice = new CameraDevice("192.168.1.65", 8000, "admin", "pw&123456", 0);
    public CameraDevice[] getData() {
        CameraDevice[] deviceList = new CameraDevice[4];
        try {
            deviceList[0] = new CameraDevice(
                    binding.etIp1.getText().toString(),
                    Integer.parseInt(binding.etPort1.getText().toString()),
                    binding.etUser1.getText().toString(),
                    binding.etPassword1.getText().toString(),
                    Integer.parseInt(binding.etChannel1.getText().toString()));
        } catch (Exception ignored) {
        }
        try {
            deviceList[1] = new CameraDevice(
                    binding.etIp2.getText().toString(),
                    Integer.parseInt(binding.etPort2.getText().toString()),
                    binding.etUser2.getText().toString(),
                    binding.etPassword2.getText().toString(),
                    Integer.parseInt(binding.etChannel2.getText().toString()));
        } catch (Exception ignored) {
        }
        try {
            deviceList[2] = new CameraDevice(
                    binding.etIp3.getText().toString(),
                    Integer.parseInt(binding.etPort3.getText().toString()),
                    binding.etUser3.getText().toString(),
                    binding.etPassword3.getText().toString(),
                    Integer.parseInt(binding.etChannel3.getText().toString()));
        } catch (Exception ignored) {
        }
        try {
            deviceList[3] = new CameraDevice(
                    binding.etIp4.getText().toString(),
                    Integer.parseInt(binding.etPort4.getText().toString()),
                    binding.etUser4.getText().toString(),
                    binding.etPassword4.getText().toString(),
                    Integer.parseInt(binding.etChannel4.getText().toString()));
        } catch (Exception ignored) {
        }
        return deviceList;
    }
}

