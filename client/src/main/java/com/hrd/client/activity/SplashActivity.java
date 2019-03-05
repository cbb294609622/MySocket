package com.hrd.client.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hrd.client.R;
import com.hrd.client.codescan.MipcaActivityCapture;
import com.hrd.client.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/**
 * Created by HP on 2018/3/28.
 */

public class SplashActivity extends Activity  implements View.OnClickListener {

    private TextView mCountDownTextView;
    private MyCountDownTimer mCountDownTimer;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        mCountDownTextView = (TextView) findViewById(R.id.start_skip_count_down);
        mCountDownTextView.setOnClickListener(this);
        mCountDownTextView.setVisibility(View.GONE);
        applyPermission();
    }

    private void applyPermission() {
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "照相机", R.drawable.permission_ic_camera));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "手机识别", R.drawable.permission_ic_phone));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "文件存储", R.drawable.permission_ic_storage));
         HiPermission.create(SplashActivity.this)
                .title("亲爱的用户")
                .msg("为保证应用正常运行,请开启以下权限！")
                .style(R.style.PermissionBlueStyle)
                .permissions(permissionItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        ToastUtil.showLongToast(mContext,"权限未开启，无法使用");
                    }

                    @Override
                    public void onFinish() {
                        //进入倒计时
                        mCountDownTextView.setText("6s 跳过");
                        mCountDownTextView.setVisibility(View.VISIBLE);
                        mCountDownTimer = new MyCountDownTimer(6000, 1000);
                        mCountDownTimer.start();
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_skip_count_down:
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                onSwitchView();
                break;
        }
    }
    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以「 毫秒 」为单位倒计时的总数
         *                          例如 millisInFuture = 1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick()
         *                          例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         */

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        public void onFinish() {
            mCountDownTextView.setText("0s 跳过");
            onSwitchView();
        }

        public void onTick(long millisUntilFinished) {
            mCountDownTextView.setText(millisUntilFinished / 1000 + "s 跳过");
        }

    }

    private void onSwitchView() {
        Intent intent = new Intent(mContext,HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }
}
