package com.ax.vertu.weatheralarmclock.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.Database;
import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.activity.AlarmPreferencesActivity;
import com.ax.vertu.weatheralarmclock.adapter.AlarmListAdapter;
import com.ax.vertu.weatheralarmclock.receiver.AlarmServiceBroadcastReciever;
import com.ax.vertu.weatheralarmclock.receiver.NotificationWakeUpReceiver;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener {
    AlarmListAdapter alarmListAdapter;
    ListView mathAlarmListView;
    ImageButton add, setting;
    public final static int notificationId = 1;

    public AlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, null, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SetlistView();
        SetAddButton();
        SetSettingButton();
    }

    @Override
    public void onPause() {
        Database.deactivate();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("###onResume");
        updateAlarmList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("activity","onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", String.valueOf(resultCode));
        switch (resultCode) {
            case Activity.RESULT_OK:
                Bundle b = data.getExtras();
                Alarm alarm = (Alarm) b.getSerializable("object");//回传的值
                if (alarm != null) {
                    Log.d("data", alarm.getAlarmName());
                }
                break;
            default:
                break;
        }
    }

    private void SetAddButton() {
        add = (ImageButton) getActivity().findViewById(R.id.Add);
        if (add != null) {
            add.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent newAlarmIntent = new Intent(getActivity().getApplicationContext(), AlarmPreferencesActivity.class);
                    startActivityForResult(newAlarmIntent, 0);
//                    startActivity(newAlarmIntent);
                }

            });
        }
    }


    private void SetlistView() {
        mathAlarmListView = (ListView) getActivity().findViewById(R.id.listView);
        if (mathAlarmListView != null) {
            mathAlarmListView.setLongClickable(true);
            mathAlarmListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("删除");
                    dialog.setMessage("删除这个闹钟?");
                    dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Database.init(getActivity());
                            Database.deleteEntry(alarm);
                            //取消
                            CancelAlarmServiceBroadcastReciever(alarm);
                            updateAlarmList();
                        }
                    });
                    dialog.show();
                    return true;
                }
            });
            CallAlarmServiceBroadcastReciever(null);
            alarmListAdapter = new AlarmListAdapter(this);
            this.mathAlarmListView.setAdapter(alarmListAdapter);
            mathAlarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), AlarmPreferencesActivity.class);
                    intent.putExtra("alarm", alarm);
                    startActivityForResult(intent, 0);
                }

            });
        }
    }


    public void updateAlarmList() {
        Database.init(getActivity());
        final List<Alarm> alarms = Database.getAll();
        if (alarmListAdapter != null)
            alarmListAdapter.setMathAlarms(alarms);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // reload content
                if (alarmListAdapter != null)
                    alarmListAdapter.notifyDataSetChanged();
                TextView text = (TextView) getActivity().findViewById(R.id.textView);
                if (alarms != null && alarms.size() > 0) {
                    text.setVisibility(View.GONE);
                } else {
                    text.setText(R.string.NoClockAlert);
                    text.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void SetSettingButton() {
        setting = (ImageButton) getActivity().findViewById(R.id.Setting);
        if (setting != null) {
            setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CreateNotification(null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.mipmap.app_icon);
                    builder.setTitle("选择闹钟模式");
                    final String []mode = {"普通模式", "天气闹钟"};
                    builder.setItems(mode, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sp = getActivity().getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("mode", which);
                            editor.commit();
                            Toast.makeText(getActivity(), "闹钟已经设置为 "+ mode[which], Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }

            });
        }
    }

    private void CreateNotification(Alarm alarm) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), NotificationWakeUpReceiver.class);
        getActivity().sendBroadcast(intent);//发送广播事件
    }

    protected void CancelAlarmServiceBroadcastReciever(Alarm alarm) {
        AlarmServiceBroadcastReciever reciever = new AlarmServiceBroadcastReciever();
        reciever.CancelAlarm(getActivity(), alarm);
    }

    /**
     * 设置闹钟服务
     */
    protected void CallAlarmServiceBroadcastReciever(Alarm alarm) {

//        Intent serviceIntent = new Intent(this, AlarmService.class);
//        this.startService(serviceIntent);
        AlarmServiceBroadcastReciever reciever = new AlarmServiceBroadcastReciever();
        reciever.setAlarm(getActivity(), alarm);
//        bindService()

//        Intent serviceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
//        this.startService(serviceIntent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkBox_alarm_active) {
            CheckBox checkBox = (CheckBox) v;
            Alarm alarm = (Alarm) alarmListAdapter.getItem((Integer) checkBox.getTag());
            alarm.setAlarmActive(checkBox.isChecked());
            Database.update(alarm);
            if (checkBox.isChecked()) {
                CallAlarmServiceBroadcastReciever(alarm);
                Toast.makeText(getActivity(), alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
            } else {
                CancelAlarmServiceBroadcastReciever(alarm);
                Toast.makeText(getActivity(), "闹钟已取消", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
