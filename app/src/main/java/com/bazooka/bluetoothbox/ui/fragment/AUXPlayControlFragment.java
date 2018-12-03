package com.bazooka.bluetoothbox.ui.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actions.ibluz.manager.BluzManagerData;
import com.actions.ibluz.manager.IAuxManager;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.fragment.BaseFragment;
import com.bazooka.bluetoothbox.bean.event.VolumeChangedEvent;
import com.bazooka.bluetoothbox.ui.activity.MainActivity;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：AUX 播放控制页面
 */
public class AUXPlayControlFragment extends BaseFragment {


    @BindView(R.id.tv_music_state)
    TextView tvMusicState;
    @BindView(R.id.iv_volume_control)
    ImageView ivVolumeControl;
    @BindView(R.id.sb_volume)
    SeekBar sbVolume;

    private BluzManagerUtils mBluzManagerUtils;
    private IAuxManager mAuxManager;
    private PromptDialogV2 hintDialog;
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.fragment_aux_control, container, false);
    }

    @Override
    public void initData() {
        hintDialog = DialogUtils.createNoConnectedDialog(getContext(), new PromptDialogV2.OnButtonClickListener() {
            @Override
            public void onPositiveClick() {
                hintDialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                MainActivity.showActivity(getContext(), true);
                hintDialog.dismiss();
            }
        });
        mBluzManagerUtils = BluzManagerUtils.getInstance();
        mBluzManagerUtils.setMode(BluzManagerData.FuncMode.LINEIN);

        mAuxManager = mBluzManagerUtils.getAuxManager(() -> {
            mAuxManager.setOnAuxUIChangedListener(state -> {
                if (state == BluzManagerData.PlayState.PAUSED) {
                    //暂停
                    ivVolumeControl.setImageResource(R.drawable.ic_volume_mute);
                    tvMusicState.setText(R.string.aux_mute);
                    tvMusicState.setTextColor(ContextCompat.getColor(mContext, R.color.auxMute));
                }else {
                    //播放
                    ivVolumeControl.setImageResource(R.drawable.ic_volume_normal);
                    tvMusicState.setText(R.string.aux_playing);
                    tvMusicState.setTextColor(ContextCompat.getColor(mContext, R.color.auxPlaying));

                }
            });
        });
    }

    @Override
    public void initView() {

        int volume = SpManager.getInstance().getCurrentVolume();
        int maxVolume = SpManager.getInstance().getMaxVolume();

        sbVolume.setMax(maxVolume);
        sbVolume.setProgress(maxVolume);
        sbVolume.setProgress(volume);

    }

    @Override
    public void addViewListener() {
        ivVolumeControl.setOnClickListener(v -> {

            if(BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                hintDialog.show();
                return;
            }

            mAuxManager.mute();
        });

        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sbVolume.setPressed(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBluzManagerUtils.setVolume(seekBar.getProgress());
            }
        });


    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVolumeChange(VolumeChangedEvent event) {
        int volume = event.getVolume();
        boolean mute = event.isMute();

        sbVolume.setProgress(volume);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if(mAuxManager != null) {
            mAuxManager.setOnAuxUIChangedListener(null);
            mAuxManager = null;
        }
    }
}
