package app.coolweather.com.coolweather.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import app.coolweather.com.coolweather.receiver.AutoUpdateReceiver;
import app.coolweather.com.coolweather.util.HttpCallbackListener;
import app.coolweather.com.coolweather.util.HttpUtil;
import app.coolweather.com.coolweather.util.Utility;

/**
 * Created by shixiaofei on 2016/9/25.
 */

public class AutoUpdateService extends IntentService{

    public AutoUpdateService() {
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int during_time = 8 * 60 * 60 *1000;
        long triggerTime = SystemClock.elapsedRealtime() + during_time;
        Intent intent = new Intent(AutoUpdateService.this, AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateWeather();
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String resp) {
                Utility.handleWeatherResponse(AutoUpdateService.this, resp);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
