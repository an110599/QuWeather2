package com.quweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;
        //这是一天中和自定义接收天气信息的一些类的创建
public class Weather {
    public String status;

    public Basic basic;

    public AQI aqi;

    public NOW now;

    public Suggestion suggestion;  //建议

    @SerializedName("daily_forecast")    //因为这是一个数组的集 未来几天的数据
    public List<Forecast> forecastList;
}
