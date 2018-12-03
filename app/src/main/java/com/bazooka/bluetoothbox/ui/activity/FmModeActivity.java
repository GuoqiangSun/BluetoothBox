package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import com.actions.ibluz.manager.BluzManagerData;
import com.actions.ibluz.manager.IRadioManager;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.MusicCommonActivity;
import com.bazooka.bluetoothbox.bean.event.ModeChangedEvent;
import com.bazooka.bluetoothbox.cache.db.FmChannelCacheHelper;
import com.bazooka.bluetoothbox.cache.db.entity.FmChannelCache;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.ui.fragment.FMListFragment;
import com.bazooka.bluetoothbox.ui.fragment.FmControlFragment;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.GsonUtils;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：FM播放界面
 */

public class FmModeActivity extends MusicCommonActivity {

    private boolean canScan = false;
    private boolean isScan = false;
    private List<FmChannelCache> fmChannels = new ArrayList<>();

    private List<Fragment> fragments;
    private BluzManagerUtils mBluzManagerUtils;
    private IRadioManager mRadioManager;

    private ProgressDialog mProgressDialog;
    private FmControlFragment fmControlFragment;
    private FMListFragment fmListFragment;

    /**
     * 当前播放频道
     */
    private int currPlayChannel = 87500;
    private PromptDialogV2 hintDialog;
    private int channels;

    @Override
    public void initData() {

        hintDialog = DialogUtils.createNoConnectedDialog(mContext, new PromptDialogV2.OnButtonClickListener() {
            @Override
            public void onPositiveClick() {
                hintDialog.dismiss();
            }

            @Override
            public void onNegativeClick() {
                MainActivity.showActivity(mContext, true);
                hintDialog.dismiss();
            }
        });

        fmChannels.addAll(FmChannelCacheHelper.getInstance().getAllCacheChannel());
        fragments = new ArrayList<>();
        fragments.add(fmControlFragment = new FmControlFragment());
        fragments.add(fmListFragment = FMListFragment.newInstance(fmChannels));

        mProgressDialog = new ProgressDialog(mContext);
        channels=fmControlFragment.getCurrentChannel();
        mBluzManagerUtils = BluzManagerUtils.getInstance();
        if (mBluzManagerUtils.getCurrentMode() == BluzManagerData.FuncMode.RADIO) {
            initRadioManager();
        } else {
            mProgressDialog.setMessage(getString(R.string.loading));
            mBluzManagerUtils.setMode(BluzManagerData.FuncMode.RADIO);
        }

    }

    @Override
    public List<Fragment> setViewPagerContent() {
        return fragments;
    }

    @Override
    public int setBackgroundResId() {
        return R.drawable.bg_green;
    }

    @Override
    public int setIconRes() {
        return R.drawable.ic_fm;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void addViewListener() {

        mProgressDialog.setOnDismissListener(dialog -> {
            if (isScan) {
                isScan = false;
                mRadioManager.cancelScan();
            }
        });

        fmControlFragment.setOnButtonClickListener(new FmControlFragment.OnButtonClickListener() {
            //扫描按钮点击
            @Override
            public void onScanClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                if (!canScan) {
                    return;
                }
                mProgressDialog.setMessage(getString(R.string.scan_fm_channels_hint));
                isScan = true;
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                mRadioManager.scan();
            }

            //上一频道
            @Override
            public void onPreClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                if (fmChannels.size() > 0) {
                    int index = findNextOrPreChannel(false);
                    int channel = fmChannels.get(index).getChannel();
                    fmControlFragment.setCurrentChannel(channel);
                    mRadioManager.select(channel);
                }
            }

            //下一频道
            @Override
            public void onNextClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                if (fmChannels.size() > 0) {
                    int index = findNextOrPreChannel(true);
                    int channel = fmChannels.get(index).getChannel();
                    fmControlFragment.setCurrentChannel(channel);
                    mRadioManager.select(channel);
                }
            }

            //向前微调
            @Override
            public void onMicroPreClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                currPlayChannel -= 100;
                fmControlFragment.setCurrentChannel(currPlayChannel);
                mRadioManager.select(currPlayChannel);
            }

            //向后微调
            @Override
            public void onMicroNextClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                currPlayChannel += 100;
                fmControlFragment.setCurrentChannel(currPlayChannel);
                mRadioManager.select(currPlayChannel);
            }

            //添加FM频道
            @Override
            public void onAddFmClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                int size = fmChannels.size();
                //添加数据库记录
                List<FmChannelCache> fmChannelCaches = FmChannelCacheHelper.getInstance().addCache(size, channels);
                fmChannels.addAll(fmChannelCaches);//添加到列表中
                fmListFragment.setFmChannels(fmChannelCaches);

            }
        });

        fmControlFragment.setOnUiChangeListener(new FmControlFragment.OnUiChangeListener() {
            @Override
            public void onChannelChangeFinished(float value) {
                 channels = (int) (value * 1000);
                if (mRadioManager != null) {
                    mRadioManager.select(channels);
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
                mBluzManagerUtils.setVolume(volume);
            }
        });

        fmListFragment.setOnChannelClickListener(channel -> {
            if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                hintDialog.show();
                return;
            }
            fmControlFragment.setCurrentChannel(currPlayChannel);
            mRadioManager.select(channel);
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private BluzManagerData.OnRadioUIChangedListener mRadioUIChangedListener = new BluzManagerData.OnRadioUIChangedListener() {

        @Override
        public void onStateChanged(int i) {
            switch (i) {
                case BluzManagerData.PlayState.PLAYING:
                    canScan = true;
                    break;
                case BluzManagerData.PlayState.PAUSED:
                    canScan = false;
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onChannelChanged(int channel) {
            currPlayChannel = channel;
//            fmControlFragment.setCurrentChannel(currPlayChannel);
        }

        @Override
        public void onBandChanged(int i) {

        }
    };

    /**
     * 扫描成功
     */
    private BluzManagerData.OnScanCompletionListener mScanCompletionListener = new BluzManagerData.OnScanCompletionListener() {

        @Override
        public void onCompletion(List<BluzManagerData.RadioEntry> list) {

            Logger.json(GsonUtils.getInstance().toJson(list));

            List<FmChannelCache> channels = FmChannelCacheHelper.getInstance().addCache(list, true);
            fmChannels.clear();
            fmChannels.addAll(channels);
            fmListFragment.setFmChannels(channels);
            isScan = false;
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (fmChannels.size() == 0) {
                ToastUtils.showShortToast(R.string.fm_no_channels_found);
            } else {
                ToastUtils.showShortToast(R.string.fm_scanned_successfully);
            }
        }
    };

    /**
     * 查询上一个或下一个频道
     *
     * @param next 是否下一个
     */
    private int findNextOrPreChannel(boolean next) {
        for (int i = 0, length = fmChannels.size(); i < length; i++) {
            FmChannelCache channel = fmChannels.get(i);
            if (channel.getChannel() > currPlayChannel) {
                if (next) {
                    return i;
                } else {
                    int temp = i - 1;
                    return temp < 0 ? length - 1 : temp;
                }
            } else if (channel.getChannel() == currPlayChannel) {
                if (next) {
                    return (i + 1) % length;
                } else {
                    int temp = i - 1;
                    return temp < 0 ? length - 1 : temp;
                }
            }
        }

        return 0;
    }

    public class CurrentChannel {
        int channel;

        public CurrentChannel(int channel) {
            this.channel = channel;
        }

        public int getChannel() {
            return channel;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRadioManager != null) {
            mRadioManager.setOnScanCompletionListener(null);
            mRadioManager.setOnRadioUIChangedListener(null);
            mRadioManager = null;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModeChanged(ModeChangedEvent event) {
        if (event.getMode() == BluzManagerData.FuncMode.RADIO) {
            initRadioManager();
        }
    }

    private void initRadioManager() {
        mRadioManager = mBluzManagerUtils.getRadioManager(() -> {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mRadioManager.setOnRadioUIChangedListener(mRadioUIChangedListener);
            mRadioManager.setOnScanCompletionListener(mScanCompletionListener);

            currPlayChannel = mRadioManager.getCurrentChannel();
            Logger.d(currPlayChannel);
            EventBus.getDefault().postSticky(new CurrentChannel(currPlayChannel));
        });
    }
}
