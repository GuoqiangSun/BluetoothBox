package com.bazooka.bluetoothbox.base.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bazooka.bluetoothbox.base.activity.IActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：Fragment 基类
 */

public abstract class BaseFragment extends Fragment implements IActivity{

    protected Activity mContext;
    private View mFragmentRootView;
    private Unbinder unbinder;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public FragmentManager getSupportFragmentManager(){
        return ((FragmentActivity)mContext).getSupportFragmentManager();
    }

}
