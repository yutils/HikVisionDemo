package com.xx.hikvisiondemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;


/**
 * 存储app全局数据，实现全局共享数据
 */
public class App extends Application {
    //获取数据初始设置
    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取数据初始设置
        preferences = getSharedPreferences("preferences", MODE_PRIVATE);
        editor = preferences.edit();
        initValue();
    }

    //设置系统初始值
    private void initValue() {
        //设置初始音量值0-15
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 7, AudioManager.FLAG_PLAY_SOUND);
    }
}
