package shixiaofei.coolweather.love.shaonianAndxmv.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import shixiaofei.coolweather.love.shaonianAndxmv.service.AutoUpdateService;


/**
 * Created by shixiaofei on 2016/9/25.
 */

public class AutoUpdateReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, AutoUpdateService.class);
        context.startActivity(intent1);
    }
}
