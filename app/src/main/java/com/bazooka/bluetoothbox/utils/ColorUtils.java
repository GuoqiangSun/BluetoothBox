package com.bazooka.bluetoothbox.utils;

import android.graphics.Color;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/16
 * 作用：
 */

public class ColorUtils {

    /**
     * 转换整数类型的颜色值为RGB
     *
     * @param color
     * @return 0 R, 1 G, 2 B
     */
    public static int[] convertRGB(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);

        return new int[]{red, green, blue};
    }

    /**
     * 转换整数类型的颜色值为ARGB
     *
     * @param color
     * @return 0 A, 1 R, 2 G, 3 B
     */
    public static int[] convertARGB(int color) {
        int alpha = (color & 0xff000000) >>> 24;
        int red = (color & 0x00ff0000) >> 16;
        int green = (color & 0x0000ff00) >> 8;
        int blue = (color & 0x000000ff);

        return new int[]{alpha, red, green, blue};
    }

    public static int ARGB2Color(int alpha, int red, int green, int blue) {
        return Color.argb(alpha, red, green, blue);
    }

    public static int RGB2Color(int red, int green, int blue) {
        return Color.rgb(red, green, blue);
    }


}
