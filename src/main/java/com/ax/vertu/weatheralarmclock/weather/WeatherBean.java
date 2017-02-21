package com.ax.vertu.weatheralarmclock.weather;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by VERTU on 2017/1/5.
 */
public class WeatherBean implements Serializable{
    private String city;            //城市
    private int pm25;               //pm2.5
    private String weather;         //天气
    private String date;            //日期
    private String wind;            //风
    private String temperature;     //温度
    private String temperatuerNow;  //当前温度

    public String getTemperatuerNow() {
        return temperatuerNow;
    }

    public void setTemperatuerNow(String temperatuerNow) {
        this.temperatuerNow = temperatuerNow;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }


    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

}
