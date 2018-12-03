package com.bazooka.bluetoothbox.base.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.ui.adapter.ViewPagerAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：蓝牙、FM、USB、AUX模式的共同基类
 */
public abstract class MusicCommonActivity extends BaseActivity {

    @BindView(R.id.ll_root)
    LinearLayout llRoot;
    @BindView(R.id.iv_back)
    protected ImageView ivBack;
    @BindView(R.id.iv_icon)
    protected ImageView ivIcon;
    /**
     * 指示点1
     */
    @BindView(R.id.iv_indicator_1)
    protected ImageView ivIndicator1;
    /**
     * 指示点2
     */
    @BindView(R.id.iv_indicator_2)
    protected ImageView ivIndicator2;
    @BindView(R.id.view_pager)
    protected ViewPager viewPager;

    protected ViewPagerAdapter pagerAdapter;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_common;
    }

    @Override
    public void initView() {
        llRoot.setBackgroundResource(setBackgroundResId());
        ivIcon.setImageResource(setIconRes());

        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivIcon, "Rotation", 0f, 360f);
        rotation.setDuration(3500);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setRepeatMode(ObjectAnimator.RESTART);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();

        fragments.clear();
        fragments.addAll(setViewPagerContent());
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(fragments.size());

        initViews();
        setIvBackListener();
        setViewPagerListener();
    }

    private void setIvBackListener(){
        ivBack.setOnClickListener(v -> finish());
    }

    private void setViewPagerListener(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    ivIndicator1.setImageResource(R.drawable.ic_indicator_checked);
                    ivIndicator2.setImageResource(R.drawable.ic_indicator_normal);
                } else {
                    ivIndicator1.setImageResource(R.drawable.ic_indicator_normal);
                    ivIndicator2.setImageResource(R.drawable.ic_indicator_checked);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置 ViewPager 的内容 Fragment 集合
     */
    public abstract List<Fragment> setViewPagerContent();

    public abstract int setBackgroundResId();

    /**
     * 设置旋转图片的资源id
     */
    public abstract int setIconRes();

    public abstract void initViews();

    public abstract void onBackClick();
}
