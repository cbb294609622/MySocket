package com.hrd.client;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Socket socket = null;
    String buffer = "";
    TextView txt1;
    TextView send;
    EditText ed1;
    String geted1;
    ImageView closeBtn;

    TextView btnLeft;
    TextView btnRight;
    TextView btnZero;
    TextView btnTou;
    TextView btnAgo;
    TextView btnAfter;
    TextView btnLeftMove;
    TextView btnRightMove;

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                txt1.append("server:" + bundle.getString("msg") + "\n");
            }
        }
    };

    private String ips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);

        ips = getIntent().getStringExtra("ips");
        txt1 = (TextView) findViewById(R.id.txt1);
        send = (TextView) findViewById(R.id.send);
        ed1 = (EditText) findViewById(R.id.ed1);
        closeBtn = (ImageView) findViewById(R.id.close);
        btnLeft = (TextView) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(this);
        btnRight = (TextView) findViewById(R.id.btn_right);
        btnRight.setOnClickListener(this);
        btnZero = (TextView) findViewById(R.id.btn_zero);
        btnZero.setOnClickListener(this);
        btnTou = (TextView) findViewById(R.id.btn_tou);
        btnTou.setOnClickListener(this);
        btnAgo = (TextView) findViewById(R.id.btn_ago);
        btnAgo.setOnClickListener(this);
        btnAfter = (TextView) findViewById(R.id.btn_after);
        btnAfter.setOnClickListener(this);
        btnLeftMove = (TextView) findViewById(R.id.btn_leftmove);
        btnLeftMove.setOnClickListener(this);
        btnRightMove = (TextView) findViewById(R.id.btn_rightmove);
        btnRightMove.setOnClickListener(this);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                myThread("1");
                break;
            case R.id.btn_right:
                myThread("2");
                break;
            case R.id.btn_zero:
                myThread("3");
                break;
            case R.id.btn_tou:
                myThread("4");
                break;
            case R.id.btn_ago:
                myThread("5");
                break;
            case R.id.btn_after:
                myThread("6");
                break;
            case R.id.btn_leftmove:
                myThread("7");
                break;
            case R.id.btn_rightmove:
                myThread("8");
                break;
            case R.id.send:
                geted1 = ed1.getText().toString();
                txt1.append("client:" + geted1 + "\n");
                //启动线程 向服务器发送和接收信息
                myThread(geted1);
                break;
        }

    }

    public void myThread(String str){
        new MyThread(str).start();
    }

    class MyThread extends Thread {

        public String txt1;

        public MyThread(String str) {
            txt1 = str;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                //连接服务器 并设置连接超时为1秒
                socket = new Socket();
                socket.connect(new InetSocketAddress(ips, 30000), 1000); //端口号为30000
                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //读取发来服务器信息
                String line = null;
                buffer = "";
                while ((line = bff.readLine()) != null) {
                    buffer = line + buffer;
                }

                //向服务器发送信息
                ou.write(txt1.getBytes("gbk"));
                ou.flush();
                bundle.putString("msg", buffer.toString());
                msg.setData(bundle);
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
                //关闭各种输入输出流
                bff.close();
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                bundle.putString("msg", "服务器连接失败！请检查网络是否打开");
                msg.setData(bundle);
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
