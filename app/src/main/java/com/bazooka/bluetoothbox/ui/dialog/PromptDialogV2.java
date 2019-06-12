package com.bazooka.bluetoothbox.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/10/16
 * 作用：统一的提示弹窗
 */

public class PromptDialogV2 extends Dialog implements View.OnClickListener {

    private TextView tvHint;
    private Button btnPositive;
    private Button btnNegative;
    private String mHint, mPositiveText, mNegativeText;

    private int what;
    private int arg1;


    private OnButtonClickListener mListener;

    public PromptDialogV2(@NonNull Context context) {
//        this(context, getDefaultDialogTheme(context));
        super(context);
    }

    public PromptDialogV2(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    private static int getDefaultDialogTheme(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);
        return outValue.resourceId;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prompt);

        tvHint = (TextView) findViewById(R.id.tv_hint);
        btnPositive = (Button) findViewById(R.id.btn_positive);
        btnNegative = (Button) findViewById(R.id.btn_negative);

        btnPositive.setBackgroundResource(R.drawable.bg_dark_grey_n);
        btnNegative.setBackgroundResource(R.drawable.bg_light_blue_n);

        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
    }

    public void setHintMessage(String hint) {
        this.mHint = hint;
    }

    public void setPositiveText(String positiveText) {
        this.mPositiveText = positiveText;
    }

    public void setNegativeText(String negativeText) {
        this.mNegativeText = negativeText;
    }

    @Override
    protected void onStart() {
        super.onStart();
        tvHint.setText(mHint);
        btnPositive.setText(mPositiveText);
        btnNegative.setText(mNegativeText);

        //设置背景半透明
        DisplayMetrics dm = new DisplayMetrics();
        Activity ownerActivity = getOwnerActivity();
        if (ownerActivity != null) {
            ownerActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        Window window = getWindow();
        if (window != null) {
//            window.setLayout(dm.widthPixels, window.getAttributes().height);

//            int selectColor = Color.parseColor("#0E000080");
//            window.setBackgroundDrawable(new ColorDrawable(selectColor));

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    public void setPositiveBackground(int resId) {
        btnPositive.setBackgroundResource(resId);
    }

    public void setNegativeBackground(int resId) {
        btnNegative.setBackgroundResource(resId);
    }

    /**
     * 展示 Dialog，可以携带一个 int 类型的参数，
     *
     * @param what 操作类型类型
     * @param arg1 int 类型参数
     */
    public void show(int what, int arg1) {
        super.show();
        this.what = what;
        this.arg1 = arg1;
    }

    public int getArg1() {
        return arg1;
    }

    public int getWhat() {
        return what;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                if (mListener != null) {
                    mListener.onPositiveClick();
                }
                break;
            case R.id.btn_negative:
                if (mListener != null) {
                    mListener.onNegativeClick();
                }
                break;
            default:
                break;
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.mListener = l;
    }


    public interface OnButtonClickListener {
        /**
         * 确定按钮点击
         */
        void onPositiveClick();

        /**
         * 取消按钮点击
         */
        void onNegativeClick();
    }
}
