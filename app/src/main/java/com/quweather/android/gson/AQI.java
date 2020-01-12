package com.quweather.android.gson;

public class AQI {  //AQI(Air Quality Index,空气质量指数)是报告每日空气质量的参数

        public AQICity city;

        public class AQICity {

            public String aqi;

            public String pm25;

        }

    }
