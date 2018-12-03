package com.bazooka.bluetoothbox.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.fragment.BaseFragment;
import com.bazooka.bluetoothbox.cache.db.FmChannelCacheHelper;
import com.bazooka.bluetoothbox.cache.db.entity.FmChannelCache;
import com.bazooka.bluetoothbox.listener.FmItemDleteListener;
import com.bazooka.bluetoothbox.ui.adapter.FmChannelAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：FM 列表面
 */
public class FMListFragment extends BaseFragment {

    private static final String ATTR_CHANNELS = "FMListFragment.Channels";
    private boolean isPlaying = true;
    private OnChannelClickListener onChannelClickListener;

    @BindView(R.id.rv_music_list)
    RecyclerView rvMusicList;

    private FmChannelAdapter adapter;
    private List<FmChannelCache> mFmChannels = new ArrayList<>();

    public static FMListFragment newInstance(List<FmChannelCache> fmChannelCaches){
        FMListFragment fragment = new FMListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ATTR_CHANNELS, new ArrayList<>(fmChannelCaches));
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_list, container, false);
    }

    @Override
    public void initData() {
        List<FmChannelCache> channels = getArguments().getParcelableArrayList(ATTR_CHANNELS);
        if(channels != null) {
            mFmChannels.addAll(channels);
        }
        adapter = new FmChannelAdapter(getContext(), mFmChannels);

        adapter.setFmitemDeleteListener(new FmItemDleteListener() {
            @Override
            public void delete(int position) {
                FmChannelCache channelCache = mFmChannels.get(position);
                FmChannelCacheHelper.getInstance().deleteCache(channelCache);
                mFmChannels.remove(position);
                adapter.notifyDataSetChanged();
                //删除数据库记录
            }
        });
    }


    @Override
    public void initView() {

        rvMusicList.setLayoutManager(new LinearLayoutManager(mContext));
        rvMusicList.setAdapter(adapter);
    }

    public void setFmChannels(List<FmChannelCache> fmChannels){
        mFmChannels.clear();
        mFmChannels.addAll(fmChannels);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addViewListener() {
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if(onChannelClickListener != null) {
                onChannelClickListener.onChannelClick(mFmChannels.get(position).getChannel());
            }
        });
    }

    public void setOnChannelClickListener(OnChannelClickListener l) {
        onChannelClickListener = l;
    }

    public interface OnChannelClickListener {
        void onChannelClick(int channel);
    }

}
