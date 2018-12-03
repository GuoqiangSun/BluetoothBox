package com.bazooka.bluetoothbox.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.bazooka.bluetoothbox.application.App;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Created by whyte on 2016/10/17 0017.
 * 计算
 */

public class CalculateUtils {
    private CalculateUtils() {
    }

    private static int displayWidth;
    private static int displayHeight;

    static {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
    }

    /**
     * px转dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        final float scale = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = App.getContext().getResources()
                .getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(float spValue) {
        final float fontScale = App.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度(px)
     *
     * @return
     */
    public static int getDisplayWidth() {
        return displayWidth;
    }

    /**
     * 获取屏幕高度(px)
     *
     * @return
     */
    public static int getDisplayHeight() {
        return displayHeight;
    }

    /**
     * 对字符串进行md5加密
     *
     * @param str
     * @return
     */
    public static String toMD5(@NonNull String str) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 判断是否是密码
     *
     * @param password 密码
     * @return 合法为true 否则为false
     */
    public static boolean isPassword(String password) {
        return !TextUtils.isEmpty(password) && Pattern.matches("^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,16}$", password);
    }

    /**
     * @Function : 判断邮箱是否合法
     * @Param :email 输入的邮箱
     * @Return :合法为true 否则为false
     * @Author ：zhangla
     */
    public static boolean isEmail(String email) {
        return !TextUtils.isEmpty(email) && Pattern.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", email);
    }

    /**
     * @Function :判断是否是手机电话
     * @Param ：mobiles(手机号码)
     * @Return ：合法为true 否则为false
     * @Author ：zhangla
     */
    public static boolean isMobile(String mobiles) {
        return !TextUtils.isEmpty(mobiles) && Pattern.matches("^((1[3-9][0-9]))\\d{8}$", mobiles);
    }

//    private static TypedValue mTmpValue = new TypedValue();
//
//    public static int getDimenValue(Context context, int dimenResId){
//
//    }
}
