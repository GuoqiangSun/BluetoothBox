package com.bazooka.bluetoothbox.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.application.App;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/22
 *         作用：
 */

public class ToastUtil {


    public static void showConnectSuccessToast() {
        Toast t = createCustomerToast(R.layout.toast_connect_success, Gravity.CENTER, Toast.LENGTH_LONG);
        t.show();
    }

    public static void showConnectFailToast() {
        Toast t = createCustomerToast(R.layout.toast_connect_fail, Gravity.CENTER, Toast.LENGTH_LONG);
        t.show();
    }

    public static void showSearchFailToast() {
        Toast t = createCustomerToast(R.layout.toast_search_fail, Gravity.CENTER, Toast.LENGTH_LONG);
        t.show();
    }

    public static Toast createCustomerToast(int layoutResId, int gravity, int duration) {
        Context context = App.getContext();
        Toast t = new Toast(context);
        View v = LayoutInflater.from(context).inflate(layoutResId, null);
        t.setView(v);
        t.setGravity(gravity, 0, 0);
        t.setDuration(duration);
        return t;
    }
}
