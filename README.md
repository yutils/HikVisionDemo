海康威视Android studio版 直接代码中改变参数就可以是使用  
在作者原版demo基础上  
1.更新libs到海康威视最新版  
2.更新AndroidStudio到4.1.3    
3.更新gradle到6.5  
4.升级到安卓11  
5.升级到Androidx  
6.升级到java8  
7.优化部分代码结构  
8.添加可控子码流变量  
## 封装后一路播放请看  My1Activity代码
## 双路播放请看  My2Activity代码

双路播放
```java
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
```
