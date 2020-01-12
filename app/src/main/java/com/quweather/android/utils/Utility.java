package com.quweather.android.utils;

import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quweather.android.DataBean.City;
import com.quweather.android.DataBean.County;
import com.quweather.android.DataBean.Province;
import com.quweather.android.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//用来解析和处理服务器返回的省级数据（Json）
public class Utility  {
    private static final String TAG = "ChooseAreaFragment";
    public static boolean handleProvinceResponse(String response){

        //当里面的不为null或者空时，就吧JSON数组进行便利成JSOn对象然后将对象中对应的键-->值获取出来 在设置在DataBean中里面的省份（province）
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();  //保存在数据库中 ，一直存在不覆盖
                    Log.d(TAG,"province的——————————"+i);
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }


    ///======================返回的市级数据==========================

    public static boolean handleCityResponse(String response,int proviceId){
        //同理 加了一个对应的省级id
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityname(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(proviceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return false;
    }

    ///======================返回的县级数据==========================

    public static boolean handleCountyResponse(String response,int proviceId){
        //同理 加了一个对应的市级id来对应
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setCityId(countyObject.getInt("id"));
                    county.setCityId(proviceId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        return false;
    }
  /*  和前面的不同 他是吧一个整体的数据传入一个JSON的对象中
     让后在在所有数据的对象中获取指定的数组 在装入数组中最后在获取数组元素的
     第一值就是指定的值 然后新建一个Gson对象将json数据传入进去gson进行封装
     然后传给我们自定义的实体类Weaher中
    */
        public static Weather handlerWeatherResponse(String respone){
        try {
            JSONObject jsonObject = new JSONObject(respone);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
