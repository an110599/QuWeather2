package com.quweather.android.DataBean;

import org.litepal.crud.DataSupport;
    //这是记录省份的一个data 实体类  每一个litepal都要继承DataSupport类
public class Province extends DataSupport {
    private int id;  //每个实体类应该有的字段
    private String provinceName; //记录省份名字
    private  int provinceCode; //记录省的代号

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }

        public int getProvinceCode() {
            return provinceCode;
        }

        public void setProvinceCode(int provinceCode) {
            this.provinceCode = provinceCode;
        }
    }
