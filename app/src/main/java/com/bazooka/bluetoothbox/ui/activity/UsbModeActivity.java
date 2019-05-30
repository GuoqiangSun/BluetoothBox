package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.actions.ibluz.manager.BluzManagerData;
import com.actions.ibluz.manager.IMusicManager;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.MusicCommonActivity;
import com.bazooka.bluetoothbox.bean.event.ModeChangedEvent;
import com.bazooka.bluetoothbox.bean.event.UsbMusicChangeEvent;
import com.bazooka.bluetoothbox.bean.event.UsbMusicScanSuccessEvent;
import com.bazooka.bluetoothbox.bean.event.UsbPlayStateChangeEvent;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.ui.fragment.UsbMusicListFragment;
import com.bazooka.bluetoothbox.ui.fragment.UsbPlayControlFragment;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：USB播放界面
 */
public class UsbModeActivity extends MusicCommonActivity {

    /**
     * 获取USB音乐列表
     */
    private static final int MESSAGE_GET_PLISTENTRY = 1;

    private UsbPlayControlFragment usbFragment;
    private UsbMusicListFragment usbMusicListFragment;
    private List<Fragment> fragments;
    private BluzManagerUtils mBluzManagerUtils;

    private ProgressDialog progressDialog;
    private IMusicManager mMusicManager;
    private MusicHandler mHandler;
    private PromptDialogV2 hintDialog;


    private static class MusicHandler extends Handler {
        private final WeakReference<UsbModeActivity> activityWeakReference;
        private List<BluzManagerData.PListEntry> mPListEntryList = new ArrayList<>();
        private IMusicManager musicManager;
        private BluzManagerData.OnPListEntryReadyListener onPListEntryReadyListener;

        MusicHandler(UsbModeActivity activity, IMusicManager musicManager) {
            super(Looper.getMainLooper());
            activityWeakReference = new WeakReference<>(activity);
            this.musicManager = musicManager;

            onPListEntryReadyListener = list -> {
                UsbModeActivity usbActivity = activityWeakReference.get();
                if (usbActivity != null) {
                    mPListEntryList.addAll(list);
                    usbActivity.setProgressDialogMessage(usbActivity.getString(
                            R.string.notice_usb_music_loading_message,
                            mPListEntryList.size(), musicManager.getPListSize()));
                    MusicHandler.this.sendEmptyMessage(MESSAGE_GET_PLISTENTRY);
                }
            };
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UsbModeActivity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MESSAGE_GET_PLISTENTRY:
                        int musicSize = musicManager.getPListSize();
                        if (mPListEntryList.size() < musicSize) {
                            int surplus = musicSize - mPListEntryList.size();
                            musicManager.getPList(mPListEntryList.size() + 1, surplus > 5 ? 5 : surplus,
                                    onPListEntryReadyListener);
                        } else {
                            MusicCache.getInstance().getUsbMusicList().clear();
                            MusicCache.getInstance().getUsbMusicList().addAll(mPListEntryList);
                            activity.dismissProgressDialog();
                            //To UsbMusicListFragment.addMusicList
                            EventBus.getDefault().postSticky(new UsbMusicScanSuccessEvent(mPListEntryList));
                        }
                        break;
                    default:
                        break;
                }
            }

        }
    }


    @Override
    public void initData() {
//        EventBus.getDefault().register(this);

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

        fragments = new ArrayList<>();
        fragments.add(usbFragment = new UsbPlayControlFragment());
        fragments.add(usbMusicListFragment = new UsbMusicListFragment());
        mBluzManagerUtils = BluzManagerUtils.getInstance();

        progressDialog = new ProgressDialog(mContext);


        if (mBluzManagerUtils.getCurrentMode() == BluzManagerData.FuncMode.USB) {
            initUsbMode();
        } else {
            mBluzManagerUtils.setMode(BluzManagerData.FuncMode.USB);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public List<Fragment> setViewPagerContent() {
        return fragments;
    }

    @Override
    public int setBackgroundResId() {
        return R.drawable.bg_red;
    }

    @Override
    public int setIconRes() {
        return R.drawable.ic_usb;
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
        usbFragment.setOnUiChangeListener(volume -> mBluzManagerUtils.setVolume(volume));

        usbFragment.setOnButtonClick(new UsbPlayControlFragment.OnButtonClick() {
            @Override
            public void onPlayClick(boolean isPlaying) {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                } else if (!mBluzManagerUtils.isUhostEnable()) {
                    ToastUtils.showShortToast(R.string.no_usb);
                    return;
                }
                if (isPlaying) {
                    mMusicManager.pause();
                } else {
                    mMusicManager.play();
                }
                int currentPosition = mMusicManager.getCurrentPosition();
                usbMusicListFragment.play(currentPosition);
            }

            @Override
            public void onPreClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                } else if (!mBluzManagerUtils.isUhostEnable()) {
                    ToastUtils.showShortToast(R.string.no_usb);
                    return;
                }
                mMusicManager.previous();
                int currentPosition = mMusicManager.getCurrentPosition();
                usbMusicListFragment.play(currentPosition);
            }

            @Override
            public void onNextClick() {
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                } else if (!mBluzManagerUtils.isUhostEnable()) {
                    ToastUtils.showShortToast(R.string.no_usb);
                    return;
                }
                mMusicManager.next();
                int currentPosition = mMusicManager.getCurrentPosition();
                usbMusicListFragment.play(currentPosition);
            }
        });

        usbMusicListFragment.setOnMusicItemClickListener(index -> {
            if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                hintDialog.show();
                return;
            } else if (!mBluzManagerUtils.isUhostEnable()) {
                ToastUtils.showShortToast(R.string.no_usb);
                return;
            }
            if (mMusicManager != null) {
                mMusicManager.select(index);
            }
        });
    }

    public void setProgressDialogMessage(String message) {
        if (progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean finish = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish = true;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mMusicManager != null) {
            mMusicManager.setOnMusicUIChangedListener(null);
            mMusicManager.setOnMusicEntryChangedListener(null);
            mMusicManager = null;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onModeChangedEvent(ModeChangedEvent event) {
        int mode = event.getMode();

        if (mode == BluzManagerData.FuncMode.USB) {
            initUsbMode();
        }
    }

    private void initUsbMode() {
        mMusicManager = mBluzManagerUtils.getBluzManager().getMusicManager(() -> {
            if (finish) {
                return;
            }
            mHandler = new MusicHandler(UsbModeActivity.this, mMusicManager);
            if (mBluzManagerUtils.isContentChanged() || MusicCache.getInstance().getUsbMusicList().isEmpty()) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                mHandler.sendEmptyMessage(MESSAGE_GET_PLISTENTRY);
            } else {
                dismissProgressDialog();
                //To UsbMusicListFragment.addMusicList
                EventBus.getDefault().postSticky(new UsbMusicScanSuccessEvent(MusicCache.getInstance().getUsbMusicList()));
            }
        });

        mMusicManager.setOnMusicUIChangedListener(new BluzManagerData.OnMusicUIChangedListener() {
            @Override
            public void onLoopChanged(int i) {
            }

            @Override
            public void onStateChanged(int state) {
                EventBus.getDefault().postSticky(new UsbPlayStateChangeEvent(state));
            }
        });

        mMusicManager.setOnMusicEntryChangedListener(musicEntry ->
                EventBus.getDefault().postSticky(new UsbMusicChangeEvent(musicEntry.artist,
                        musicEntry.name,
                        mMusicManager.getDuration()))
        );
    }

}
