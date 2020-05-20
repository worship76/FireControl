package com.nuc.server.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nuc.server.activity.MainActivity;

public class Warn extends Activity {

    private Vibrator vibrator;//设置震动
    private PowerManager.WakeLock mWakelock;//设置屏幕唤醒

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置屏幕唤醒
        //Toast.makeText(getApplicationContext(),"成功跳转",Toast.LENGTH_SHORT).show();测试
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //设置震动
        startVibrator();
        //设置弹窗
        createDialog();
    }

    //设置震动
    private void startVibrator(){

        //获取NoticeActivity中存储的status
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int status = preferences.getInt("status",0);

        if(status == 1){

            vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);

            final int rateData = preferences.getInt("rateData",100);
            long[] pattern = { rateData,rateData,rateData,rateData }; // 停止 开启 停止 开启
            //第一个参数pattern表示震动频率，第二个参数0表示循环播放
            vibrator.vibrate(pattern, 0);
        } else {
            Toast.makeText(Warn.this,"震动开关未打卡",Toast.LENGTH_SHORT).show();
        }
    }

    //显示弹窗
    private void createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警报！警报！")
                .setCancelable(false)//点击窗口外部不退出窗口
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (vibrator != null){
                            vibrator.cancel();
                        }
                        finish();
                        //回到首页列表
                         Intent intent1 = new Intent(Warn.this,MainActivity.class);
                        startActivity(intent1);
                    }
                }).show();
    }

    //锁屏状态下唤醒屏幕，要在OnResume()方法中启动，并在OnPause()中释放,不然会出bug，并且在AndroidManifest.xml要添加息屏权限不然会报权限错误
    @Override
    protected void onResume() {
        super.onResume();
        //唤醒屏幕
        acquireWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //media.release();
        //释放屏幕
        releaseWakeLock();
    }

    /**
     * 唤醒屏幕
     */
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock(){
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        mWakelock.acquire();
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock(){
        mWakelock.release();
    }
}
