package com.ax.vertu.weatheralarmclock.receiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.ax.vertu.weatheralarmclock.Alarm;
import com.ax.vertu.weatheralarmclock.Database;
import com.ax.vertu.weatheralarmclock.service.SchedulingService;
import com.ax.vertu.weatheralarmclock.tools.NetworkTool;
import com.ax.vertu.weatheralarmclock.weather.GetDataListener;
import com.ax.vertu.weatheralarmclock.weather.GetDataTask;
import com.ax.vertu.weatheralarmclock.weather.WeatherBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class AlarmServiceBroadcastReciever extends WakefulBroadcastReceiver implements GetDataListener{

    private Alarm alarm;
    private WeatherBean weatherBean = null;

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private GetDataTask getDataTask;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
        try {
            //            11-27 13:43:39.020 14674-14674/zeusro.specialalarmclock D/TEST: null
//            11-27 13:43:39.020 14674-14674/zeusro.specialalarmclock D/TEST: android.intent.extra.ALARM_COUNT
//            11-27 13:43:39.020 14674-14674/zeusro.specialalarmclock D/TEST: 1
//            11-27 13:43:39.020 14674-14674/zeusro.specialalarmclock D/AlarmServiceBroadcastReciever: false
//            Log.d("TEST", String.valueOf(intent.getAction()));
//            Bundle bundle = intent.getExtras();

//            Set<String> keySet = bundle.keySet();
//            for (String key : keySet) {
//                Object value = bundle.get(key);
//                Log.d("TEST", key.toString());
//                Log.d("TEST", value.toString());
//            }
//            Log.d(this.getClass().getSimpleName(), String.valueOf(bundle.getSerializable("alarm") != null));
//            alarm = (Alarm) bundle.getSerializable("alarm");
//            if (alarm == null)
//                throw new Exception("参数没有啊混蛋");
            //            service.putExtra("alarm", alarm);
            // FIXME: 2015/11/27 通过对象传递找到对象而不是查数据库
//            Log.d(this.getClass().getSimpleName(), String.valueOf( intent.getSerializableExtra("alarm") != null));
//            alarm = (Alarm) intent.getSerializableExtra("alarm");

            this.context = context;
            alarm = getNext(context);
            //如果网络连接可用
            if (NetworkTool.ping()) {
                try {
                    SharedPreferences sp = context.getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
                    String city = sp.getString("city", "北京");
                    //获取天气数据
                    URL url = new URL("http://api.map.baidu.com/telematics/v3/weather?location="+ URLEncoder.encode(city, "utf-8")+"&output=json&ak=hAACaiVdsqQhtC02PG8S17uw35uNXxET&mcode=84:7B:58:F2:07:BD:88:08:69:F9:4E:AE:F4:37:13:A8:48:A8:3C:10;com.ax.vertu.weatheralarmclock");
                    getDataTask = new GetDataTask(url, this);
                    getDataTask.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }else {
                Intent service = new Intent(context, SchedulingService.class);
                service.putExtra("alarm", alarm);
                service.putExtra("weatherBean", weatherBean);

                // Start the service, keeping the device awake while it is launching.
                startWakefulService(context, service);
                setResultCode(Activity.RESULT_OK);
            }

        } catch (Exception e) {
            Log.wtf("WTF", e);
        }
    }


    public void setAlarm(Context context, Alarm alarm) {
        if (alarm == null)
            alarm = getNext(context);
        if (alarm == null) {
            Log.d(context.getPackageName(), "没有闹钟");
            //CancelAlarm(context, alarm);
            return;
        }
        alarm.setAlarmActive(true);
        Intent intent = new Intent(context, AlarmServiceBroadcastReciever.class);
        alarmIntent = PendingIntent.getBroadcast(context, alarm.getAlarmRequestCode(), intent, 0);
        System.out.println("###闹钟已设置requestCode"+alarm.getAlarmRequestCode());
        intent.setAction("com.ax.vertu.weatheralarmclock.action.alert");
        Calendar calendar = alarm.getAlarmTime();
/*        Calendar now = (Calendar) calendar.clone();
        now.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        now.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
        now.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            CancelAlarm(context);
            return;
        }*/

        //判断闹钟时间是否大于当前时间
        if (calendar.getTimeInMillis()<System.currentTimeMillis()){
            calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
            alarm.setAlarmTime(calendar);
        }
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            Log.d(this.getClass().getSimpleName(), calendar.getTime().toString());
//            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),  AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        System.out.println("###闹钟启动requestCode"+alarm.getAlarmRequestCode());
        System.out.println("###闹钟启动getId"+alarm.getId());
        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        //可用状态
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    /**
     * Cancels the alarm.
     *
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void CancelAlarm(Context context, Alarm alarm) {
        alarm.setAlarmActive(false);
        if (null == alarmIntent) {
            Intent intent = new Intent(context, AlarmServiceBroadcastReciever.class);
            alarmIntent = PendingIntent.getBroadcast(context, alarm.getAlarmRequestCode(), intent, 0);
        }
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }else{
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(alarmIntent);
            System.out.println("###闹钟已取消"+alarm.getAlarmRequestCode());
        }
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    private Alarm getNext(Context context) {
        Set<Alarm> alarmQueue = new TreeSet<Alarm>(new Comparator<Alarm>() {
            @Override
            public int compare(Alarm lhs, Alarm rhs) {
                int result = 0;
                long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return result;
            }
        });
        Database.init(context);
        List<Alarm> alarms = Database.getAll();
        for (Alarm alarm : alarms) {
            if (alarm.IsAlarmActive())
                alarmQueue.add(alarm);
        }
        if (alarmQueue.iterator().hasNext()) {
            return alarmQueue.iterator().next();
        } else {
            return null;
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
        SharedPreferences sp = context.getSharedPreferences("AlarmClock", Context.MODE_PRIVATE);
        int pm25 = sp.getInt("pm25", 400);
        if (pm25 > weatherBean.getPm25()) {    //如果pm2.5没有达到设定值则闹钟正常提醒
            Intent service = new Intent(context, SchedulingService.class);
            service.putExtra("alarm", alarm);
            service.putExtra("weatherBean", weatherBean);
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, service);
            setResultCode(Activity.RESULT_OK);
        }
    }
}
