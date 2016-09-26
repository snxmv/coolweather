package shixiaofei.coolweather.love.shaonianAndxmv.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import shixiaofei.coolweather.love.shaonianAndxmv.db.CoolWeatherDB;
import shixiaofei.coolweather.love.shaonianAndxmv.model.City;
import shixiaofei.coolweather.love.shaonianAndxmv.model.County;
import shixiaofei.coolweather.love.shaonianAndxmv.model.Province;

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

    /**
     * 解析服务器返回的JSON天气数据信息,并将数据存储到本地(非数据库)
     */
    public synchronized static void handleWeatherResponse(Context context, String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 天气信息存储到本地
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode,

                                        String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
