package com.bazooka.bluetoothbox.utils;

import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.Toast;

import com.bazooka.bluetoothbox.application.App;

/**
 * Created by whyte on 2016/9/27 0027.
 */

public class ToastUtils {

    private ToastUtils() {
    }

    @Nullable
    private static Toast mToast;

    private static boolean showToast = true;

    public static void showShortToast(String message) {
        if(showToast){
            if(mToast == null) {
                mToast = Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    public static void showShortToast(int resId) {
        if(showToast) {
            if(mToast == null) {
                mToast = Toast.makeText(App.getContext(), resId, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }

    public static void showLongToast(String message) {
        if(showToast) {
            if(mToast == null) {
                mToast = Toast.makeText(App.getContext(), message, Toast.LENGTH_LONG);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(message);
            }
            mToast.show();
        }
    }

    public static void showLongToast(int resId) {
        if(showToast) {
            if(mToast == null) {
                mToast = Toast.makeText(App.getContext(), resId, Toast.LENGTH_LONG);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(resId);
            }
            mToast.show();
        }
    }


    public static void cancel(){
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
