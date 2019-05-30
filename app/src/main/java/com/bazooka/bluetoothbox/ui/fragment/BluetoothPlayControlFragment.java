package com.bazooka.bluetoothbox.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.fragment.BaseFragment;
import com.bazooka.bluetoothbox.bean.Music;
import com.bazooka.bluetoothbox.bean.event.PlayEvent;
import com.bazooka.bluetoothbox.bean.event.VolumeChangedEvent;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.ui.activity.BluetoothMusicActivity;
import com.bazooka.bluetoothbox.utils.DateUtils;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：蓝牙音乐播放控制页面
 */

public class BluetoothPlayControlFragment extends BaseFragment {

    @BindView(R.id.sb_volume)
    SeekBar sbVolume;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_music_time)
    TextView tvMusicTime;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_pre)
    ImageView ivPre;
    @BindView(R.id.iv_next)
    ImageView ivNext;

    private Music currMusic;

    private boolean isPlaying;


    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_control, container, false);
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);

        int volume = SpManager.getInstance().getCurrentVolume();
        int maxVolume = SpManager.getInstance().getMaxVolume();

        sbVolume.setMax(maxVolume);
        sbVolume.setProgress(maxVolume);
        sbVolume.setProgress(volume);


    }

    @Override
    public void initView() {
        ivPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    public void addViewListener() {
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BluzManagerUtils.getInstance().setVolume(seekBar.getProgress());
            }
        });
    }

    @OnClick({R.id.iv_play, R.id.iv_pre, R.id.iv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                if (MusicCache.getPlayService().getPlayingMusic() == null) {
                    MusicCache.getPlayService().play(0);
                } else {
                    MusicCache.getPlayService().playPause();
                }
                break;
            case R.id.iv_pre:
                MusicCache.getPlayService().prev();
                break;
            case R.id.iv_next:
                MusicCache.getPlayService().next();
                break;
            default:
                break;
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void playStateChanged(BluetoothMusicActivity.MusicStateChangeEvent event) {
        isPlaying = event.isPlaying();
        ivPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void updateMusicInfo(Music music) {
        currMusic = music;
        tvName.setText(music.getArtist());
        tvInfo.setText(music.getFileName());
        tvMusicTime.setText(DateUtils.millToString(music.getDuration()));
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onVolumeChange(VolumeChangedEvent event) {
        int volume = event.getVolume();
        boolean mute = event.isMute();

        sbVolume.setProgress(volume);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
