package app.coolweather.com.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shixiaofei on 2016/9/16.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address,
                                         final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String line;
                    StringBuilder resp = new StringBuilder();
                    while ((line=br.readLine()) != null){
                        resp.append(line);
                    }

                    if(listener != null){
                        listener.onFinish(resp.toString());
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    listener.onError(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onError(e);
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }
}
