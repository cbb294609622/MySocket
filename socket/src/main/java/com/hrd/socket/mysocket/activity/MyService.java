package com.hrd.socket.mysocket.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.hrd.socket.mysocket.action.RobotContext;
import com.hrd.socket.mysocket.bean.PublicBean;
import com.hrd.socket.mysocket.net.AddHeader;
import com.hrd.socket.mysocket.net.Api;
import com.hrd.socket.mysocket.util.GsonUtil;
import com.hrd.socket.mysocket.util.InstructionsUtils;
import com.koushikdutta.async.AsyncSSLSocket;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.xyirobot.open.RobotSDK;
import com.xyirobot.open.led.RobotLedManager;
import com.xyirobot.open.motion.RobotDirection;
import com.xyirobot.open.motion.RobotMotionManager;
import com.xyirobot.open.speech.SpeechCallback;
import com.xyirobot.open.tts.RobotTtsManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by HP on 2018/3/29.
 */

public class MyService extends Service {

    public MyService() {
        Log.i("TAG", "MusicService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();

    }

    RobotSDK sdk;
    private RobotMotionManager motionManager;
    private RobotLedManager ledManager;

//    private Handler uiHandler;
//    SpeechCallback callback = new SpeechCallback() {
//        @Override
//        public void onSpeech(final String text) {
//            Log.i("TAG",text);
//            uiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (text.contains("视频")){
//                        onSwitchAction(6);
//                    }
//                }
//            });
//        }
//    };
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG", "onCreate");
        sdk = RobotSDK.getInstance();
        sdk.init();
        motionManager = RobotMotionManager.getInstance();
        ledManager = RobotLedManager.getInstance();

//        uiHandler = new Handler();
//        sdk.registerSpeechCallback(callback);

        //把ip 和端口 发给后端
        iPAddressData();
    }

    AsyncHttpServer server;
    List<WebSocket> _sockets;
    WebSocket webSocketClose;

    private void webSocketNetWork() {
        server = new AsyncHttpServer();
        _sockets = new ArrayList<WebSocket>();
        server.websocket("/live", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                if (_sockets.size() == 0) {
                    Log.i("TAG","200链接成功");
                    _sockets.add(webSocket);
                    webSocket.send("200");
                    webSocketClose = webSocket;
                    timer.start();
                } else {
                    //服务器占线中
                    Log.i("TAG","202服务器占线中");
                    webSocket.send("202");//服务器正在被使用中
                    webSocket.close();
                }

                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error");
                        } finally {
                            _sockets.remove(webSocket);
                        }
                    }
                });

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        timer.cancel();
                        webSocket.close();
                        onSwitchAction(InstructionsUtils.deCodeNum(s));
                    }
                });
            }
        });
        server.listen(40000);

    }



    CountDownTimer timer = new CountDownTimer(1000 * 10, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            Log.i("tag", millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish() {
            Log.i("TAG","201断开连接");
            webSocketClose.send("201");//断开连接
            webSocketClose.close();
        }
    };
    RobotContext robotContext;
    InputMethodManager imm;
    Random random = new Random();
    private void onSwitchAction(int actions) {
        switch (actions) {
            case 1:
                int strOne = (int) (Math.random() * Api.str.length);
                String strr = Api.str[strOne];
                //打招呼
                RobotTtsManager.getInstance().synth("你好，我是小C，我的英文名字是Cino，"+strr);
                robotContext = new RobotContext(motionManager,ledManager);
                robotContext.blinkLed(1,2,5);
                robotContext.turnHeadVertical(3000,45);
                robotContext.turnHeadVertical(2000,5);
                robotContext.turnRotate(3000);
                robotContext.reset();
                break;
            case 2:
                //动动手
                robotContext = new RobotContext(motionManager,ledManager);
                RobotTtsManager.getInstance().synth("你好呀,小主人");
                robotContext.blinkLed(3,4,8);
                robotContext.turnHandMove(2500);
                robotContext.reset();
                break;
            case 3:
                //动动腰
                robotContext = new RobotContext(motionManager,ledManager);
                RobotTtsManager.getInstance().synth("小主人，和我一起做运动吧！");
                robotContext.blinkLed(1,2,8);
                robotContext.blinkLed(3,4,3);
                robotContext.turnLeft(1000);
                robotContext.turnLeft(1000);
                robotContext.turnLeft(1000);
                robotContext.turnRight(1000);
                robotContext.turnRight(1000);
                robotContext.turnRight(1000);
                robotContext.turnRotate(3500);
                robotContext.reset();
                break;
            case 4:
                //笑哈哈
                robotContext = new RobotContext(motionManager,ledManager);
                robotContext.lightLed(1,2,5);
                robotContext.lightLed(3,4,5);
                int index = (int) (Math.random() * Api.jokes.length);
                String joke = Api.jokes[index];
                RobotTtsManager.getInstance().synth("小主人，你是想让我给你讲一个笑话吗?"+joke.toString());
                robotContext.resetLed();
                break;
            case 5:
                //萌萌哒
                robotContext = new RobotContext(motionManager,ledManager);
                RobotTtsManager.getInstance().synth("喵喵喵，喵喵喵，喵喵喵,感觉自己萌萌哒");
                robotContext.lightLed(1,2,8);
                robotContext.lightLed(3,4,8);
                robotContext.turnHandMove(3000);
                robotContext.resetLed();
                break;
            case 6:
                //背唐诗
                int indexBook = (int) (Math.random() * Api.books.length);
                String book = Api.books[indexBook];
                RobotTtsManager.getInstance().synth(book.toString());
                robotContext.lightLed(1,2,3);
                robotContext.lightLed(3,4,3);
                robotContext.resetLed();
                break;
            case 7:
                //左边移动
                RobotMotionManager.getInstance().move(RobotDirection.LEFT, 2, 50);
                break;
            case 8:
                //右边移动
                RobotMotionManager.getInstance().move(RobotDirection.RIGHT, 2, 50);
                break;
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("TAG", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
        //返回值不同，Service被杀掉的情况也不同
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {

        Log.i("TAG", "onUnbind");
        return super.onUnbind(intent);
    }


    private void iPAddressData() {
        AddHeader adds = new AddHeader();
        adds.addData("socketIp", getlocalip());//ip地址
        adds.addData("socketPort", "40000");//端口
        String header = adds.getHeader();
        OkHttpUtils.postString()
                .url(Api.getIPAddress())
                .content(header)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("TAG", e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        PublicBean bean = (PublicBean) GsonUtil.toFrom(response, PublicBean.class);
                        if (bean.code.equals("200")) {
                            //开启服务
                            webSocketNetWork();
                        } else {
                            iPAddressData();
                        }
                    }
                });
    }


    /**
     * 或取本机的ip地址
     */
    private String getlocalip() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        //  Log.d(Tag, "int ip "+ipAddress);
        if (ipAddress == 0) return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

}
