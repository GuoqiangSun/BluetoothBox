package com.bazooka.bluetoothbox.utils;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/23
 *         作用：
 */

public class DateUtils {

    public static String millToString(long mill){
        long allSecond = mill / 1000;
        long hour = allSecond / 3600;
        long minute = allSecond % 3600 / 60 ;
        long second = allSecond % 60;
        String hourStr = (hour < 10 ? "0"  : "") + hour;
        String minuteStr = (minute < 10 ? "0"  : "") + minute;
        String secondStr = (second < 10 ? "0"  : "") + second;
        return hourStr + ":" + minuteStr + ":" + secondStr;
    }
}
