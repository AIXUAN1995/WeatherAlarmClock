package com.ax.vertu.weatheralarmclock.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.StaticWakeLock;
import com.ax.vertu.weatheralarmclock.receiver.AlarmServiceBroadcastReciever;
import com.ax.vertu.weatheralarmclock.view.SlideView;
import com.ax.vertu.weatheralarmclock.weather.WeatherBean;

public class AlarmAlertActivity extends AppCompatActivity implements View.OnClickListener {
    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean alarmActive;
    private WeatherBean weatherBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //锁屏弹出Activity
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.alert);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            alarm = (Alarm) bundle.getSerializable("alarm");
            weatherBean = (WeatherBean)bundle.getSerializable("weatherBean");
            if (null != alarm) {
                this.setTitle(alarm.getAlarmName());
                startAlarm();
            }
        }
        SetSlideView();
        SetTelephonyStateChangedListener();

        //如果网络连接可用,获得了天气数据
        if (weatherBean != null) {
            ((TextView) findViewById(R.id.tv_tempNow)).setText(weatherBean.getTemperatuerNow());
            ((TextView) findViewById(R.id.tv_weather)).setText(weatherBean.getWeather());
            //根据天气设置背景
            String[] weather = weatherBean.getWeather().split("转");
            LinearLayout ll_weather = (LinearLayout) findViewById(R.id.ll_weather);
            Toast.makeText(AlarmAlertActivity.this, weather[0], Toast.LENGTH_SHORT).show();
            if (weather[0].contains("晴")) {
                ll_weather.setBackgroundResource(R.drawable.bg_sunny);
            } else if (weather[0].contains("阴")) {
                ll_weather.setBackgroundResource(R.drawable.bg_overcast);
            } else if (weather[0].contains("多云")) {
                ll_weather.setBackgroundResource(R.drawable.bg_cloudy);
            } else if (weather[0].contains("雷")) {
                ll_weather.setBackgroundResource(R.drawable.bg_thunder);
            } else if (weather[0].contains("雨")) {
                ll_weather.setBackgroundResource(R.drawable.bg_rainy);
            } else if (weather[0].contains("雪")) {
                ll_weather.setBackgroundResource(R.drawable.bg_snowy);
            } else if (weather[0].contains("雾") || weather[0].contains("霾")) {
                ll_weather.setBackgroundResource(R.drawable.bg_fog_and_haze);
            } else {
                ll_weather.setBackgroundResource(R.drawable.bg);
            }

            ((TextView) findViewById(R.id.tv_city)).setText(weatherBean.getCity());
            int pm25 = weatherBean.getPm25();
            TextView tv_pm25 = (TextView) findViewById(R.id.tv_pm25);
            //根据pm2.5判断空气质量
            if (pm25 >= 0 && pm25 <= 50) {           //优
                tv_pm25.setText("PM2.5 " + pm25 + " 优");
                //tv_pm25.setTextColor(Color.WHITE);
            } else if (pm25 > 50 && pm25 <= 100) {     //良
                tv_pm25.setText("PM2.5 " + pm25 + " 良");
            } else if (pm25 > 100 && pm25 <= 150) {     //轻度污染
                tv_pm25.setText("PM2.5 " + pm25 + " 轻度污染");
            } else if (pm25 > 150 && pm25 <= 200) {    //中度污染
                tv_pm25.setText("PM2.5 " + pm25 + " 中度污染");
                tv_pm25.setTextColor(Color.parseColor("#ffff00"));
            } else if (pm25 > 200 && pm25 <= 300) {    //重度污染
                tv_pm25.setText("PM2.5 " + pm25 + " 重度污染");
                tv_pm25.setTextColor(Color.parseColor("#ffa500"));
            } else if (pm25 > 300) {                 //严重污染
                tv_pm25.setText("PM2.5 " + pm25 + " 严重污染");
                tv_pm25.setTextColor(Color.parseColor("#ff0000"));
            }

            ((TextView) findViewById(R.id.tv_temperature)).setText(weatherBean.getTemperature());
            ((TextView) findViewById(R.id.tv_date)).setText(weatherBean.getDate());
            ((TextView) findViewById(R.id.tv_wind)).setText(weatherBean.getWind());
        } else {
            ((LinearLayout) findViewById(R.id.ll_weather)).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.iv)).setVisibility(View.VISIBLE);
        }

    }

    private void SetTelephonyStateChangedListener() {
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(getClass().getSimpleName(), "Incoming call: " + incomingNumber);
                        try {
                            mediaPlayer.pause();
                        } catch (IllegalStateException e) {

                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(getClass().getSimpleName(), "Call State Idle");
                        try {
                            mediaPlayer.start();
                        } catch (IllegalStateException e) {

                        }
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void SetSlideView() {
        SlideView slideView = (SlideView) findViewById(R.id.slider);
        slideView.setSlideListener(new SlideView.SlideListener() {
            @Override
            public void onDone() {
                AlarmServiceBroadcastReciever reciever = new AlarmServiceBroadcastReciever();
                reciever.CancelAlarm(AlarmAlertActivity.this, alarm);
                ReleaseRelease();
                Toast.makeText(AlarmAlertActivity.this, "早起啦", Toast.LENGTH_SHORT).show();
                Log.d("SHIT", String.valueOf(Build.VERSION.SDK_INT));
                if (Build.VERSION.SDK_INT > 15) {
                    quit();
                    return;
                }
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
                System.exit(0);

            }
        });
    }

    @TargetApi(16)
    protected void quit() {
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmActive = true;
    }

    private void startAlarm() {
        if (!alarm.getAlarmTonePath().equals("")) {
            mediaPlayer = new MediaPlayer();
            if (alarm.IsVibrate()) {
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                long[] pattern = {1000, 200, 200, 200};
                vibrator.vibrate(pattern, 0);
            }
            try {
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.setDataSource(this, Uri.parse(alarm.getAlarmTonePath()));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                mediaPlayer.release();
                alarmActive = false;
            }
        }

    }

    /**
     * 禁止返回取消闹钟
     */
    @Override
    public void onBackPressed() {
        if (!alarmActive)
            super.onBackPressed();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        StaticWakeLock.lockOff(this);
    }

    protected void ReleaseRelease() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }
        super.onDestroy();
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
