package com.nuc.server.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.a26559.firecontrol.R;
import com.nuc.server.util.view.DataView;


public class ParamActivity extends AppCompatActivity {

    private Switch touch;
    private SeekBar seekBar;
    private SeekBar seekBar_tem;
    private SeekBar seekBar_refresh;
    private TextView rate;
    private TextView bell;
    private TextView refresh;
    private int status;
    private int rateData;
    private int bellData;
    private int refreshData;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        touch = (Switch) findViewById(R.id.touch_switch);
        touch.setChecked(preferences.getInt("status",0)==1?true:false);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(preferences.getInt("rateData",100));
        seekBar_tem = (SeekBar) findViewById(R.id.seekBar_tem);
        seekBar_tem.setProgress(preferences.getInt("bellData",40));
        seekBar_refresh = (SeekBar) findViewById(R.id.seekBar_refresh);
        seekBar_refresh.setProgress(preferences.getInt("refreshData",30));
        rate = (TextView) findViewById(R.id.rate);
        bell = (TextView) findViewById(R.id.bell);
        refresh = (TextView) findViewById(R.id.refresh);

        status = 0;

        final SharedPreferences.Editor editor = preferences.edit();
        System.out.println(editor);

        touch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = 1;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("status", status);
                    editor.commit();

                } else {
                    status = 0;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("onStart-->" + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rateData = seekBar.getProgress();
                editor.putInt("rateData", rateData);
                editor.commit();
                System.out.println("onStop-->" + seekBar.getProgress() + preferences.getInt("rateData",100));
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rate.setText(String.valueOf(progress));
            }
        });
        seekBar_tem.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("onStart-->" + seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bellData = seekBar.getProgress();
                editor.putInt("bellData", bellData);
                editor.commit();
                editor.apply();
                System.out.println("onStop-->" + seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bell.setText(String.valueOf(progress));
            }
        });
        seekBar_refresh.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                System.out.println("onStart-->" + seekBar.getProgress());
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                refreshData = seekBar.getProgress();
                editor.putInt("refreshData", refreshData);
                editor.commit();
                editor.apply();
                DataView dataView = new DataView(ParamActivity.this);
                Integer s = preferences.getInt("refreshData",30);
                dataView.setSecond(s*1000);
                System.out.println("onStop-->" + seekBar.getProgress());
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                refresh.setText(String.valueOf(progress));
            }
        });
    }
}
