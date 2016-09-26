package app.coolweather.com.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.youmi.android.AdManager;

import java.util.ArrayList;
import java.util.List;

import app.coolweather.com.coolweather.R;
import app.coolweather.com.coolweather.db.CoolWeatherDB;
import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.County;
import app.coolweather.com.coolweather.model.Province;
import app.coolweather.com.coolweather.util.HttpCallbackListener;
import app.coolweather.com.coolweather.util.HttpUtil;
import app.coolweather.com.coolweather.util.Utility;

/**
 * Created by shixiaofei on 2016/9/24.
 */

public class ChooseAreaActivity extends Activity{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;

    private ListView listView;
    private ArrayAdapter<String> adapter;

    private CoolWeatherDB coolWeatherDB;
    private List<String> datalist = new ArrayList<>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    private int currentLevel;

    private boolean isFromWeatherActivity;//是否是从WeatherActivity返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdManager.getInstance(this).init("c68c01d335da275d", "45651a14520149f0", false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        if(preferences.getBoolean("city_selected", false) && !isFromWeatherActivity){
            Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.choose_area);

        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);

        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities(selectedProvince.getId());
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties(selectedCity.getId());
                }else if(currentLevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询所有省份,优先从数据库查，如果没有则从网络获取
     */
    private void queryProvinces(){
        provinceList = coolWeatherDB.loadProvinces();
        if(provinceList.size() > 0){
            datalist.clear();
            for(Province province : provinceList){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null, "province");
        }
    }
    private void queryCities(int provinceId){
        cityList = coolWeatherDB.loadCitys(provinceId);
        if(cityList.size() > 0){
            datalist.clear();
            for(City city : cityList){
                datalist.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }
    private void queryCounties(int cityId){
        countyList = coolWeatherDB.loadCountys(cityId);
        if(countyList.size() > 0){
            datalist.clear();
            for(County county : countyList){
                datalist.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 从网络获取Province/City/County信息
     */
    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String resp) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(coolWeatherDB, resp);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(coolWeatherDB, resp,
                            selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(coolWeatherDB, resp,
                            selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities(selectedProvince.getId());
                            }else if("county".equals(type)){
                                queryCounties(selectedCity.getId());
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runonUiThread返回主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * 显示加载进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭加载进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 返回按键处理
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_PROVINCE){
            finish();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else if(currentLevel == LEVEL_COUNTY){
            queryCities(selectedProvince.getId());
        }else{
            queryCounties(selectedCity.getId());
        }
    }
}
