package com.hrd.socket.mysocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hrd.socket.mysocket.activity.TestActivity;

/**
 * Created by HP on 2018/3/1.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Thread.sleep(10000L);
            intent = new Intent(context, TestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
