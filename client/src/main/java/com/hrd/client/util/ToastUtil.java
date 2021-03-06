package com.hrd.client.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hrd.client.R;


/**
 * Created by Administrator on 2016/5/21.
 */
public class ToastUtil {
        private static Toast toast;
        private static View view;

        private ToastUtil() {
        }

        @SuppressLint("ShowToast")
        private static void getToast(Context context) {
            if (toast == null) {
                toast = new Toast(context);
            }
            if (view == null) {
                view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
                view.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                view.setPadding(20,10,20,10);
            }
            toast.setView(view);
        }

        public static void showShortToast(Context context, CharSequence msg) {
            showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }

        public static void showShortToast(Context context, int resId) {
            showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
        }

        public static void showLongToast(Context context, CharSequence msg) {
            showToast(context.getApplicationContext(), msg, Toast.LENGTH_LONG);
        }

        public static void showLongToast(Context context, int resId) {
            showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
        }

        private static void showToast(Context context, CharSequence msg,
                                      int duration) {
            try {
                getToast(context);
                toast.setText(msg);
                toast.setDuration(duration);
                toast.show();
            } catch (Exception e) {
                Log.d("Toast",e.getMessage());
            }
        }

        private static void showToast(Context context, int resId, int duration) {
            try {
                if (resId == 0) {
                    return;
                }
                getToast(context);
                toast.setText(resId);
                toast.setDuration(duration);
                toast.show();
            } catch (Exception e) {
                Log.d("Toast",e.getMessage());
            }
        }

}
