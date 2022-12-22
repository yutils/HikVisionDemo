package com.xx.hikvisiondemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.hikvision.CameraDevice;
import com.hikvision.CameraManager;
import com.xx.hikvisiondemo.databinding.My1Binding;
import com.yujing.utils.YImageDialog;
import com.yujing.utils.YToast;
import com.yujing.utils.YTts;

/**
 * 封装调用
 * 单路播放
 *
 * @author yujing 2021年4月23日15:19:26
 */
public class My1Activity extends Activity {
    //海康威视摄像头管理类
    CameraManager cameraManager = new CameraManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        My1Binding binding = DataBindingUtil.setContentView(this, R.layout.my1);
        //设置摄像头参数
        CameraDevice cameraDevice = null;//= new CameraDevice("192.168.1.67", 8000, "admin", "pw&123456", 0);
        //如果有传过来的数据。就播放传递过来的数据
        CameraDevice[] cameraDevices = (CameraDevice[]) getIntent().getSerializableExtra("data");
        if (cameraDevices.length >= 1) cameraDevice = cameraDevices[0];

        //启动
        cameraManager.initAll(this, cameraDevice, binding.surfaceView);

//        //拍照存文件
//        binding.btTake.setOnClickListener(v -> {
//            long time = System.currentTimeMillis();
//            String path = getApplicationContext().getFilesDir().getPath() + "/" + "a.jpg";
//            boolean success = cameraManager.takePicture(path);
//            YToast.show(getApplication(), "拍照:" + success + "\n路径:" + path + "\n耗时：" + (System.currentTimeMillis() - time));
//        });

        //拍照获取Bitmap
        binding.btTake.setOnClickListener(v -> {
            //拍照监听
            long time = System.currentTimeMillis();
            Bitmap bitmap = cameraManager.takePicture();
            //bitmap = Utils.addTextToBitmap(bitmap, "名称：张三");
            if (bitmap == null) {
                YTts.play("拍照失败");
                YToast.show("拍照失败");
                return;
            }
            YImageDialog.show(bitmap);
            YToast.show("分辨率:" + bitmap.getWidth() + "*" + bitmap.getHeight() + "\n耗时：" + (System.currentTimeMillis() - time));
        });

        //叠加文字
        binding.btAddString.setOnClickListener(v -> {
            cameraManager.showString("哈哈哈，测试成功");
        });

        //清除文字
        binding.btClearString.setOnClickListener(v -> {
            cameraManager.showString("");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraManager.onDestroy();
    }
}
