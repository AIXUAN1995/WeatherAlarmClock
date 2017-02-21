package com.ax.vertu.weatheralarmclock.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.ax.vertu.weatheralarmclock.R;

public class AboutActivity extends AppCompatActivity {
    TextView tv_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String text =
                "<html>" +
                "<body>" +
                "<h1>关于天气闹钟的简要说明</h1>" +
                "<h2>1.功能说明</h2>" +
                "<p>天气闹钟将闹钟和天气预报相结合，共设置了两种模式：普通闹钟模式和天气闹钟模式</p>" +
                "<p>——普通闹钟模式：与常规闹钟的使用方式相同</p>"+
                "<p>——天气闹钟模式：可以设置临界pm2.5的值，如果闹钟将要提醒时的pm2.5的监测值大于设定值，则闹钟不响，否则闹钟正常提醒</p>"+
                "<br/>" +
                "<h2>2.使用说明</h2>" +
                "<p>设置闹钟参数后点击返回按钮完成闹钟设置</p>" +
                "<p>只有选择天气闹钟模式时才能设置临界pm2.5的值</p>" +
                "<br/>" +
                "<h2>3.后续需要完善内容</h2>" +
                "<p>恢复初始设置后需要重新启动app才能完全初始化，需要进一步优化</p>" +
                "<p>目前只能支持绝大部分地级市的天气预报，需要继续细化</p>" +
                "<p>加入pm2.5预警提醒功能</p>" +
                "</body>" +
                "</html>";
        tv_about = ((TextView)findViewById(R.id.tv_about));
        tv_about.setText(Html.fromHtml(text));
        tv_about.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
