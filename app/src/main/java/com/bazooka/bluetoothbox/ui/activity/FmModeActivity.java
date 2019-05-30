package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.actions.ibluz.manager.BluzManagerData;
import com.actions.ibluz.manager.IRadioManager;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.MusicCommonActivity;
import com.bazooka.bluetoothbox.bean.event.ModeChangedEvent;
import com.bazooka.bluetoothbox.cache.db.FmChannelCacheHelper;
import com.bazooka.bluetoothbox.cache.db.entity.FmChannelCache;
import com.bazooka.bluetoothbox.listener.FmModeCallback;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：FM播放界面
 */

public class FmModeActivity extends MusicCommonActivity implements FmModeCallback {
    private String TAG = "FmModeActivity";
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
    private int currPlayChannell = 87500;

    private int getCurPlayChannel() {
        return currPlayChannell;
    }

    private void setCurPlayChannel(int curPlayChannel) {
        this.currPlayChannell = curPlayChannel;
    }

    private PromptDialogV2 hintDialog;
    private int channels;

    private boolean addClick = false;

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
        fragments.add(fmListFragment = FMListFragment.newInstance(fmChannels, this));

        mProgressDialog = new ProgressDialog(mContext);
        channels = fmControlFragment.getCurrentChannel();
        mBluzManagerUtils = BluzManagerUtils.getInstance();
        if (mBluzManagerUtils.getCurrentMode() == BluzManagerData.FuncMode.RADIO) {
            Log.i(TAG, "mBluzManagerUtils is RADIO model");
            initRadioManager();
        } else {
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.show();
            mBluzManagerUtils.setMode(BluzManagerData.FuncMode.RADIO);
            Log.i(TAG, "mBluzManagerUtils setMode RADIO");
        }
        Log.i(TAG, "fmChannels = " + fmChannels.size());
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
                    setCurPlayChannel(channel);
                    channels = channel;
                    Log.v(TAG, " onPreClick " + channel);
                    fmControlFragment.setCurrentChannel(channel);
                    mRadioManager.select(channel);
                    fmListFragment.select(channel);
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
                    setCurPlayChannel(channel);
                    channels = channel;
                    Log.v(TAG, " onNextClick " + channel);
                    fmControlFragment.setCurrentChannel(channel);
                    mRadioManager.select(channel);
                    fmListFragment.select(channel);
                }
            }

            //向前微调
            @Override
            public void onMicroPreClick() {
                Log.v(TAG, " onMicroPreClick " + getCurPlayChannel());
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }

                float selectedValue = (getCurPlayChannel() - 100) / 1000f;

                if (selectedValue < fmControlFragment.getMinValue()
                        || selectedValue > fmControlFragment.getMaxValue()) {

                    Log.w(TAG, "expected selectedValue in ["
                            + fmControlFragment.getMinValue() + "," + fmControlFragment.getMaxValue()
                            + "],but the selectedValue is " + selectedValue);

                    int currPlayChannel = (int) (fmControlFragment.getMinValue() * 1000);
                    channels = currPlayChannel;
                    Log.d(TAG, " rest currPlayChannel " + currPlayChannel);
                    setCurPlayChannel(currPlayChannel);
                    fmControlFragment.setCurrentChannel(currPlayChannel);
                    mRadioManager.select(currPlayChannel);
                    fmListFragment.select(currPlayChannel);
                    return;
                }
                int curPlayChannel = getCurPlayChannel();
                curPlayChannel -= 100;
                setCurPlayChannel(curPlayChannel);
                Log.v(TAG, " onMicroPreClick -= 100:" + curPlayChannel);
                channels = curPlayChannel;
                fmControlFragment.setCurrentChannel(curPlayChannel);
                mRadioManager.select(curPlayChannel);
                fmListFragment.select(curPlayChannel);
            }

            //向后微调
            @Override
            public void onMicroNextClick() {
                Log.v(TAG, " onMicroNextClick :" + getCurPlayChannel());
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }

                float selectedValue = (getCurPlayChannel() + 100) / 1000f;

                if (selectedValue < fmControlFragment.getMinValue()
                        || selectedValue > fmControlFragment.getMaxValue()) {

                    Log.w(TAG, "expected selectedValue in ["
                            + fmControlFragment.getMinValue() + "," + fmControlFragment.getMaxValue()
                            + "],but the selectedValue is " + selectedValue);

                    int currPlayChannel = (int) (fmControlFragment.getMaxValue() * 1000);
                    Log.d(TAG, " rest currPlayChannel " + currPlayChannel);
                    channels = currPlayChannel;
                    setCurPlayChannel(currPlayChannel);
                    fmControlFragment.setCurrentChannel(currPlayChannel);
                    mRadioManager.select(currPlayChannel);
                    fmListFragment.select(currPlayChannel);
                    return;
                }

                int curPlayChannel = getCurPlayChannel();
                curPlayChannel += 100;
                setCurPlayChannel(curPlayChannel);
                Log.v(TAG, " onMicroNextClick += 100:" + curPlayChannel);
                channels = curPlayChannel;
                fmControlFragment.setCurrentChannel(curPlayChannel);
                mRadioManager.select(curPlayChannel);
                fmListFragment.select(curPlayChannel);
            }

            //添加FM频道
            @Override
            public void onAddFmClick() {
                Log.v(TAG, " onAddFmClick ");
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }

                //判断是否添加重复数据
                for (int i = 0; i < fmChannels.size(); i++) {
                    if (fmChannels.get(i).getChannel() == channels) {
                        Log.d(TAG, " repeat add ");
                        return;
                    }
                }

                int size = fmChannels.size();
                //添加数据库记录
                List<FmChannelCache> fmChannelCaches = FmChannelCacheHelper.getInstance().addCache(size, channels);
                fmChannels.addAll(fmChannelCaches);//添加到列表中
                //排序
                Collections.sort(fmChannels, new sortByFmChannles());
                fmListFragment.setFmChannels(fmChannels, true);
                fmListFragment.select(getCurPlayChannel());
                Log.i(TAG, "onAddFmClick fmChannels = " + fmChannels.size());

            }
        });

        fmControlFragment.setOnUiChangeListener(new FmControlFragment.OnUiChangeListener() {
            @Override
            public void onChannelChangeFinished(float value) {
                Log.i(TAG, "onChannelChangeFinished = " + value);
                channels = (int) (value * 1000);
                setCurPlayChannel(channels);
                if (fmListFragment != null) {
                    fmListFragment.select(channels);
                }
                if (fmControlFragment != null) {
                    fmControlFragment.setCurrentChannel(channels);
                }
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
            Log.i(TAG, "selectChannel = " + channel);
            fmControlFragment.setCurrentChannel(channel);

            if (mRadioManager != null) {
                mRadioManager.select(channel);
            }

        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        int model = mBluzManagerUtils.getCurrentMode();
        Log.v(TAG, " onResume: getCurPlayChannel:" + model);

        if (model != BluzManagerData.FuncMode.RADIO) {
            mBluzManagerUtils.setMode(BluzManagerData.FuncMode.RADIO);
        }

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
            Log.v(TAG, " onChannelChanged: currPlayChannel:" + channel);
            if (fmControlFragment != null
                    && channel > (fmControlFragment.getMinValue() * 1000)
                    && channel < (fmControlFragment.getMaxValue() * 1000)) {
                setCurPlayChannel(channel);
            } else {
                Log.e(TAG, " onChannelChanged out of range");
            }
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
            fmListFragment.setFmChannels(channels, true);
            fmListFragment.select(getCurPlayChannel());
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
            if (channel.getChannel() > getCurPlayChannel()) {
                if (next) {
                    return i;
                } else {
                    int temp = i - 1;
                    return temp < 0 ? length - 1 : temp;
                }
            } else if (channel.getChannel() == getCurPlayChannel()) {
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

    @Override
    public void deleteCallback(int position) {
        if (fmChannels != null) {
            fmChannels.remove(position);
        }
        Log.i(TAG, "删除第" + position + "个");
        Log.i(TAG, "删除后的大小" + fmChannels.size());
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
        Log.v(TAG, " onModeChanged: :" + event.getMode());
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
            int currPlayChannel = mRadioManager.getCurrentChannel();
            Log.v(TAG, " initRadioManager finish: currPlayChannel :" + currPlayChannel);
            EventBus.getDefault().postSticky(new CurrentChannel(currPlayChannel));
        });
        Log.v(TAG, " initRadioManager finish: initRadioManager=null? :" + (mRadioManager == null));
    }

    /**
     * 对集合进行排序
     */
    class sortByFmChannles implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            FmChannelCache fm1 = (FmChannelCache) o1;
            FmChannelCache fm2 = (FmChannelCache) o2;
            if (fm1.getChannel() > fm2.getChannel()) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
