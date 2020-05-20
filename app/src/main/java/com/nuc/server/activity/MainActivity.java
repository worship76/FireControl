package com.nuc.server.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.a26559.firecontrol.R;
import com.nuc.server.activity.fragment.ContextFragment;
import com.nuc.server.activity.fragment.PaintFragment;
import com.nuc.server.activity.fragment.PersonalFragment;
import com.nuc.server.service.Warn;
import com.nuc.server.util.view.DataView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //private SharedPreferences preferences;
    private SharedPreferences pref;

    //FragmentActivity
    private ViewPager mViewPager;
    private LinearLayout mTabContext;
    private LinearLayout mTabPaint;
    private LinearLayout mTabPersonal;

    private ImageButton mContextImg;
    private ImageButton mPaintImg;
    private ImageButton mPersonalImg;

    private Fragment mContext;
    private Fragment mPaint;
    private Fragment mPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.side_main);

        /*动态权限申请*/
        initPermission();
        /*初始化页面布局*/
        initView();
        initViewSide();
        initViewPage(0);

        initEvent();
    }


    /**
     * 重写点击事件
     * @param arg0：被选择的view
     */
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.id_tab_context:
                initViewPage(0);
                mViewPager.setCurrentItem(0);
                mContextImg.setImageResource(R.drawable.tab_bar_01_check);
                mPaintImg.setImageResource(R.drawable.tab_bar_02);
                mPersonalImg.setImageResource(R.drawable.tab_bar_03);
                break;
            case R.id.id_tab_paint:
                initViewPage(1);
                mViewPager.setCurrentItem(1);
                mContextImg.setImageResource(R.drawable.tab_bar_01);
                mPaintImg.setImageResource(R.drawable.tab_bar_02_check);
                mPersonalImg.setImageResource(R.drawable.tab_bar_03);
                break;
            case R.id.id_tab_personal:
                initViewPage(2);
                mViewPager.setCurrentItem(2);
                mContextImg.setImageResource(R.drawable.tab_bar_01);
                mPaintImg.setImageResource(R.drawable.tab_bar_02);
                mPersonalImg.setImageResource(R.drawable.tab_bar_03_check);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化布局
     */
    private void initView(){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //Fragment
        mTabContext = (LinearLayout) findViewById(R.id.id_tab_context);
        mTabPaint = (LinearLayout) findViewById(R.id.id_tab_paint);
        mTabPersonal = (LinearLayout) findViewById(R.id.id_tab_personal);
        mContextImg = (ImageButton) findViewById(R.id.id_tab_context_img);
        mPaintImg = (ImageButton) findViewById(R.id.id_tab_paint_img);
        mPersonalImg = (ImageButton) findViewById(R.id.id_tab_personal_img);
        mViewPager = (ViewPager) findViewById(R.id.id_viewPage);

        /*状态栏设置*/
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setStatusBar();
    }

    /**
     * 初始化侧滑栏
     */
    private void initViewSide() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationview = (NavigationView) findViewById(R.id.navigation_view);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);//将toolbar与ActionBar关联
        getSupportActionBar().hide();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);//初始化状态
        toggle.syncState();

        /*---------------------------添加头布局和尾布局-----------------------------*/
        //获取xml头布局view
        View headerView = navigationview.getHeaderView(0);

        //寻找头部里面的控件
        ImageView imageView = headerView.findViewById(R.id.iv_head);
        TextView usernameInMain =  headerView.findViewById(R.id.username_main);


        usernameInMain.setText(pref.getString("userName","临时用户"));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "点击了头像", Toast.LENGTH_LONG).show();
            }
        });
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
        ColorStateList csl = (ColorStateList) getResources().getColorStateList(R.color.nav_menu_text_color);
        //设置item的条目颜色
        navigationview.setItemTextColor(csl);
        //去掉默认颜色显示原来颜色  设置为null显示本来图片的颜色
        navigationview.setItemIconTintList(csl);

        //设置条目点击监听
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //点击哪个按钮
                if (menuItem.getTitle().equals("关于")){
                    Toast.makeText(MainActivity.this,"copyright ©1607094120李彬" , Toast.LENGTH_LONG).show();
                    //设置哪个按钮被选中
                    menuItem.setChecked(true);
                } else if(menuItem.getTitle().equals("区域划分")){
                    Toast.makeText(MainActivity.this,"您所负责的区域为"+pref.getString("sid","null")+"区",Toast.LENGTH_LONG).show();
                    menuItem.setChecked(true);
                } else {
                    initPython();
                    callPythonCode();
                }

                //关闭侧边栏
//                drawer.closeDrawers();
                return false;
            }
        });

        //设置按钮点击监听
        Button setting_btn = navigationview.findViewById(R.id.footer_item_setting);
        Button logout_btn = navigationview.findViewById(R.id.footer_item_out);

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setPositiveButton("退出登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this,SignActivity.class);
                        Log.i("退出登录", "onClick: ");
                        SharedPreferences.Editor editor1 = pref.edit();
                        editor1.putString("isone", "no");
                        editor1.apply();
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setMessage("你确定你不是点错了吗？");
                builder.show();
            }
        });
    }
   /**
     * 初始化initViewPage
     * @param i ：要初始化的布局
     */
    private void initViewPage(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();// 创建一个事务
        hideFragment(transaction);// 我们先把所有的Fragment隐藏了，然后下面再开始处理具体要显示的Fragment
        switch (i) {
            case 0:
                if (mContext == null) {
                    mContext = new ContextFragment();
                    transaction.add(R.id.id_content, mContext);// 将Fragment添加到Activity中
                } else {
                    transaction.show(mContext);
                }
                break;
            case 1:
                if (mPaint == null) {
                    mPaint = new PaintFragment();
                    transaction.add(R.id.id_content, mPaint);
                } else {
                    transaction.show(mPaint);
                }
                break;
            case 2:
                if (mPersonal == null) {
                    mPersonal = new PersonalFragment();
                    transaction.add(R.id.id_content, mPersonal);
                } else {
                    transaction.show(mPersonal);
                }
                break;
            default:
                break;
        }
        transaction.commit();// 提交事务
        initEvent();
    }

    /**
     * 传递点击事件
     */
    private void initEvent() {
        mTabContext.setOnClickListener(this);
        mTabPaint.setOnClickListener(this);
        mTabPersonal.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                int currentItem = mViewPager.getCurrentItem();
                switch (currentItem) {
                    case 0:
                        mContextImg.setImageResource(R.drawable.tab_bar_01_check);
                        mPaintImg.setImageResource(R.drawable.tab_bar_02);
                        mPersonalImg.setImageResource(R.drawable.tab_bar_03);
                        break;
                    case 1:
                        mContextImg.setImageResource(R.drawable.tab_bar_01);
                        mPaintImg.setImageResource(R.drawable.tab_bar_02_check);
                        mPersonalImg.setImageResource(R.drawable.tab_bar_03);
                        break;
                    case 2:
                        mContextImg.setImageResource(R.drawable.tab_bar_01);
                        mPaintImg.setImageResource(R.drawable.tab_bar_02);
                        mPersonalImg.setImageResource(R.drawable.tab_bar_03_check);
                    default:
                        break;
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     *  隐藏所有的Fragment
     * @param transaction：事务
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (mContext != null) {
            transaction.hide(mContext);
        }
        if (mPaint != null) {
            transaction.hide(mPaint);
        }
        if (mPersonal != null) {
            transaction.hide(mPersonal);
        }
    }

    /**
     * 动态权限申请
     */
    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.VIBRATE
        };
    }

    /**
     * 沉浸式状态栏
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));//设置状态栏背景色
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
            Toast.makeText(this, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    /**
     * 沉浸式状态栏参数
     */
    //特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useThemestatusBarColor = false;
    //状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    protected boolean useStatusBarColor = false;

    // 初始化Python环境
    public void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
    // 调用python代码
    public void callPythonCode(){
        Python py_test = Python.getInstance();
        // 等价用法：py.getModule("hello").get("greet").call("Android");
        PyObject object = py_test.getModule("getHumidity").callAttr("get",1);
        String hum = object.toJava(String.class);

        Toast.makeText(this,"当前湿度为："+ hum,Toast.LENGTH_SHORT).show();
    }
}
