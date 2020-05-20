package com.nuc.server.activity.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

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


public class PersonalFragment extends Fragment {

    private View rootView;
    private SharedPreferences pref;
    private String url;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(null == rootView){
            rootView = inflater.inflate(R.layout.fragment_personal,container,false);
            initView(rootView);
            getHandler();
        }
        return rootView;
    }
    private void initView(View rootView){

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.navigation_personal);
        View headerView = navigationView.getHeaderView(0);
        TextView userName_personal = headerView.findViewById(R.id.username_personal);

        pref = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        final String id = String.valueOf(pref.getInt("id",0));
        final String sid = pref.getString("sid",null);
        final String username = pref.getString("userName","临时用户");

        userName_personal.setText(username);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.nav_menu_text_color);
        //设置item的条目颜色
        navigationView.setItemTextColor(csl);
        //去掉默认颜色显示原来颜色  设置为null显示本来图片的颜色
        navigationView.setItemIconTintList(csl);

        //设置条目点击监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //点击哪个按钮
                if (menuItem.getTitle().equals("修改信息")){

                    menuItem.setChecked(true);

                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    View view = inflater.inflate(R.layout.input_layout3,null,false);
                    final EditText updateName = view.findViewById(R.id.update_name);
                    final EditText updateNum = view.findViewById(R.id.update_num);
                    updateName.setText(pref.getString("userName","临时用户"));
                    updateNum.setText(pref.getString("num","null"));

                    new AlertDialog.Builder(getContext())
                            .setView(view)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final String newUsername = updateName.getText().toString();
                                    final String newNum = updateNum.getText().toString();
                                    url = MyUrl.base + "user/updateMessage.json";
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            PostTestForMessage(url, newUsername, newNum,id);
                                        }
                                    }.start();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();

                } else if (menuItem.getTitle().equals("修改密码")){
                    menuItem.setChecked(true);

                    //输入密码
                    final EditText input = new EditText(getContext());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("请输入密码").setView(input).setNegativeButton("取消",null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input_psd = input.getText().toString();
                            if (input_psd.equals(pref.getString("password",null))) {
                                LayoutInflater inflater = LayoutInflater.from(getContext());
                                View view = inflater.inflate(R.layout.input_layout4,null,false);
                                final EditText updatePsd = view.findViewById(R.id.update_psd);
                                final EditText updateConfirm = view.findViewById(R.id.update_confirm);

                                new AlertDialog.Builder(getContext())
                                        .setView(view)
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                url = MyUrl.base + "user/updatePsd.json";
                                                final String newPsd = updatePsd.getText().toString();
                                                final String newPsdConfirm = updateConfirm.getText().toString();
                                                if (newPsd.equals(newPsdConfirm)){
                                                    new Thread() {
                                                        @Override
                                                        public void run() {
                                                            PostTestForPsd(url,newPsd,id);
                                                        }
                                                    }.start();
                                                }
                                                else {
                                                    new android.support.v7.app.AlertDialog.Builder(getContext())
                                                            .setTitle("两次密码不一致，请重新输入")
                                                            .setPositiveButton("确认", null).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                            else {
                                new android.support.v7.app.AlertDialog.Builder(getContext())
                                        .setTitle("密码错误")
                                        .setPositiveButton("确认", null).show();
                            }
                        }
                    }).show();
                } else {
                    Toast.makeText(getContext(),"您负责的传感器为："+sid+"号传感器",Toast.LENGTH_SHORT).show();
                    menuItem.setChecked(true);
                }
                return false;
            }
        });
    }

    private void getHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:
                        new android.support.v7.app.AlertDialog.Builder(getContext())
                                .setTitle("修改失败")
                                .setMessage("请检查网络连接")
                                .setPositiveButton("确认", null).show();
                        break;
                    case 2:
                        new android.support.v7.app.AlertDialog.Builder(getContext())
                                .setTitle("修改成功")
                                .setPositiveButton("确认", null).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void PostTestForMessage(final String url,String userName,String num,String id){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        formbuilder.add("userName", userName);
        formbuilder.add("num", num);
        formbuilder.add("id",id);

        FormBody body = formbuilder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("jsonshow", "updateUser:"+call.toString());
                Log.d("jsonshowerror", "updateUser:"+e.toString());

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String callback = response.body().string();
                Log.d("jsonshow", "response:"+callback);
                analysis(callback,url);
            }
        });
    }

    private void PostTestForPsd(final String url,String password,String id){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        formbuilder.add("password", password);
        formbuilder.add("id",id);

        FormBody body = formbuilder.build();
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("jsonshow", "updateUser:"+call.toString());
                Log.d("jsonshowerror", "updateUser:"+e.toString());

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String callback = response.body().string();
                Log.d("jsonshow", "response:"+callback);
                analysis(callback,url);
            }
        });
    }

    private void analysis(String result,String url) {
        JsonParser parser = new JsonParser();  //创建JSON解析器
        JsonObject subObject = (JsonObject) parser.parse(result);

        int s = subObject.get("status").getAsInt();

        Log.d("jsonshow", "status:" + s);
        if (url.equals(MyUrl.base + "user/updateMessage.json")) {
            if (s == 0) {
                Message message1 = new Message();
                message1.what = 1;
                handler.sendMessage(message1);
            }
            else if (s == 1) {
                Message message = Message.obtain();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
        if (url.equals(MyUrl.base + "user/updatePsd.json")) {
            if (s == 0) {
                Message message1 = new Message();
                message1.what = 1;
                handler.sendMessage(message1);
            }
            else if (s == 1) {
                Message message = Message.obtain();
                message.what = 2;
                handler.sendMessage(message);
            }
        }
    }
}
