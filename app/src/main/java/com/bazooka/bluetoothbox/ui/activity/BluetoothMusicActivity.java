package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import com.actions.ibluz.manager.BluzManagerData;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.MusicCommonActivity;
import com.bazooka.bluetoothbox.bean.Music;
import com.bazooka.bluetoothbox.bean.event.ModeChangedEvent;
import com.bazooka.bluetoothbox.bean.event.PlayEvent;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.listener.OnPlayerEventListener;
import com.bazooka.bluetoothbox.service.PlayService;
import com.bazooka.bluetoothbox.ui.fragment.BluetoothPlayControlFragment;
import com.bazooka.bluetoothbox.ui.fragment.MusicListFragment;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：蓝牙音乐播放界面
 */

public class BluetoothMusicActivity extends MusicCommonActivity {

    private List<Fragment> fragments;
    private BluzManagerUtils mBluzManagerUtils;

    private PlayService mPlayService;
    /**
     * 音乐列表是否加载完成
     */
    private boolean updateFinish = false;
    private ProgressDialog loadingDialog;

    public static boolean isLastPlaying = false;

    @Override
    public void initData() {
        mPlayService = MusicCache.getPlayService();

        mBluzManagerUtils = BluzManagerUtils.getInstance();
        //设置模式为 蓝牙音乐
        mBluzManagerUtils.setMode(BluzManagerData.FuncMode.A2DP);
        updateFinish = SpManager.getInstance().getMusicListUpdateFinish();

        fragments = new ArrayList<>();
        fragments.add(new BluetoothPlayControlFragment());
        fragments.add(new MusicListFragment());

        if (mPlayService != null) {
            if (isLastPlaying && !mPlayService.isPlaying()) {
                mPlayService.playPause();
            }
        }
    }

    @Override
    public List<Fragment> setViewPagerContent() {
        return fragments;
    }

    @Override
    public int setBackgroundResId() {
        return R.drawable.bg_blue;
    }

    @Override
    public int setIconRes() {
        return R.drawable.ic_bluetooth;
    }

    @Override
    public void initViews() {
        loadingDialog = new ProgressDialog(mContext);
        loadingDialog.setMessage(getString(R.string.loading));
        if (!updateFinish) {
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        } else {
            if (MusicCache.getMusicList().size() > 0 && mPlayService != null) {
                int playingPosition = mPlayService.getPlayingPosition() == -1 ? 0 : mPlayService.getPlayingPosition();
                //TO: BluetoothPlayControlFragment.updateMusicInfo 更新音乐播放信息
                EventBus.getDefault().postSticky(MusicCache.getMusicList().get(playingPosition));
            }
        }

        //TO: BluetoothPlayControlFragment.playStateChanged 更新音乐播放状态
        if (mPlayService != null && mPlayService.isPlaying()) {
            EventBus.getDefault().postSticky(new MusicStateChangeEvent(true));
        } else {
            EventBus.getDefault().postSticky(new MusicStateChangeEvent(false));
        }
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void addViewListener() {
        if (mPlayService != null) {

            mPlayService.setOnPlayEventListener(new OnPlayerEventListener() {
                @Override
                public void onChange(Music music) {
                    EventBus.getDefault().postSticky(music);
                    PlayEvent playEvent = new PlayEvent(MusicCache.getPlayService().getPlayingPosition());
                    EventBus.getDefault().post(playEvent);
                }

                @Override
                public void onPlayerStart() {
                    EventBus.getDefault().postSticky(new MusicStateChangeEvent(true));
                    PlayEvent playEvent = new PlayEvent(MusicCache.getPlayService().getPlayingPosition());
                    EventBus.getDefault().post(playEvent);
                }

                @Override
                public void onPlayerPause() {
                    EventBus.getDefault().postSticky(new MusicStateChangeEvent(false));
                }

                @Override
                public void onPublish(int progress) {

                }

                @Override
                public void onBufferingUpdate(int percent) {
                }

                @Override
                public void onMusicListUpdate() {
                    loadingDialog.dismiss();
                }
            });
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModeChanged(ModeChangedEvent event) {

    }

    public class MusicStateChangeEvent {
        private boolean isPlaying;

        public MusicStateChangeEvent(boolean isPlaying) {
            this.isPlaying = isPlaying;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }
    }

}
