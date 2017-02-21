package com.ax.vertu.weatheralarmclock.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.ax.vertu.weatheralarmclock.R;

public class ModeActivity extends AppCompatActivity {

    private String []modeNames = {"普通闹钟", "天气闹钟"};
    private String []pm25s = {"100", "200", "300", "400", "500", "600", "700"};
    private Spinner sp_mode, sp_pm25;
    private Button btn_ok;
    private int mode = 0, pm25 = 400;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
        sp_mode = (Spinner) findViewById(R.id.sp_mode);
        sp_pm25 = (Spinner) findViewById(R.id.sp_pm25);
        btn_ok = ((Button)findViewById(R.id.btn_ok));
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("AlarmClock", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("mode", mode);
                editor.putInt("pm25", pm25);
                editor.commit();
                finish();
            }
        });

        //给sp_mode绑定数据源
        ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, modeNames);
        //设置下拉列表风格
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_mode.setAdapter(adapter1);
        sp_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = position;
                //如果选中普通闹钟，禁用pm25选项
                if (position == 0){
                    sp_pm25.setEnabled(false);
                }else {
                    sp_pm25.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //给sp_pm25绑定数据源
        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, pm25s);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_pm25.setAdapter(adapter2);
        sp_pm25.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pm25 = Integer.parseInt(pm25s[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SharedPreferences sp = getSharedPreferences("AlarmClock", MODE_PRIVATE);
        mode = sp.getInt("mode", 0);
        pm25 = sp.getInt("pm25", 400);
        sp_mode.setSelection(mode);
        for (int i=0; i<pm25s.length; i++){
            if (pm25s[i].equals(pm25+"")){
                sp_pm25.setSelection(i);
                break;
            }
        }
    }
}
