package com.quweather.android.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil  {
    //定义一个与服务器进行数据交互的类从服务器中获取数据
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient(); //创建一个okhttpClient的实例
        Request request = new Request.Builder().url(address).build();  //需要一个Request对象，先提供构建者Builder 进行调用其中的url传递具体的地址值，build构建完成这就是一个有数据的requset
       // Response execute = client.newCall(request).execute(); 这个execute方法是用来请求地址后返回里面的数据
        client.newCall(request).enqueue(callback);   //这是一个回调用来接收结果的
    }
}
