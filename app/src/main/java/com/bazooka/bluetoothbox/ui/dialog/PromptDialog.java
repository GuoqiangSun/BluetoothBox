package com.bazooka.bluetoothbox.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.dialog.BaseDialogFragment;
import com.bazooka.bluetoothbox.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/10/16
 * 作用：统一的提示弹窗
 */

public class PromptDialog extends BaseDialogFragment {

    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.btn_positive)
    Button btnPositive;
    @BindView(R.id.btn_negative)
    Button btnNegative;

    private OnButtonClickListener mListener;
    private String hintContent;
    private String positiveText;
    private String negativeText;

    /**
     * 实例化提示对话框
     *
     * @param hintContent  提示内容
     * @param positiveText 确定按钮文字
     * @param negativeText 取消按钮文字
     * @return 对话框
     */
    public static PromptDialog newInstance(String hintContent, String positiveText, String negativeText) {
        PromptDialog dialog = new PromptDialog();
        Bundle bundle = new Bundle();
        bundle.putString("hintContent", hintContent);
        bundle.putString("positiveText", positiveText);
        bundle.putString("negativeText", negativeText);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_prompt, container, false);
    }

    @Override
    protected boolean isShowTitle() {
        return false;
    }

    @Override
    public void initData() {
        Bundle data = getArguments();
        hintContent = data.getString("hintContent");
        positiveText = data.getString("positiveText");
        negativeText = data.getString("negativeText");
    }

    @Override
    public void initView() {
        tvHint.setTextSize(ScreenUtils.px2dip(50));

        tvHint.setText(hintContent);
        btnPositive.setText(positiveText);
        btnNegative.setText(negativeText);
    }

    @Override
    public void addViewListener() {

    }

    @OnClick({R.id.btn_positive, R.id.btn_negative})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_positive:
                if (mListener != null) {
                    mListener.onPositiveClick();
                }
                break;
            case R.id.btn_negative:
                if (mListener != null) {
                    mListener.onNegativeClick();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.mListener = l;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {

        return super.show(transaction, tag);
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
