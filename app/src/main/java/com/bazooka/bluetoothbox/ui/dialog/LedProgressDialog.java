package com.bazooka.bluetoothbox.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：闪法推送进度对话框
 */

public class LedProgressDialog extends Dialog {

    private int max;

    ProgressBar pbProgress;
    RelativeLayout rlProgress;
    TextView tvFinishMessage;
    Button btnOkay;
    LinearLayout llSendFinished;
    TextView tvProgress;
    Button btnCancel;

    private OnButtonClickListener mOnButtonClickListener;

    public LedProgressDialog(@NonNull Context context) {
        this(context, getDefaultDialogTheme(context));
    }

    public LedProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
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
        setContentView(R.layout.dialog_led_progress);

        pbProgress = (ProgressBar) findViewById(R.id.progress);
        rlProgress = (RelativeLayout) findViewById(R.id.rl_progress);
        tvFinishMessage = (TextView) findViewById(R.id.tv_finsh_message);
        btnOkay = (Button) findViewById(R.id.btn_okay);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        llSendFinished = (LinearLayout) findViewById(R.id.ll_send_finished);

        setCancelable(false);

        initViews();
        addViewListener();
    }

    private void initViews() {

    }

    private void addViewListener() {
        btnOkay.setOnClickListener(v -> {
            dismiss();
        });

        btnCancel.setOnClickListener(v -> {
            if(mOnButtonClickListener != null) {
                mOnButtonClickListener.onCancelClick();
            }
        });
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setProgress(int progress) {
        pbProgress.setProgress(progress);
        tvProgress.setText(progress + "/" + max);
    }

    @Override
    protected void onStart() {
        super.onStart();
        pbProgress.setMax(max);
        tvProgress.setText("1/" + max);
    }

    @Override
    public void show() {
        super.show();
        setCancelable(false);
        rlProgress.setVisibility(View.VISIBLE);
        llSendFinished.setVisibility(View.GONE);
    }

    /**
     * 设置发送完成
     *
     * @param finishMessage 完成后的提示系信息
     */
    public void setSendSuccess(String finishMessage) {
        setCancelable(true);
        rlProgress.setVisibility(View.GONE);
        llSendFinished.setVisibility(View.VISIBLE);
        tvFinishMessage.setText(finishMessage);
    }


    public void setOnButtonClickListener(OnButtonClickListener l) {
        mOnButtonClickListener = l;
    }


    public interface OnButtonClickListener {
        void onCancelClick();
    }

}
