package com.bazooka.bluetoothbox.utils;

import com.bazooka.bluetoothbox.application.App;


/**
 * Created by Taro on 2017/4/17.
 * 屏幕相关工具类
 */
public class ScreenUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = App.getContext().getResources().getDisplayMetrics().density;
        int value = (int) (pxValue / scale + 0.5f);
        return value;
    }

    /**
     * sp转px
     */
    public static int sp2px(final float spValue) {
        final float fontScale = App.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }



}
