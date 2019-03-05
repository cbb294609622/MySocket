package com.hrd.socket.mysocket.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hrd.socket.mysocket.R;
import com.hrd.socket.mysocket.util.InstructionsUtils;
import com.hrd.socket.mysocket.util.QRCodeUtil;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.xyirobot.open.RobotSDK;
import com.xyirobot.open.motion.RobotDirection;
import com.xyirobot.open.motion.RobotMotionManager;
import com.xyirobot.open.motion.RobotMotions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public ServerSocket serverSocket = null;
    public TextView mTextView, textView1;
    private RobotMotionManager motionManager;
    private String IP = "";
    String buffer = "";
    public ImageView qrcode;


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                String nums = bundle.getString("msg");
                mTextView.append("client：" + nums + "\n");
                onSwitchAction(InstructionsUtils.deCodeNum(nums));

            }
        }
    };

    private void onSwitchAction(int actions) {
        switch (actions) {
            case 0:
                Toast.makeText(MainActivity.this, "错误的指令，无法执行", Toast.LENGTH_LONG).show();
                break;
            case 1:
                //向左
                motionManager.action(RobotMotions.TURN_LEFT);
                break;
            case 2:
                //向右
                motionManager.action(RobotMotions.TURN_RIGHT);
                break;
            case 3:
                //转圈
                motionManager.action(RobotMotions.TURN_AROUND);
                break;
            case 4:
                //调头
                motionManager.action(RobotMotions.TURN_BACK);
                break;
            case 5:
                //前进
                RobotMotionManager.getInstance().move(RobotDirection.FORTH, 2, 50);
                break;
            case 6:
                //后退
                RobotMotionManager.getInstance().move(RobotDirection.BACK, 2, 50);
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

    RobotSDK sdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdk = RobotSDK.getInstance();
        sdk.init();
        setContentView(R.layout.activity_main);

        motionManager = RobotMotionManager.getInstance();


        mTextView = (TextView) findViewById(R.id.textsss);
        textView1 = (TextView) findViewById(R.id.textView1);
        qrcode = (ImageView) findViewById(R.id.qrcode);
        IP = getlocalip();
        textView1.setText("IP addresss:" + IP);
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(IP, 480, 480);
        qrcode.setImageBitmap(mBitmap);
        //socket直连
//        socketNetWork();

        //websocket直连
        webSocketNetWork();

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
                    _sockets.add(webSocket);
                    webSocketClose = webSocket;
                    timer.start();
                } else {
                    //服务器占线中
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
                        onSwitchAction(InstructionsUtils.deCodeNum(s));
                        webSocket.send("200");
                        webSocket.close();
                    }
                });
            }
        });
        server.listen(30000);
    }

    CountDownTimer timer = new CountDownTimer(1000*10, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            Log.i("tag", millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish() {
            webSocketClose.send("201");//断开连接
            webSocketClose.close();
        }
    };


    private void socketNetWork() {
        new Thread() {
            public void run() {
                Bundle bundle = new Bundle();
                bundle.clear();
                OutputStream output;
                String str = "ok";
                try {
                    serverSocket = new ServerSocket(30000);
                    while (true) {
                        Message msg = new Message();
                        msg.what = 0x11;
                        try {
                            Socket socket = serverSocket.accept();
                            output = socket.getOutputStream();
                            output.write(str.getBytes("gbk"));
                            output.flush();
                            socket.shutdownOutput();
                            BufferedReader bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line = null;
                            buffer = "";
                            while ((line = bff.readLine()) != null) {
                                buffer = line + buffer;
                            }
                            bundle.putString("msg", buffer.toString());
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                            bff.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }.start();
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
