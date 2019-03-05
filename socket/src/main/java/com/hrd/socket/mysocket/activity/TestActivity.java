package com.hrd.socket.mysocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by HP on 2018/3/29.
 */

public class TestActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity  OnCreate()....");
        System.out.println("准备开启服务");
        Intent intent = new Intent(TestActivity.this, MyService.class);
        startService(intent);
        finish();
    }
}
