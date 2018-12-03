package com.bazooka.bluetoothbox.ui.activity;

import android.support.v4.app.Fragment;
import android.view.View;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.MusicCommonActivity;
import com.bazooka.bluetoothbox.ui.fragment.AUXPlayControlFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：AUX播放界面
 */

public class AuxModeActivity extends MusicCommonActivity {

    private List<Fragment> fragments;


    @Override
    public void initData() {
        fragments = new ArrayList<>();
        fragments.add(new AUXPlayControlFragment());

    }

    @Override
    public List<Fragment> setViewPagerContent() {
        return fragments;
    }

    @Override
    public int setBackgroundResId() {
        return R.drawable.bg_purple;
    }

    @Override
    public int setIconRes() {
        return R.drawable.ic_aux;
    }

    @Override
    public void initViews() {
        ivIndicator1.setVisibility(View.GONE);
        ivIndicator2.setVisibility(View.GONE);
    }


    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void addViewListener() {

    }

}
