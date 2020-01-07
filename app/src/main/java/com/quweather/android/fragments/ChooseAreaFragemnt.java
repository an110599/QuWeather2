package com.quweather.android.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.quweather.android.DataBean.City;
import com.quweather.android.DataBean.County;
import com.quweather.android.DataBean.Province;
import com.quweather.android.R;
import com.quweather.android.utils.HttpUtil;
import com.quweather.android.utils.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragemnt extends Fragment {
    public static final int LEVEL_PROVINCE = 0; //用来记录返回等级，好点击返回进行判断
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;  //进度对话框
    private Button mBackButton;                //三个实例化
    private TextView mTitleText;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;          //数组适配器
    private List<String> dataList = new ArrayList<>();  //给适配器的添加的数据*n
    private List<Province> mProvinces; //省的list集合
    private List<City> mCities;   //
    private List<County> mCounties;  //
    private Province selectedProvince; //选中的省份，用来显示和判断
    private City selectedCity;
    private int currentLevel; //当前选择的等级 与前面做对比 来判断

    @Nullable
    @Override      //实例化里面的控件
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        mBackButton = view.findViewById(R.id.back_button);
        mTitleText = view.findViewById(R.id.title_text);
        mListView = view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mAdapter);
        return view;
    }
        //对控件的事件的视图的修改
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这是用来判断当前是否为省级的，如果为省级,就展示出市级的数据
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinces.get(position);
                    queryCities();
                }
                    else if (currentLevel ==LEVEL_CITY){
                        selectedCity = mCities.get(position);
                        queryCounties();
                    }
            }
        });
         //设置单击事件，一个按钮控制返回如果 为县级就返回至上一级市 ，如果判断为市就返回至上一级省
          mBackButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (currentLevel == LEVEL_COUNTY){
                      queryCities(); //市
                  }
                  else if (currentLevel == LEVEL_CITY){
                      queryProvinces();  //省
                  }

              }
          });
          queryProvinces();  //一开始加载视图就要执行这条省的数据语句（也就是初始界面）
    }
        //查询全国所有的省，有限从数据库中查询，如果没有查询到在到数据库上查询到在传给数据库 在调用此方法
    private void queryProvinces() {
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE); //因为为首页所以不显示返回按钮
        mProvinces = DataSupport.findAll(Province.class); //这里直接加载所有省数据
        //进行判空处理
        if (mProvinces.size()>0){
            dataList.clear(); //清楚当前的数据资源用来装当前的省数据
            for(Province province:mProvinces){
                dataList.add(province.getProvinceName()); //添加里面的省的名字
            }
            mAdapter.notifyDataSetChanged(); //通知更改数据
            mListView.setSelection(0);
        }
        else {
            // 在服务器段获取
            //知识一个JSON的数据打开直接看到数据 需要在这里面获取
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
                //这是市级数据
    private void queryCities() {
        mTitleText.setText(selectedProvince.getProvinceName()); //设置省份名，因为省份对应这市级
        mBackButton.setVisibility(View.VISIBLE); //设置可见
        mCities = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (mCities.size()>0){
            dataList.clear();
            for (City city : mCities) {
                dataList.add(city.getCityname());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_CITY; //设置当前等级 用于返回判断
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;  //根据当前悬着的省级代码获取市级
            queryFromServer(address,"city");
        }
    }
    private void queryCounties() {
        mTitleText.setText(selectedCity.getCityname());
        mBackButton.setVisibility(View.VISIBLE);
        mCounties = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (mCounties.size()>0){
            dataList.clear();
            for (County county : mCounties) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");

        }

    }

    private void queryFromServer(String address, final String type) {
        showProgreeDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            //回调产生的两个方法一个是请求失败，一个是回应数据
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgreeDialog();
                        Toast.makeText(getContext(),"加载失败！！！",Toast.LENGTH_SHORT).show();}
                });
            }
            //这是对网址请求后返回的数据回应 其中 response 就是待解析的 对象
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();  // 获取到的具体内容
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);  //这里面调用我们创建的类中来解析里面的内容并保存在数据库中 并返回一个布尔值
                }
                else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId()); //多了一个省份的id值 用来对应一省份 对应多市
                }
                else if("county".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedCity.getId());
                }
                // 如果成功在网络上获取到数据，将数据装换成具体内容并保存在对应数据库上  更改result的值 执行下面对应的方法
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgreeDialog();
                            if ("province".equals(type)){ //如果传递的为省就执行
                                queryProvinces();
                            }
                            else if("city".equals(type)){
                                queryCities();
                            }
                            else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });

                }



            }
        });
    }
            //进度框的关闭
    private void closeProgreeDialog() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
        //进度框的展示
    private void showProgreeDialog(){
        if (mProgressDialog==null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setTitle("获取资源中");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

}
