package com.quweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class NOW { //当前 温度，天气类型
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {

        @SerializedName("txt")
        public String info;

    }

}
