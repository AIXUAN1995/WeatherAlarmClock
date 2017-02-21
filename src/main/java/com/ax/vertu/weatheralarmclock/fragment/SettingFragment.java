package com.ax.vertu.weatheralarmclock.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.activity.AboutActivity;
import com.ax.vertu.weatheralarmclock.activity.ModeActivity;
import com.ax.vertu.weatheralarmclock.adapter.SettingListViewAdapter;
import com.ax.vertu.weatheralarmclock.tools.MyActivityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    private List<Map<String, Object>> data;
    private int []itemIcons = {R.drawable.mode, R.drawable.recover, R.drawable.about};
    private String []itemNames = {"闹钟模式", "恢复初始设置", "关于"};
    private MyActivityClass[] activityClasss = {
            new MyActivityClass(ModeActivity.class),
            new MyActivityClass(AboutActivity.class),
            new MyActivityClass(AboutActivity.class)};
    private ListView lv_setting;
    private SettingListViewAdapter adapter;

    public SettingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv_setting = (ListView) getActivity().findViewById(R.id.lv_setting);
        data = getData();
        adapter = new SettingListViewAdapter(getActivity(), data);
        lv_setting.setAdapter(adapter);
        lv_setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setIcon(R.drawable.alert);
                    builder.setTitle("恢复设置");
                    builder.setMessage("确认要恢复默认设置吗");
                    builder.setPositiveButton("恢复", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sp = getActivity().getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("mode", 0);
                            editor.putInt("pm25", 400);
                            editor.putString("city", "北京");
                            editor.commit();
                            //重启应用
                            /*final Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);*/

                            Toast.makeText(getActivity(), "恢复默认设置成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                    return;

                }
                Intent intent = new Intent(getActivity(), activityClasss[position].getMyActivityClass());
                startActivity(intent);
            }
        });
    }

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i=0; i<itemNames.length; i++){
            map = new HashMap<String, Object>();
            map.put("icon", itemIcons[i]);
            map.put("name", itemNames[i]);
            list.add(map);
        }
        return list;
    }
}
