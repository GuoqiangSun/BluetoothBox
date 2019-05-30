package com.bazooka.bluetoothbox.base.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bazooka.bluetoothbox.base.activity.IActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：DialogFragment 形式的 Dialog 的基类
 */

public abstract class BaseDialogFragment extends DialogFragment implements IActivity {

    protected Activity mContext;
    private View mFragmentRootView;
    private Unbinder unbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setStyle(isShowTitle() ? DialogFragment.STYLE_NORMAL : DialogFragment.STYLE_NO_TITLE, returnDialogStyle());
    }

    @Override
    public void onStart() {
        super.onStart();

        //设置背景半透明
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(dm.widthPixels, window.getAttributes().height);

//            int selectColor = Color.parseColor("#0E000080");
//            window.setBackgroundDrawable(new ColorDrawable(selectColor));

            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentRootView = inflaterView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, mFragmentRootView);

        this.initData();
        this.initView();
        this.addViewListener();
        return mFragmentRootView;
    }

    protected int returnDialogStyle() {
        return android.R.style.Theme_Holo_Light_Dialog_MinWidth;
    }

    protected abstract boolean isShowTitle();

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }

}
