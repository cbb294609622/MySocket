package com.hrd.socket.mysocket;

import android.app.Application;
import android.util.Log;

import com.hrd.socket.mysocket.util.ImageDownLoader;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * Created by HP on 2018/1/19.
 */

public class PadApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //全局异常捕获
        initCrash();
        //全局网络配置
        initOKHTTP();
        //图片
        loadImage();
    }

    private void loadImage() {
        OkHttpClient client = new OkHttpClient.Builder()
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .build();
        Picasso.setSingletonInstance(new Picasso.Builder(this).
                downloader(new ImageDownLoader(client))
                .build());
    }

    private void initCrash() {
        //全局异常捕获 初始化
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    private void initOKHTTP() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(6000L, TimeUnit.MILLISECONDS)
                .readTimeout(6000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }
}
