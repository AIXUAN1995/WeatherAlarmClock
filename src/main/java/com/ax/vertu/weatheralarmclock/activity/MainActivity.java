package com.ax.vertu.weatheralarmclock.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.adapter.MyFragmentPagerAdapter;
import com.ax.vertu.weatheralarmclock.fragment.AlarmFragment;
import com.ax.vertu.weatheralarmclock.fragment.SettingFragment;
import com.ax.vertu.weatheralarmclock.fragment.WeatherFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private boolean isExit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("###MainActivity onCreate开始");

        viewPager = (ViewPager) findViewById(R.id.vp);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        //Fragment对象做ViewPager的数据源
        fragmentList = new ArrayList<>();
        fragmentList.add(new AlarmFragment());
        fragmentList.add(new WeatherFragment());
        fragmentList.add(new SettingFragment());
        //添加标题
        titleList = new ArrayList<>();
        titleList.add("闹钟");
        titleList.add("天气");
        titleList.add("设置");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);     //设置tab模式，当前为系统默认模式
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(0)));//添加tab选项卡
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(1)));
        tabLayout.addTab(tabLayout.newTab().setText(titleList.get(2)));

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        //给ViewPager添加适配器
        viewPager.setAdapter(adapter);
        //将TabLayout和ViewPager关联起来
        tabLayout.setupWithViewPager(viewPager);
        //给TabLayout添加适配器
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            //退出
            finish();
        }
    }


}
