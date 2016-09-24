package app.coolweather.com.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import app.coolweather.com.coolweather.model.City;
import app.coolweather.com.coolweather.model.County;
import app.coolweather.com.coolweather.model.Province;

/**数据库管理者: 封装各种数据库操作
 * Created by shixiaofei on 2016/9/24.
 */

public class CoolWeatherDB {
    /**
     * 数据库名字和版本
     */
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB; //单例
    private SQLiteDatabase db; //用于执行增删改查操作

    /**
     * 构造方法私有化(单例模式)
     * @param context
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME
        ,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB == null){
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 写省数据到数据库
     * @param province
     */
    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 加载数据库Province信息
     * @return
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        while (cursor.moveToFirst()){
            Province province = new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }


    /**
     * 写城市数据到数据库
     * @param city
     */
    public void saveCity(City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 加载数据库City信息
     * @return
     */
    public List<City> loadCitys(){
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, null, null, null, null, null);
        while (cursor.moveToFirst()){
            City city = new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
            list.add(city);
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }


    /**
     * 写县数据到数据库
     * @param county
     */
    public void saveCounty(County county){
        if(county != null){
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County",null,values);
        }
    }

    /**
     * 加载数据库County信息
     * @return
     */
    public List<County> loadCountys(){
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, null, null, null, null, null);
        while (cursor.moveToFirst()){
            County county = new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
            list.add(county);
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
