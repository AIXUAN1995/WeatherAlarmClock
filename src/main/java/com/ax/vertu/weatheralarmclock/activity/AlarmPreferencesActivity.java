package com.ax.vertu.weatheralarmclock.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.AlarmPreference;
import com.ax.vertu.weatheralarmclock.Database;
import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.adapter.AlarmSettingItemListAdapter;

public class AlarmPreferencesActivity extends BaseActivity {

    private Alarm alarm;
    private MediaPlayer mediaPlayer;
    private ListAdapter listAdapter;
    private ListView listView;
    private CountDownTimer alarmToneTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("alarm")) {
            //更新数据
            alarm = ((Alarm) bundle.getSerializable("alarm"));
        } else {
            alarm = (new Alarm());
        }
        if (bundle != null && bundle.containsKey("adapter")) {
            setListAdapter((AlarmSettingItemListAdapter) bundle.getSerializable("adapter"));
            System.out.println("###adapter存在");
        } else {
            setListAdapter(new AlarmSettingItemListAdapter(this, alarm));
            System.out.println("###adapter不存在");
        }

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                final AlarmSettingItemListAdapter alarmPreferenceListAdapter = (AlarmSettingItemListAdapter) listAdapter;
                final AlarmPreference alarmPreference = (AlarmPreference) alarmPreferenceListAdapter.getItem(position);
                AlertDialog.Builder alert;
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                switch (alarmPreference.getType()) {
                    case BOOLEAN:
                        CheckedTextView checkedTextView = (CheckedTextView) v;
                        boolean checked = !checkedTextView.isChecked();
                        ((CheckedTextView) v).setChecked(checked);
                        switch (alarmPreference.getKey()) {
                            case ALARM_VIBRATE:
                                alarm.setVibrate(checked);
                                if (checked) {
                                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vibrator.vibrate(1000);
                                }
                                break;
                        }
                        alarmPreference.setValue(checked);
                        break;
                    case Ring:
                        alert = new AlertDialog.Builder(AlarmPreferencesActivity.this);
                        alert.setTitle(alarmPreference.getTitle());
                        CharSequence[] items = new CharSequence[alarmPreference.getOptions().length];
                        for (int i = 0; i < items.length; i++)
                            items[i] = alarmPreference.getOptions()[i];
                        alert.setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alarm.setAlarmTonePath(alarmPreferenceListAdapter.getAlarmTonePaths()[which]);
                                if (alarm.getAlarmTonePath() != null) {
                                    if (mediaPlayer == null) {
                                        mediaPlayer = new MediaPlayer();
                                    } else {
                                        if (mediaPlayer.isPlaying())
                                            mediaPlayer.stop();
                                        mediaPlayer.reset();
                                    }
                                    try {
                                        // mediaPlayer.setVolume(1.0f, 1.0f);
                                        mediaPlayer.setVolume(0.2f, 0.2f);
                                        mediaPlayer.setDataSource(AlarmPreferencesActivity.this, Uri.parse(alarm.getAlarmTonePath()));
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                                        mediaPlayer.setLooping(false);
                                        mediaPlayer.prepare();
                                        mediaPlayer.start();

                                        // Force the mediaPlayer to stop after 3
                                        // seconds...
                                        if (alarmToneTimer != null)
                                            alarmToneTimer.cancel();
                                        alarmToneTimer = new CountDownTimer(3000, 3000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {

                                            }

                                            @Override
                                            public void onFinish() {
                                                try {
                                                    if (mediaPlayer.isPlaying())
                                                        mediaPlayer.stop();
                                                } catch (Exception e) {

                                                }
                                            }
                                        };
                                        alarmToneTimer.start();
                                    } catch (Exception e) {
                                        try {
                                            if (mediaPlayer.isPlaying())
                                                mediaPlayer.stop();
                                        } catch (Exception e2) {

                                        }
                                    }
                                }
                                alarmPreferenceListAdapter.setMathAlarm(alarm);
                                alarmPreferenceListAdapter.notifyDataSetChanged();
                            }

                        });
                        alert.show();
                        break;

                    default:
                        break;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
//        String data = "data";
//        Log.d(data, alarm.getAlarmName());
//        Log.d(data, alarm.getAlarmTime().toString());
//        Log.d(data, String.valueOf(alarm.getDays().length));
//        Log.d(data, alarm.getAlarmTonePath());
//        Log.d(data, String.valueOf(alarm.getVibrate()));


//保存闹钟信息
//        int[] days = alarm.getDays();
//        if (days == null || days.length < 1) {
//            //todo: 当任何一天都不重复时,只提醒一次
//        }
        if (alarm.getAlarmTime().getTimeInMillis()<=System.currentTimeMillis()){
            alarm.getAlarmTime().setTimeInMillis(alarm.getAlarmTime().getTimeInMillis()+24*60*60*1000);
        }
        CallAlarmServiceBroadcastReciever(alarm);
        Database.init(getApplicationContext());
        if (alarm.getId() < 1) {
            Database.create(alarm);
        } else {
            Database.update(alarm);
        }
        System.out.println("###"+alarm.getAlarmTime().getTimeInMillis());
        System.out.println("###getId"+alarm.getId());
        Toast.makeText(AlarmPreferencesActivity.this, alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
        //跨activity传值,用于测试
//        Intent resultIntent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("object", alarm);
//        resultIntent.putExtras(bundle);
//        setResult(RESULT_OK, resultIntent);
        ReleaseMusicPlayer();
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("alarm", alarm);
//        outState.putSerializable("adapter", (AlarmSettingItemListAdapter) listAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            ReleaseMusicPlayer();
        } catch (Exception e) {
        }
        // setListAdapter(null);
    }


    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        getListView().setAdapter(listAdapter);

    }

    public ListView getListView() {
        if (listView == null)
            listView = (ListView) findViewById(android.R.id.list);
        return listView;
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    private void ReleaseMusicPlayer() {
        if (mediaPlayer != null)
            mediaPlayer.release();
        mediaPlayer = null;
    }
}


