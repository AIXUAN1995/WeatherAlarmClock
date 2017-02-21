package com.ax.vertu.weatheralarmclock.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.activity.AlarmAlertActivity;
import com.ax.vertu.weatheralarmclock.receiver.AlarmServiceBroadcastReciever;
import com.ax.vertu.weatheralarmclock.weather.WeatherBean;

public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
        final Alarm alarm = (Alarm) intent.getExtras().getSerializable("alarm");
        final WeatherBean weatherBean = (WeatherBean) intent.getExtras().getSerializable("weatherBean");
        Intent alarmAlertActivityIntent = new Intent(getApplicationContext(), AlarmAlertActivity.class);
        alarmAlertActivityIntent.putExtra("alarm", alarm);
        alarmAlertActivityIntent.putExtra("weatherBean", weatherBean);
        alarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(alarmAlertActivityIntent);
        AlarmServiceBroadcastReciever.completeWakefulIntent(intent);
    }

}
