package com.ax.vertu.weatheralarmclock.weather;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by VERTU on 2017/1/6.
 */
public class GetPictureTask extends AsyncTask<String, Void, Void> {
    private WeatherBean weatherBean;
    private String dayPictureUrl;
    private String nightPictureUrl;
    GetDataListener listener;

    public GetPictureTask(WeatherBean weatherBean, String dayPictureUrl, String nightPictureUrl, GetDataListener listener) {
        this.weatherBean = weatherBean;
        this.dayPictureUrl = dayPictureUrl;
        this.nightPictureUrl = nightPictureUrl;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... params) {
//        weatherBean.setDayPicture(getHttpBitmap(dayPictureUrl));
//        weatherBean.setNightPicture(getHttpBitmap(nightPictureUrl));
        return null;
    }

    /**
     * 获取网落图片资源
     *
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileURL;
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        listener.getPicture();
        super.onPostExecute(aVoid);
    }
}
