package com.nuc.server.activity.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a26559.firecontrol.R;
import com.nuc.server.activity.MainActivity;
import com.nuc.server.util.view.DataView;

public class ContextFragment extends Fragment {

    public View rootView;
    private SharedPreferences preferences;
    private Integer tem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(null == rootView){
            rootView = inflater.inflate(R.layout.fragment_context,container,false);
            initView(rootView);
        }
        initView(rootView);
        initData();
        return rootView;
    }

    private void initView(View rootView){
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getContext());
        tem = preferences.getInt("tem",30);
    }
    private void initData(){
        DataView dataView = rootView.findViewById(R.id.temperatureView);
        dataView.setCurrentTemp(tem);
    }

}
