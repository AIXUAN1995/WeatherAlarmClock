package com.ax.vertu.weatheralarmclock.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ax.vertu.weatheralarmclock.R;
import com.ax.vertu.weatheralarmclock.activity.SelectCityActivity;
import com.ax.vertu.weatheralarmclock.tools.NetworkTool;
import com.ax.vertu.weatheralarmclock.weather.GetDataListener;
import com.ax.vertu.weatheralarmclock.weather.GetDataTask;
import com.ax.vertu.weatheralarmclock.weather.WeatherBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment implements GetDataListener{

    private static final int NO_NETWORK = 0;
    private static final int HAVE_NETWORK = 1;
    public static final int REQUEST_CITY = 1;
    private WeatherBean weatherBean;
    private GetDataTask getDataTask;
    private String city;
    private TextView tv_city;
    private ImageView iv_refresh;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case NO_NETWORK:
                    ((LinearLayout) getActivity().findViewById(R.id.ll_weather)).setVisibility(View.GONE);
                    ((ImageView) getActivity().findViewById(R.id.iv)).setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                case HAVE_NETWORK:
                    SharedPreferences sp = getActivity().getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
                    city = sp.getString("city", "北京");
                    //获取天气数据
                    //URL url = new URL("http://api.map.baidu.com/telematics/v3/weather?location=%E4%BF%9D%E5%AE%9A&output=json&ak=hAACaiVdsqQhtC02PG8S17uw35uNXxET&mcode=84:7B:58:F2:07:BD:88:08:69:F9:4E:AE:F4:37:13:A8:48:A8:3C:10;com.ax.vertu.weatheralarmclock");
                    updateWeather(city);
                    break;
            }
        }
    };

    public WeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.weather, null, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!NetworkTool.ping())
                    mHandler.sendEmptyMessage(NO_NETWORK);
                else
                    mHandler.sendEmptyMessage(HAVE_NETWORK);
            }
        }).start();
/*
        //如果网络连接可用
        if (NetworkTool.ping()) {
            SharedPreferences sp = getActivity().getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
            city = sp.getString("city", "北京");
            //获取天气数据
            //URL url = new URL("http://api.map.baidu.com/telematics/v3/weather?location=%E4%BF%9D%E5%AE%9A&output=json&ak=hAACaiVdsqQhtC02PG8S17uw35uNXxET&mcode=84:7B:58:F2:07:BD:88:08:69:F9:4E:AE:F4:37:13:A8:48:A8:3C:10;com.ax.vertu.weatheralarmclock");
            updateWeather(city);
        } else {
            ((LinearLayout) getActivity().findViewById(R.id.ll_weather)).setVisibility(View.GONE);
            ((ImageView) getActivity().findViewById(R.id.iv)).setVisibility(View.VISIBLE);
        }
*/
        tv_city = (TextView) getActivity().findViewById(R.id.tv_city);
        iv_refresh = (ImageView) getActivity().findViewById(R.id.iv_refresh);
        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCityActivity.class);
                startActivityForResult(intent, REQUEST_CITY);
            }
        });
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getActivity().getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
                city = sp.getString("city", "北京");
                updateWeather(city);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SelectCityActivity.RESULTCODE && data!=null){
            city = data.getExtras().getString("city");
            updateWeather(city);
            SharedPreferences sp = getActivity().getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("city", city);
            editor.commit();
        }
    }

    public void updateWeather(String city){
        try {
            URL url = null;
            try {
                url = new URL("http://api.map.baidu.com/telematics/v3/weather?location="+URLEncoder.encode(city, "utf-8")+"&output=json&ak=hAACaiVdsqQhtC02PG8S17uw35uNXxET&mcode=84:7B:58:F2:07:BD:88:08:69:F9:4E:AE:F4:37:13:A8:48:A8:3C:10;com.ax.vertu.weatheralarmclock");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            getDataTask = new GetDataTask(url, this);
            getDataTask.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //解析json数据函数
    public WeatherBean parseJson(String data) {
        WeatherBean weatherBean = new WeatherBean();
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray arrResults = jsonObject.getJSONArray("results");
            if (arrResults.length() > 0) {
                JSONObject result1 = arrResults.getJSONObject(0);
                weatherBean.setCity(result1.optString("currentCity"));
                weatherBean.setPm25(result1.optInt("pm25"));

                //获得weather_data中天气数据
                JSONArray arrWeather = result1.getJSONArray("weather_data");
                JSONObject weatherData1 = arrWeather.getJSONObject(0);
                String date = weatherData1.optString("date");
                //截取实时温度
                int start = date.indexOf("：");
                int end = date.indexOf(")");
                weatherBean.setTemperatuerNow(date.substring(start + 1, end));
                weatherBean.setDate(date.substring(0, 9));
                weatherBean.setWeather(weatherData1.optString("weather"));
                weatherBean.setWind(weatherData1.optString("wind"));
                weatherBean.setTemperature(weatherData1.optString("temperature"));
            }
            return weatherBean;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void getData(String data) {
        weatherBean = parseJson(data);
        if (weatherBean == null)
            return;
        ((TextView) getActivity().findViewById(R.id.tv_tempNow)).setText(weatherBean.getTemperatuerNow());
        ((TextView) getActivity().findViewById(R.id.tv_weather)).setText(weatherBean.getWeather());
        //根据天气设置背景
        String[] weather = weatherBean.getWeather().split("转");
        LinearLayout ll_weather = (LinearLayout) getActivity().findViewById(R.id.ll_weather);
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

        ((TextView) getActivity().findViewById(R.id.tv_city)).setText(weatherBean.getCity());
        int pm25 = weatherBean.getPm25();
        TextView tv_pm25 = (TextView) getActivity().findViewById(R.id.tv_pm25);
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

        ((TextView) getActivity().findViewById(R.id.tv_temperature)).setText(weatherBean.getTemperature());
        ((TextView) getActivity().findViewById(R.id.tv_date)).setText(weatherBean.getDate());
        ((TextView) getActivity().findViewById(R.id.tv_wind)).setText(weatherBean.getWind());
        ((TextView) getActivity().findViewById(R.id.tv_time)).setText("更新时间 "+getTime());
    }

    public String getTime(){
        long time=System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(time);
        String s_time=format.format(date);
        return s_time;
    }
}
