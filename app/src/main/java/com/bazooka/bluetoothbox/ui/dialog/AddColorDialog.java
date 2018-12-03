package com.bazooka.bluetoothbox.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.ui.view.colorpicker.ColorPickView;
import com.bazooka.bluetoothbox.utils.CalculateUtils;
import com.bazooka.bluetoothbox.utils.ColorUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.orhanobut.logger.Logger;

import cn.qqtheme.framework.picker.NumberPicker;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/27
 *         作用：添加颜色的 Dialog
 */

public class AddColorDialog extends Dialog {

    private Activity mActivity;
    private LinearLayout llRgbInfo;
    private ColorPickView colorPickerView;
    private ImageView ivSaveColor;
    private FrameLayout flColorPicker;

    private NumberPicker mRPicker, mGPicker, mBPicker;

    private OnColorSelectedListener mListener;
    private Window dialogWindow;
    private WindowManager.LayoutParams layoutParams;

    public AddColorDialog(@NonNull Context context) {
        this(context, R.style.AddColorDialogStyle);
    }

    public AddColorDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_color);
        flColorPicker = (FrameLayout) findViewById(R.id.fl_color_picker);
        llRgbInfo = (LinearLayout) findViewById(R.id.ll_rgb_info);
        ivSaveColor = (ImageView) findViewById(R.id.iv_save_color);

        initDialog();
        initColorPickerView();
        initColorInfo();
        addListener();
    }

    private void initDialog() {
        dialogWindow = getWindow();
        layoutParams = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        layoutParams.y = ConvertUtils.dp2px(10);
        dialogWindow.setAttributes(layoutParams);
    }

    private void initColorPickerView() {
        colorPickerView = (ColorPickView) findViewById(R.id.colorPickerView);

        int displayWidth = (int) (CalculateUtils.getDisplayWidth() * 3.0f / 4);//屏幕宽度的3/4
        Logger.d(displayWidth);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) flColorPicker.getLayoutParams();
        params.width = displayWidth;
        params.height = displayWidth;
        flColorPicker.setLayoutParams(params);
        colorPickerView.setBigCircle(displayWidth / 2 - ConvertUtils.dp2px(17));//减去边框宽度，大致为17dp
    }

    private void addListener() {

        colorPickerView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(int red, int green, int blue) {
                mRPicker.getWheelView().setSelectedIndex(red);
                mGPicker.getWheelView().setSelectedIndex(green);
                mBPicker.getWheelView().setSelectedIndex(blue);
            }

            @Override
            public void onTouchStop(int red, int green, int blue) {
                mRPicker.getWheelView().setSelectedIndex(red);
                mGPicker.getWheelView().setSelectedIndex(green);
                mBPicker.getWheelView().setSelectedIndex(blue);

                if (mListener != null) {
                    mListener.onColorChange(red, green, blue);
                }
            }
        });

        ivSaveColor.setOnClickListener(v -> {
            if (mListener != null) {
                int red = mRPicker.getWheelView().getSelectedIndex();
                int green = mGPicker.getWheelView().getSelectedIndex();
                int blue = mBPicker.getWheelView().getSelectedIndex();

                mListener.onColorSelected(red, green, blue);
            }
            dismiss();
        });

        mRPicker.setOnWheelListener((index, item) -> {
            if (mListener != null) {
                mListener.onColorChange(item.intValue(),
                        mGPicker.getSelectedItem().intValue(),
                        mBPicker.getSelectedItem().intValue());
            }
        });

        mGPicker.setOnWheelListener((index, item) -> {
            if (mListener != null) {
                mListener.onColorChange(mRPicker.getSelectedItem().intValue(),
                        item.intValue(),
                        mBPicker.getSelectedItem().intValue());
            }
        });

        mBPicker.setOnWheelListener((index, item) -> {
            if (mListener != null) {
                mListener.onColorChange(mRPicker.getSelectedItem().intValue(),
                        mGPicker.getSelectedItem().intValue(),
                        item.intValue());
            }
        });
    }

    private void initColorInfo() {
        mRPicker = new NumberPicker(mActivity);
        mRPicker.setWidth(1);
        mRPicker.setCycleDisable(false);
        mRPicker.setDividerVisible(false);
        mRPicker.setOffset(1);//偏移量
        mRPicker.setRange(0, 255, 1);//数字范围
        mRPicker.setSelectedItem(255);
        mRPicker.setTextColor(0xff000000);

        mGPicker = new NumberPicker(mActivity);
        mGPicker.setWidth(1);
        mGPicker.setCycleDisable(false);
        mGPicker.setDividerVisible(false);
        mGPicker.setOffset(1);//偏移量
        mGPicker.setRange(0, 255, 1);//数字范围
        mGPicker.setSelectedItem(255);
        mGPicker.setTextColor(0xff000000);

        mBPicker = new NumberPicker(mActivity);
        mBPicker.setWidth(1);
        mBPicker.setCycleDisable(false);
        mBPicker.setDividerVisible(false);
        mBPicker.setOffset(1);//偏移量
        mBPicker.setRange(0, 255, 1);//数字范围
        mBPicker.setSelectedItem(255);
        mBPicker.setTextColor(0xff000000);

        LinearLayout.LayoutParams pickerParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        pickerParams.weight = 1;
        View mRPickerView = mRPicker.getContentView();
        View mGPickerView = mGPicker.getContentView();
        View mBPickerView = mBPicker.getContentView();
        mRPickerView.setLayoutParams(pickerParams);
        mGPickerView.setLayoutParams(pickerParams);
        mBPickerView.setLayoutParams(pickerParams);

        llRgbInfo.addView(mRPickerView, 1);
        llRgbInfo.addView(mGPickerView, 3);
        llRgbInfo.addView(mBPickerView, 5);
    }


    public interface OnColorSelectedListener {

        void onColorChange(int red, int green, int blue);

        void onColorSelected(int red, int green, int blue);
    }

    public void setOnColorSelectedListener(OnColorSelectedListener l) {
        mListener = l;
    }

}
