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
import com.bazooka.bluetoothbox.bean.event.VolumeChangedEvent;
import com.bazooka.bluetoothbox.ui.activity.FmModeActivity;
import com.bazooka.bluetoothbox.ui.view.SimpleRulerView;
import com.bazooka.bluetoothbox.utils.SpManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：FM 播放控制页面
 */

public class FmControlFragment extends BaseFragment {
    private static final String ATTR_CUR_CHANNEL = "FmControlFragment.CurrentChannel";

    @BindView(R.id.tv_fm_channel)
    TextView tvFmChannel;
    @BindView(R.id.ruler_fm)
    SimpleRulerView rulerFm;
    @BindView(R.id.iv_pre)
    ImageView ivPre;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.iv_micro_pre)
    ImageView ivMicroPre;
    @BindView(R.id.iv_micro_next)
    ImageView ivMicroNext;
    @BindView(R.id.sb_volume)
    SeekBar sbVolume;
    @BindView(R.id.iv_fm_add)
    ImageView ivFmAdd;



    private OnButtonClickListener mOnButtonClickListener;
    private OnUiChangeListener mOnUiChangeListener;
    private int mCurChannel = 87500;

    public static FmControlFragment newInstance(int curChannel) {
        FmControlFragment fragment = new FmControlFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ATTR_CUR_CHANNEL, curChannel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fm_controler, container, false);
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

        rulerFm.setSelectedValue(mCurChannel / 1000f);
        tvFmChannel.setText(getString(R.string.fm_channel, mCurChannel / 1000f));
    }

    @Override
    public void addViewListener() {
        rulerFm.setOnValueChangeListener(new SimpleRulerView.OnValueChangeListener() {
            @Override
            public void onChange(SimpleRulerView view, int position, float value) {
                tvFmChannel.setText(getString(R.string.fm_channel, value));
            }

            @Override
            public void onChangeFinished(SimpleRulerView view, int position, float value) {
                if (mOnUiChangeListener != null) {
                    mOnUiChangeListener.onChannelChangeFinished(value);
                }
            }
        });


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


    @OnClick({R.id.iv_scan, R.id.iv_pre, R.id.iv_next, R.id.iv_micro_pre, R.id.iv_micro_next,R.id.iv_fm_add})
    public void onViewClicked(View view) {
        if (mOnButtonClickListener != null) {
            switch (view.getId()) {
                case R.id.iv_scan:
                    mOnButtonClickListener.onScanClick();
                    break;
                case R.id.iv_pre:
                    mOnButtonClickListener.onPreClick();
                    break;
                case R.id.iv_next:
                    mOnButtonClickListener.onNextClick();
                    break;
                case R.id.iv_micro_pre:
                    mOnButtonClickListener.onMicroPreClick();
                    break;
                case R.id.iv_micro_next:
                    mOnButtonClickListener.onMicroNextClick();
                    break;
                case R.id.iv_fm_add:
                    mOnButtonClickListener.onAddFmClick();
                default:
                    break;
            }
        }
    }

    public void setCurrentChannel(int channel) {
        mCurChannel = channel;
        rulerFm.setSelectedValue(channel / 1000f);
        tvFmChannel.setText(getString(R.string.fm_channel, mCurChannel / 1000f));
    }

    public int getCurrentChannel(){
        return mCurChannel;
    }

    public void setOnButtonClickListener(OnButtonClickListener l) {
        this.mOnButtonClickListener = l;
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVolumeChange(VolumeChangedEvent event) {
        int volume = event.getVolume();
        boolean mute = event.isMute();

        sbVolume.setProgress(volume);
    }

    @SuppressWarnings("unused")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void currentChannel(FmModeActivity.CurrentChannel channel) {
        int currentChannel = channel.getChannel();
        setCurrentChannel(currentChannel < 87500 ? 87500 : currentChannel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }

    public void setOnUiChangeListener(OnUiChangeListener l) {
        this.mOnUiChangeListener = l;
    }

    public interface OnButtonClickListener {
        void onScanClick();

        void onPreClick();

        void onNextClick();

        void onMicroPreClick();

        void onMicroNextClick();

        void onAddFmClick();
    }

    public interface OnUiChangeListener {
        void onChannelChangeFinished(float value);

        void onVolumeChanged(int volume);

    }

}
