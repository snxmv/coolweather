package shixiaofei.coolweather.love.shaonianAndxmv.util;

/**
 * Created by shixiaofei on 2016/9/16.
 */
public interface HttpCallbackListener {
    void onFinish(String resp);
    void onError(Exception e);
}
