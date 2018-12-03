package com.bazooka.bluetoothbox.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actions.ibluz.manager.BluzManagerData;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.fragment.BaseFragment;
import com.bazooka.bluetoothbox.bean.event.UsbMusicChangeEvent;
import com.bazooka.bluetoothbox.bean.event.UsbPlayStateChangeEvent;
import com.bazooka.bluetoothbox.bean.event.VolumeChangedEvent;
import com.bazooka.bluetoothbox.utils.DateUtils;
import com.bazooka.bluetoothbox.utils.SpManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/9/14
 *         作用：USB 播放 Fragment
 */

public class UsbPlayControlFragment extends BaseFragment {

    @BindView(R.id.sb_volume)
    SeekBar sbVolume;
    @BindView(R.id.tv_name)
    TextView tvSingerName;
    @BindView(R.id.tv_info)
    TextView tvMusicName;
    @BindView(R.id.tv_music_time)
    TextView tvMusicTime;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_pre)
    ImageView ivPre;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    private int mCurVolume;
    private int mMaxVolume;
    private OnUiChangeListener mOnUiChangeListener;
    private OnButtonClick mOnButtonClick;

    private boolean isPlaying = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_music_control, container, false);
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        mCurVolume = SpManager.getInstance().getCurrentVolume();
        mMaxVolume = SpManager.getInstance().getMaxVolume();

    }

    @Override
    public void initView() {
        sbVolume.setMax(mMaxVolume);
        sbVolume.setProgress(mMaxVolume);
        sbVolume.setProgress(mCurVolume);
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
                if (mOnUiChangeListener != null) {
                    mOnUiChangeListener.onVolumeChanged(seekBar.getProgress());
                }
            }
        });
    }


    @OnClick({R.id.iv_play, R.id.iv_pre, R.id.iv_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                if (mOnButtonClick != null) {
                    mOnButtonClick.onPlayClick(isPlaying);
                }
                break;
            case R.id.iv_pre:
                if (mOnButtonClick != null) {
                    mOnButtonClick.onPreClick();
                }
                break;
            case R.id.iv_next:
                if (mOnButtonClick != null) {
                    mOnButtonClick.onNextClick();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 播放状态改变
     *
     * @param event 事件
     */
    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPlayStateChanged(UsbPlayStateChangeEvent event) {
        int state = event.getState();
        isPlaying = state == BluzManagerData.PlayState.PLAYING;
        ivPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }


    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVolumeChange(VolumeChangedEvent event) {
        int volume = event.getVolume();
        sbVolume.setProgress(volume);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMusicChang(UsbMusicChangeEvent event) {
        tvSingerName.setText(event.getArtist());
        tvMusicName.setText(event.getMusicName());
        tvMusicTime.setText(DateUtils.millToString(event.getDuration()));
    }

    public void setOnUiChangeListener(OnUiChangeListener l) {
        mOnUiChangeListener = l;
    }

    public void setOnButtonClick(OnButtonClick l) {
        mOnButtonClick = l;
    }

    public interface OnUiChangeListener {
        void onVolumeChanged(int volume);
    }

    public interface OnButtonClick {
        void onPlayClick(boolean isPlaying);

        void onPreClick();

        void onNextClick();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
