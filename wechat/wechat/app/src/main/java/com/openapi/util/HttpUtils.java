package com.openapi.util;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by muskong on 2017/9/4.
 */

public class HttpUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    /**
     * post
     * @param url
     * @param json json data
     * @return
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (
                Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    /**
     * get
     * @param url
     * @return
     * @throws IOException
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void getAsyn(String url, String json) throws Exception {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());
            }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws Exception {
        String json = "http://bi.mybi.top:9000/track/startup?data=%7B%22appid%22%3A%221%22%2C%22context%22%3A%7B%22os%22%3A%2225%22%2C%22resolution%22%3A%221920%2A1080%22%2C%22channelid%22%3A%22%5Fdefault%5F%22%2C%22network%22%3A%22other%22%2C%22ip%22%3A%22172%2E22%2E113%2E243%22%2C%22androidid%22%3A%22bf380208f0e807ad%22%2C%22deviceid%22%3A%22865902aaaa032336803%22%7D%7D";
//        getAsyn("http://bi.mybi.top:9000/track/startup", json);
        String response = get(json);
        System.out.println(response);
    }
}
