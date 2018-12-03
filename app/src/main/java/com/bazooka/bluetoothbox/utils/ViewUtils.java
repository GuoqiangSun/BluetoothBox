package com.bazooka.bluetoothbox.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bazooka.bluetoothbox.application.App;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/16
 * 作用：
 */

public class ViewUtils {

    private ViewUtils() {
    }

    public static boolean isEmpty(@Nullable TextView text) {
        return TextUtils.isEmpty(text != null ? getString(text.getText()) : "");
    }

    public static boolean isEmpty(@Nullable CharSequence text) {
        return TextUtils.isEmpty(text != null ? getString(text) : "");
    }

    public static String getString(@NonNull CharSequence text) {
        return text.toString();
    }

    public static String getString(TextView v) {
        return v.getText().toString();
    }

    public static void setIconPadding(@NonNull View view) {
        view.setPadding(CalculateUtils.dip2px(2),
                CalculateUtils.dip2px(2), CalculateUtils.dip2px(2),
                CalculateUtils.dip2px(2));
    }

    /**
     * 修改光标位置
     *
     * @param ets
     */
    public static void alertCursorIndex(@NonNull EditText... ets) {
        for (EditText et : ets) {
            if (!TextUtils.isEmpty(et.getText())) {
                Selection.setSelection(et.getText(), et.getText().length());
            }
        }
    }

    /**
     * 强制关闭软键盘
     *
     * @param et
     */
    public static void hideSoftInputFromEditText(@NonNull View et) {
        InputMethodManager imm = (InputMethodManager) App.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    /**
     * 强制显示软件盘
     *
     * @param et
     */
    public static void showSoftInputFromEditText(@NonNull EditText et) {
        et.setFocusable(true);
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) App.getContext().getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, 0);
    }

    /**
     * 设置textView的字体颜色
     * @param color 颜色
     * @param views textView
     */
    public static void setTextColor(int color, TextView... views){
        for(TextView view : views){
            view.setTextColor(color);
        }
    }
}
