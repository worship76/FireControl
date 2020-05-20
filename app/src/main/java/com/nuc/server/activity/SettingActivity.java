package com.nuc.server.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.a26559.firecontrol.R;


public class SettingActivity extends AppCompatActivity {

    private Button param;
    private Button version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        param = (Button) findViewById(R.id.param_setting);
        version = (Button) findViewById(R.id.version_setting);

        param.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,ParamActivity.class);
                startActivity(intent);
            }
        });

        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingActivity.this,"version:1.0",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
