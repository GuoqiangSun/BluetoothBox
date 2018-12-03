package com.bazooka.bluetoothbox.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.ui.view.ClearableEditText;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.bazooka.bluetoothbox.utils.ViewUtils;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/21
 *         作用：重命名对话框
 */

public class RenameDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private ClearableEditText etName;
    private TextView tvDemand;

    private String mName;//修改名称
    private String mDemandMessage;//提示信息
    private String mInputRange;//输入范围
    private InputFilter[] mInputFilters;

    private int index = 1;
    private OnOkClickListener mOnOkClickListener;

    public RenameDialog(@NonNull Context context) {
        this(context, getDefaultDialogThrem(context));
    }

    private static int getDefaultDialogThrem(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.alertDialogTheme, outValue, true);
        return outValue.resourceId;
    }

    public RenameDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_switch_rename);
        etName = (ClearableEditText) findViewById(R.id.et_name);
        tvDemand = (TextView) findViewById(R.id.tv_demand);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);




    }

    @Override
    protected void onStart() {
        super.onStart();
        etName.setText(mName);
        if (!TextUtils.isEmpty(mName)) {
            etName.setSelection(mName.length());
        }
        new Handler().postDelayed(() -> ViewUtils.showSoftInputFromEditText(etName), 200);
        if (!TextUtils.isEmpty(mDemandMessage)) {
            tvDemand.setText(mDemandMessage);
        }
        if (!TextUtils.isEmpty(mInputRange)) {
            etName.setKeyListener(DigitsKeyListener.getInstance(mInputRange));
        }
        if(mInputFilters != null) {
            etName.setFilters(mInputFilters);
        }

        tvDemand.setTextColor(0xff888888);
    }

    public void show(int index, String name) {
        this.index = index;
        this.mName = name;
        super.show();
    }

    public void setHintMessageColor(int color) {
        tvDemand.setTextColor(color);
    }

    /**
     * 设置要求信息
     *
     * @param resId resId
     */
    public void setDemandMessage(int resId) {
        mDemandMessage = mContext.getString(resId);
    }

    public void setInputRange(String inputRange) {
        mInputRange = inputRange;
    }

    public void setInputFilters(InputFilter[] inputFilters) {
        mInputFilters = inputFilters;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                String name = etName.getText().toString();
                if (mOnOkClickListener != null) {
                    mOnOkClickListener.onOkClick(index, name);
                }
                break;
            default:
                break;
        }
    }

    public void setOnOkClickListener(OnOkClickListener l) {
        mOnOkClickListener = l;
    }

    public interface OnOkClickListener {
        void onOkClick(int index, String name);
    }

}
