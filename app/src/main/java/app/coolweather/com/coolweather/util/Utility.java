package app.coolweather.com.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import app.coolweather.com.coolweather.db.CoolWeatherDB;
import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.County;
import app.coolweather.com.coolweather.model.Province;

/**
 * Created by shixiaofei on 2016/9/24.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的数据
     */
    public synchronized static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB, String response){
//        Log.e("shaonian", response);
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            for(String temp: allProvinces){
                String[] data = temp.split("\\|");
                Province province = new Province();
                province.setProvinceName(data[1]);
                province.setProvinceCode(data[0]);
                coolWeatherDB.saveProvince(province);
            }
            return true;
        }
        return false;
    }

    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response,
                                                          int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCitys = response.split(",");
            for(String temp: allCitys){
                String[] data = temp.split("\\|");
                City city = new City();
                city.setCityName(data[1]);
                city.setCityCode(data[0]);
                city.setProvinceId(provinceId);
                coolWeatherDB.saveCity(city);
            }
            return true;
        }
        return false;
    }

    public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB, String response,
                                                              int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCountys = response.split(",");
            for(String temp: allCountys){
                String[] data = temp.split("\\|");
                County county = new County();
                county.setCountyName(data[1]);
                county.setCountyCode(data[0]);
                county.setCityId(cityId);
                coolWeatherDB.saveCounty(county);
            }
            return true;
        }
        return false;
    }
}
