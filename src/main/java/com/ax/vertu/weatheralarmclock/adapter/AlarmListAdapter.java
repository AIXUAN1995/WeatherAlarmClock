package com.ax.vertu.weatheralarmclock.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.Database;
import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.activity.AlarmActivity;
import com.ax.vertu.weatheralarmclock.fragment.AlarmFragment;

/**
 * Created by Z on 2015/11/16.
 */
public class AlarmListAdapter extends BaseAdapter {
    private AlarmFragment alarmFragment;
    private AlarmActivity alarmActivity;
    private List<Alarm> alarms = new ArrayList<Alarm>();

    public static final String ALARM_FIELDS[] = {Database.COLUMN_ALARM_ACTIVE, Database.COLUMN_ALARM_TIME, Database.COLUMN_ALARM_DAYS};

    public AlarmListAdapter(AlarmActivity alarmActivity) {
        this.alarmActivity = alarmActivity;
//		Database.init(alarmActivity);
//		alarms = Database.getAll();
    }
    public AlarmListAdapter(AlarmFragment alarmFragment){
        this.alarmFragment = alarmFragment;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (null == view)
            //view = LayoutInflater.from(alarmActivity).inflate(R.layout.list_element, null);
            view = LayoutInflater.from(alarmFragment.getActivity()).inflate(R.layout.list_element, null);

        Alarm alarm = (Alarm) getItem(position);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox_alarm_active);
        checkBox.setChecked(alarm.IsAlarmActive());
        checkBox.setTag(position);
//        checkBox.setOnClickListener(alarmActivity);
        checkBox.setOnClickListener(alarmFragment);

        TextView alarmTimeView = (TextView) view.findViewById(R.id.textView_alarm_time);
        alarmTimeView.setText(alarm.getAlarmTimeString());


        TextView alarmDaysView = (TextView) view.findViewById(R.id.textView_alarm_days);

        alarmDaysView.setText(alarm.getRepeatDaysString());

        return view;
    }

    public List<Alarm> getMathAlarms() {
        return alarms;
    }

    public void setMathAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }
}
