package com.bazooka.bluetoothbox.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actions.ibluz.manager.BluzManagerData;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.fragment.BaseFragment;
import com.bazooka.bluetoothbox.bean.event.UsbMusicScanSuccessEvent;
import com.bazooka.bluetoothbox.ui.adapter.UsbMusicAdapter;

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
 * 作用：Usb播放，音乐列表界面
 */

public class UsbMusicListFragment extends BaseFragment {

    @BindView(R.id.rv_music_list)
    RecyclerView rvMusicList;

    private List<BluzManagerData.PListEntry> musicList = new ArrayList<>();
    private UsbMusicAdapter mAdapter;
    private OnMusicItemClickListener mOnMusicItemClickListener;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void initData() {
        mAdapter = new UsbMusicAdapter(musicList);
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
            if(mOnMusicItemClickListener != null) {
                mOnMusicItemClickListener.onItemClick(musicList.get(position).index);
            }
        });
    }


    /**
     * 音乐列表搜索完成
     * 由 {@link com.bazooka.bluetoothbox.ui.activity.UsbModeActivity.MusicHandler 89行} 发送
     * @param event 事件
     */
    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void addMusicList(UsbMusicScanSuccessEvent event) {
        if(event.getMusicLisc() != null) {
            musicList.clear();
            musicList.addAll(event.getMusicLisc());
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void setOnMusicItemClickListener(OnMusicItemClickListener l){
        mOnMusicItemClickListener = l;
    }

    public interface OnMusicItemClickListener{
        void onItemClick(int index);
    }
}
