package com.hrd.client.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hrd.client.MainActivity;
import com.hrd.client.R;
import com.hrd.client.codescan.MipcaActivityCapture;
import com.hrd.client.util.ToastUtil;

/**
 * Created by HP on 2018/3/28.
 */

public class HomeActivity extends Activity implements View.OnClickListener{

    private EditText editIP;
    private TextView openIP,qrcodeIP;
    private Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_homes);
        mContext = HomeActivity.this;
        editIP = (EditText) findViewById(R.id.edit_ip);
        openIP = (TextView) findViewById(R.id.open_ip);
        qrcodeIP = (TextView) findViewById(R.id.qrcode_ip);
        openIP.setOnClickListener(this);
        qrcodeIP.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.open_ip:
                String ips = editIP.getText().toString().trim();
                if (TextUtils.isEmpty(ips)){
                    ToastUtil.showLongToast(mContext,"请输入IP地址");
                }else{
                    //输入了 IP地址，并且是正确的
                    //进入下一个页面
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("ips",ips);
                    startActivity(intent);
                }
                break;
            case R.id.qrcode_ip:
                Intent intent = new Intent(mContext,MipcaActivityCapture.class);
                startActivity(intent);
                break;
        }
    }
}
