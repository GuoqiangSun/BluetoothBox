package com.bazooka.bluetoothbox.utils;

import android.content.Context;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/3
 *         作用：
 */

public class DialogUtils {

    public static PromptDialogV2 createNoConnectedDialog(Context context,
                                             PromptDialogV2.OnButtonClickListener listener){
        PromptDialogV2 dialog = new PromptDialogV2(context);
        dialog.setHintMessage(context.getString(R.string.bluetooth_connect_fail_hint));
        dialog.setNegativeText(context.getString(R.string.setting));
        dialog.setPositiveText(context.getString(R.string.cancel));
        dialog.setOnButtonClickListener(listener);
        return dialog;
    }

}
