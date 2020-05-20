package com.nuc.server.activity.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.a26559.firecontrol.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.idtk.smallchart.chart.LineChart;
import com.idtk.smallchart.data.LineData;
import com.idtk.smallchart.interfaces.iData.ILineData;
import com.nuc.server.config.MyUrl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PaintFragment extends Fragment{

    private View rootView;

    private String url;
    private String date;
    private Handler handler;

    private String[] temperature;
    private String[] smoke;
    private String[] time;

    private Button dateChoice;
    private SharedPreferences pref;

    private ArrayList<PointF> smokePointArrayList = new ArrayList<>();
    private ArrayList<PointF> temPointArrayList = new ArrayList<>();
    private ArrayList<ILineData> mTemperatureList = new ArrayList<>();
    private ArrayList<ILineData> mSmokeList = new ArrayList<>();

    private LineData mLineTemperature;
    private LineData mLineSmoke;
    private LineChart lineChart_temperature;
    private LineChart lineChart_smoke;
    private LinearLayout lineChart_LL_tem;
    private LinearLayout lineChart_LL_smoke;

    private float[] times;
    private float[] smokes;
    private float[] temperatures;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(null == rootView){
            rootView = inflater.inflate(R.layout.fragment_paint,container,false);
            initView(rootView);
            onclickInit();
        }
        return rootView;
    }

    /**
     * 初始化布局
     * @param rootView：xml文件
     */
    private void initView(View rootView){
        mLineTemperature = new LineData();
        mLineSmoke = new LineData();
        dateChoice = (Button) rootView.findViewById(R.id.dateChoice);
        lineChart_temperature = (LineChart)rootView.findViewById(R.id.lineChart_temperature);
        lineChart_smoke = (LineChart)rootView.findViewById(R.id.lineChart_smoke);
        lineChart_LL_tem = rootView.findViewById(R.id.lineChart_LL_tem);
        lineChart_LL_smoke = rootView.findViewById(R.id.lineChart_LL_smoke);
    }
    /**
     * 点击事件
     */
    private void onclickInit(){
        //选择日期
        dateChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择日期
                dateChoice();
            }
        });
    }

    /**
     * 选择日期并发送到服务器
     */
    private void dateChoice(){
        temPointArrayList.clear();
        smokePointArrayList.clear();
        mTemperatureList.clear();
        mSmokeList.clear();

        //设置日期数据
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        //用户选择的日期数据
        final Calendar dd = Calendar.getInstance();
        final long nowTime = calendar.getTimeInMillis();
        //年月日选择工具
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //将选择数值传给日期数据 dd
                dd.set(Calendar.YEAR, year);
                dd.set(Calendar.MONTH, month);
                dd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                date = new SimpleDateFormat("yyyy/MM/dd").format(new Date(dd.getTimeInMillis()));

                //获取LoginActivity中存储的sid
                pref = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
                final String sid = pref.getString("sid",null);

                //判断时间是否是未来
                long a = dd.getTimeInMillis() - nowTime;
                Log.d("日期格式",date);
                if (a > 0) {
                    Toast.makeText(getContext(), "失败，时间不能在将来哟~", Toast.LENGTH_SHORT).show();
                }
                //将date传给服务器端
                handler = new Handler(){
                    public void handleMessage(Message message){
                        super.handleMessage(message);
                        switch (message.what){
                            case -1:
                                Log.d("课设:主活动", "解析完毕 -1,出现错误");
                                break;
                            case 1:
                                Log.d("课设:主活动", "解析完毕 1,成功获取数据");

                                /**
                                 * 开始画图吧
                                 */
                                initTemData();
                                initSmoData();
                                break; } }};
                url = MyUrl.base+"sensor/findByDate.json";
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        GetTest(url,date,sid); //发送请求
                    }}).start(); }},mYear,mMonth,mDay);
        datePickerDialog.show();
    }
    /**
     * Get请求
     * @param url：请求路径
     * @param date：所选择的日期
     * @param sid：登录账户的传感器编号
     */
    private void GetTest(final String url,String date,String sid){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("date",date);
        formBody.add("sid",sid);
        FormBody body = formBody.build();
        Log.d(" 日期" ,date);
        Log.d(" 传感器",sid);

        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(" 主活动：数据返回", e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String callback = response.body().string();
                Log.e(" 主活动：数据成功返回",":"+callback);
                analysis(callback);
            }
        });
    }
    /**
     * 解析
     * @param result：服务器端返回结果(callback)
     */
    private void analysis(String result){

        /*
        {"status":"1",
           "data":[
             {"date":"2020/04/27","id":9,"sid":2,"smoke":12,"temperature":20,"time":"10:00"},
             {"date":"2020/04/27","id":10,"sid":2,"smoke":15,"temperature":20,"time":"12:00"},
             {"date":"2020/04/27","id":11,"sid":2,"smoke":13,"temperature":22,"time":"14:00"},
             {"date":"2020/04/27","id":12,"sid":2,"smoke":14,"temperature":21,"time":"16:00"}
        ]}
        */
        JsonParser parser = new JsonParser();  //创建JSON解析器
        JsonObject subObject = (JsonObject)parser.parse(result) ;
        Message message = new Message();
        message.what = subObject.get("status").getAsInt();
        if (message.what == -1){
        } else {
            JsonArray datas = subObject.get("data").getAsJsonArray();
        /*[{"date":"2020/04/27","id":9,"sid":2,"smoke":12,"temperature":20,"time":"10:00"},
        {"date":"2020/04/27","id":10,"sid":2,"smoke":15,"temperature":20,"time":"12:00"},
        {"date":"2020/04/27","id":11,"sid":2,"smoke":13,"temperature":22,"time":"14:00"},
        {"date":"2020/04/27","id":12,"sid":2,"smoke":14,"temperature":21,"time":"16:00"}]*/
            temperature = new String[datas.size()];
            smoke = new String[datas.size()];
            time = new String[datas.size()];
            times = new float[time.length];
            for (int i = 0;i < datas.size();i++){
                //获取第i+1行数据
                JsonObject sensorsin = datas.get(i).getAsJsonObject();
                temperature[i] = sensorsin.get("temperature").getAsString();
                smoke[i] = sensorsin.get("smoke").getAsString();
                time[i] = sensorsin.get("time").getAsString();
                times[i] = Float.parseFloat(time[i]);
            }
        }
        handler.sendMessage(message);
    }

    /**
     * 绘图
     */
    private void initSmoData() {

        smokes = new float[12];

        for(int i=0; i<smoke.length; i++){

            smokes[i] = Float.parseFloat(smoke[i]);
            Log.d("绘图：取得smokes[]", String.valueOf(smokes[i]));
        }
        for (int i = 0; i < time.length; i++) {
            smokePointArrayList.add(new PointF(times[i],smokes[i]));
        }

        mLineSmoke.setValue(smokePointArrayList);
        mLineSmoke.setColor(Color.GREEN);
        mLineSmoke.setPaintWidth(pxTodp_smoke(3));
        mLineSmoke.setTextSize(pxTodp_smoke(10));
        mSmokeList.add(mLineSmoke);
        initSmokeData();

    }
    public void initSmokeData(){
        lineChart_smoke.setDataList(mSmokeList);
        lineChart_LL_smoke.setVisibility(View.VISIBLE);
        //lineChart_temperature.notifyAll();
        //lineChart_temperature.notify();
        lineChart_smoke.invalidate();
    }
    private void initTemData() {

        temperatures = new float[temperature.length];

        for(int i=0; i<temperature.length; i++){
            temperatures[i] = Float.parseFloat(temperature[i]);
            Log.d("绘图：取得temperatures[]", String.valueOf(temperatures[i]));

        }
        for (int i = 0; i < time.length; i++) {
            temPointArrayList.add(new PointF(times[i],temperatures[i]));
        }

        mLineTemperature.setValue(temPointArrayList);
        mLineTemperature.setColor(Color.GREEN);
        mLineTemperature.setPaintWidth(pxTodp_tem(3));
        mLineTemperature.setTextSize(pxTodp_tem(10));
        mTemperatureList.add(mLineTemperature);
        initTemperatureData();

    }
    public void initTemperatureData(){
        lineChart_temperature.setDataList(mTemperatureList);
        lineChart_LL_tem.setVisibility(View.VISIBLE);
        //lineChart_temperature.notifyAll();
        //lineChart_temperature.notify();
        lineChart_temperature.invalidate();
    }

    protected float pxTodp_smoke(float value){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float valueDP= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,metrics);
        return valueDP;
    }
    protected float pxTodp_tem(float value){
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float valueDP= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,metrics);
        return valueDP;
    }
}
