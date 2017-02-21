package com.ax.vertu.weatheralarmclock.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.receiver.AlarmServiceBroadcastReciever;

/**
 * Created by Z on 2015/11/16.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.middle_title_actionbar);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }


    protected void  CancelAlarmServiceBroadcastReciever(Alarm alarm){
        AlarmServiceBroadcastReciever reciever = new AlarmServiceBroadcastReciever();
        reciever.CancelAlarm(this, alarm);
    }

    /**
     * 设置闹钟服务
     */
    protected void CallAlarmServiceBroadcastReciever(Alarm alarm) {

//        Intent serviceIntent = new Intent(this, AlarmService.class);
//        this.startService(serviceIntent);
        AlarmServiceBroadcastReciever reciever = new AlarmServiceBroadcastReciever();
        reciever.setAlarm(this, alarm);
//        bindService()

//        Intent serviceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
//        this.startService(serviceIntent);
    }
}
