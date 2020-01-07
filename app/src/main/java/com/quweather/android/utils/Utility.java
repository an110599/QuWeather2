package com.quweather.android.utils;

import android.text.TextUtils;

import com.quweather.android.DataBean.City;
import com.quweather.android.DataBean.County;
import com.quweather.android.DataBean.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//用来解析和处理服务器返回的省级数据（Json）
public class Utility  {
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
}
