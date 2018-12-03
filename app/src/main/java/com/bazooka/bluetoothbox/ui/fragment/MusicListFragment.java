package com.bazooka.bluetoothbox.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.fragment.BaseFragment;
import com.bazooka.bluetoothbox.bean.Music;
import com.bazooka.bluetoothbox.bean.event.LocalMusicScanSuccessEvent;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.ui.adapter.BluetoothMusicAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：蓝牙播放，音乐列表界面
 */

public class MusicListFragment extends BaseFragment {


    @BindView(R.id.rv_music_list)
    RecyclerView rvMusicList;

    private BluetoothMusicAdapter mAdapter;
    private List<Music> mMusicList = new ArrayList<>();

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void initData() {
        mAdapter = new BluetoothMusicAdapter(mMusicList);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initView() {
        rvMusicList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        rvMusicList.setLayoutManager(new LinearLayoutManager(mContext));
        rvMusicList.setAdapter(mAdapter);
    }

    @Override
    public void addViewListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            MusicCache.getPlayService().play(position);
        });
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateMusicList(LocalMusicScanSuccessEvent event) {
        mMusicList.clear();
        mMusicList.addAll(event.getMusicList());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
