package com.nuc.server.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.a26559.firecontrol.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.nuc.server.config.MyUrl;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SignActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private String url;
    private VideoView mVideoView;
    private TextView mToRegister;
    private TextView mToLogin;
    private LinearLayout loginLL;
    private LinearLayout registLL;
    private TextView mBtnLogin;
    private EditText UserName;
    private EditText Psd;
    private Button only;
    private Handler handler;

    private TextView mBtnRegister;
    private EditText mUserName;
    private EditText mPsd;
    private EditText mPsdConfirm;
    private EditText mNumber;
    private EditText mSid;

    //private SharedPreferences preferences;
    private android.content.SharedPreferences pref;
    private android.content.SharedPreferences.Editor editor;



    String ison = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        getHandler();
        initView();
        initVideoView();
    }

    /**
     * 初始化页面布局
     */
    private void initView(){

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        loginLL = (LinearLayout) findViewById(R.id.login_LL);
        registLL = (LinearLayout) findViewById(R.id.regist_LL);

        mToLogin = (TextView) findViewById(R.id.to_login);
        mToRegister = (TextView) findViewById(R.id.to_regist);
        mBtnLogin = (TextView) findViewById(R.id.btn_login);
        UserName = (EditText) findViewById(R.id.username_login);
        Psd = (EditText) findViewById(R.id.psd_login);
        only = (Button) findViewById(R.id.once);

        mUserName = (EditText) findViewById(R.id.username_register);
        mPsd = (EditText) findViewById(R.id.psd_register);
        mPsdConfirm = (EditText) findViewById(R.id.psd_confirm);
        mNumber = (EditText) findViewById(R.id.number_register);
        mSid = (EditText) findViewById(R.id.sid_register);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);

        mVideoView = (VideoView) findViewById(R.id.videoView);

        mToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLL.setVisibility(View.GONE);
                registLL.setVisibility(View.VISIBLE);
            }
        });

        mToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLL.setVisibility(View.VISIBLE);
                registLL.setVisibility(View.GONE);
            }
        });

        only.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = UserName.getText().toString();
                final String password = Psd.getText().toString();

                /*preferences = getSharedPreferences("reginfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = preferences.edit();
                editor1.putString("username",userName);
                editor1.commit();*/


                url = MyUrl.base+"user/androidLogin.json";
                System.out.println(url);
                new Thread(){
                    @Override
                    public void run() {
                        PostTestForLogin(url,userName,password);
                    }
                }.start();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUserName.getText().toString();
                final String password = mPsd.getText().toString();
                final String password1 = mPsdConfirm.getText().toString();
                final String num = mNumber.getText().toString();
                final String sid = mSid.getText().toString();
                if (!password.equals(password1)){
                    Toast.makeText(SignActivity.this,"两次密码输入不一样",Toast.LENGTH_SHORT).show();
                }else {
                    url = MyUrl.base+"user/androidRegister.json";
                    new Thread(){
                        @Override
                        public void run() {
                            PostTestForRegister(url,username,password,num,sid);
                        }
                    }.start();
                }
            }
        });
    }

    /**
     * 登录请求
     * @param url
     * @param userName
     * @param password
     */
    public void PostTestForLogin(final String url, String userName, String password){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        formbuilder.add("userName", userName);
        formbuilder.add("password", password);
        editor.putString("userName",userName);
        editor.putString("password",password);

        FormBody body = formbuilder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("jsonshow", "user:"+call.toString());
                Log.d("jsonshowerror", "user:"+e.toString());

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String callback = response.body().string();
                Log.d("jsonshow", "response:"+callback);
                analysis(callback,url);
            }
        });
    }
    /**
     * 注册请求
     * @param url
     * @param userName
     * @param password
     * @param num
     * @param sid
     */
    public void PostTestForRegister(final String url, String userName, String password, String num, String sid){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        formbuilder.add("userName", userName);
        formbuilder.add("password", password);
        formbuilder.add("num", num);
        formbuilder.add("sid", sid);
        editor.putString("userName",userName);
        editor.putString("password",password);
        FormBody body = formbuilder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("jsonshow", "user:"+call.toString());
                Log.d("jsonshowerror", "user:"+e.toString());

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String callback = response.body().string();
                Log.d("jsonshow", "response:"+callback);
                analysis(callback,url);
                if (callback == "密码错误") {
                    Log.d("jsonshow", "密码错误:");
                }
                if(callback.equals("密码错误")||callback.equals("用户不存在")||callback.equals(": 密码错误")||callback.equals(": 密码错误")){
                    Log.d("jsonshow", "密码错误22323:");
                }else {

                }
            }
        });
    }

    /**
     * 解析结果
     */
    private void getHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:
                        new AlertDialog.Builder(SignActivity.this)
                                .setTitle("登录失败")
                                .setMessage("请检查用户名和密码")
                                .setPositiveButton("确认", null).show();
                        break;
                    case 2:
                        new AlertDialog.Builder(SignActivity.this)
                                .setTitle("注册错误")
                                .setMessage("该用户已经存在")
                                .setPositiveButton("确认", null)
                                .show();
                        break;
                    case 3:
                        Toast.makeText(SignActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(SignActivity.this,"注册成功，已为您登录",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        };

        pref = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();

        try{
            ison = pref.getString("isone","no");
        } catch (Exception e){}

        if (ison.equals("yes")){
            url = MyUrl.base+"user/androidLogin.json";
            System.out.println(url);
            new Thread(){
                @Override
                public void run() {
                    PostTestForLogin(url,pref.getString("userName","null")
                            ,pref.getString("password","null"));
                }
            }.start();
//            Intent intent =new Intent(SignActivity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
        }
    }

    /**
     * 解析函数
     */
    private void analysis(String result,String url) {
        JsonParser parser = new JsonParser();  //创建JSON解析器
        JsonObject subObject = (JsonObject) parser.parse(result);

        int s = subObject.get("status").getAsInt();

        Log.d("jsonshow", "status:" + s);
        if (url.equals(MyUrl.base + "user/androidLogin.json")) {
            if (s == 0) {
                Message message1 = new Message();
                message1.what = 1;
                message1.obj = "登录失败";
                handler.sendMessage(message1);
            }
             else if (s == 1) {
                Message message = Message.obtain();
                message.what = 3;
                handler.sendMessage(message);

                String sid = subObject.get("sid").getAsString();
                String num = subObject.get("num").getAsString();
                int id = subObject.get("id").getAsInt();
                editor.putString("sid",sid);
                editor.putString("num",num);
                editor.putInt("id",id);
               /* preferences = getSharedPreferences("reginfo",Context.MODE_PRIVATE);
                SharedPreferences.Editor editorForData = preferences.edit();
                editorForData.putString("sid",sid);
                editorForData.commit();*/

                editor.putString("isone", "yes");
                editor.apply();
                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        if (url.equals(MyUrl.base + "user/androidRegister.json")) {
            if (s == 0) {
                Message message1 = new Message();
                message1.what = 2;
                message1.obj = "注册错误";
                handler.sendMessage(message1);
            }
            else if (s == 1) {
                Message message = Message.obtain();
                message.what = 4;
                handler.sendMessage(message);
                editor.putString("isone", "yes");
                editor.apply();

                Intent intent = new Intent(SignActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 视频播放
     */
    private void initVideoView() {
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sport));
        //设置相关监听
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
    }
    //播放准备
    @Override
    public void onPrepared(MediaPlayer mp){
        //开始播放
        mVideoView.start();
    }
    //播放结束
    @Override
    public void onCompletion(MediaPlayer mp){
        //开始播放
        mVideoView.start();
    }
}
