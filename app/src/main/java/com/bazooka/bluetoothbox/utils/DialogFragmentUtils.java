package com.bazooka.bluetoothbox.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/21
 *         作用：
 */

public class DialogFragmentUtils {

    public static void dismissDialog(FragmentManager manager, String tag){
        Fragment dialogFragment = manager.findFragmentByTag(tag);
        if(dialogFragment != null) {
            ((DialogFragment)dialogFragment).dismiss();
        }
    }

}
