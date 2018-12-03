package com.bazooka.bluetoothbox.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bazooka.bluetoothbox.application.Actions;
import com.bazooka.bluetoothbox.bean.Music;
import com.bazooka.bluetoothbox.bean.event.LocalMusicScanSuccessEvent;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.listener.EventCallback;
import com.bazooka.bluetoothbox.listener.OnPlayerEventListener;
import com.bazooka.bluetoothbox.receiver.NoisyAudioStreamReceiver;
import com.bazooka.bluetoothbox.utils.GsonUtils;
import com.bazooka.bluetoothbox.utils.MediaSessionManager;
import com.bazooka.bluetoothbox.utils.MusicUtils;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;


/**
 * 音乐播放后台服务
 * @author Created by wcy on 2015/11/27.
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "Service";
    private static final long TIME_UPDATE = 300L;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private final NoisyAudioStreamReceiver mNoisyReceiver = new NoisyAudioStreamReceiver();
    private final IntentFilter mNoisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final Handler mHandler = new Handler();
    private MediaPlayer mPlayer = new MediaPlayer();
    private AudioFocusManager mAudioFocusManager;
    private MediaSessionManager mMediaSessionManager;
    private OnPlayerEventListener mListener;
    // 正在播放的歌曲[本地|网络]
    private Music mPlayingMusic;
    // 正在播放的本地歌曲的序号
    private int mPlayingPosition = -1;
    private int mPlayState = STATE_IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: " + getClass().getSimpleName());
        mAudioFocusManager = new AudioFocusManager(this);
        mMediaSessionManager = new MediaSessionManager(this);
        mPlayer.setOnCompletionListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    public static void startCommand(Context context, String action) {
        Intent intent = new Intent(context, PlayService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.ACTION_MEDIA_PLAY_PAUSE:
                    playPause();
                    break;
                case Actions.ACTION_MEDIA_NEXT:
                    next();
                    break;
                case Actions.ACTION_MEDIA_PREVIOUS:
                    prev();
                    break;
                default:
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * 扫描音乐
     */
    @SuppressWarnings("all")
    public void updateMusicList(final EventCallback<Void> callback) {
        new AsyncTask<Void, Void, List<Music>>() {
            @Override
            protected List<Music> doInBackground(Void... params) {
                    return MusicUtils.scanMusic(PlayService.this);
            }

            @Override
            protected void onPostExecute(List<Music> musicList) {
                MusicCache.getMusicList().clear();
                MusicCache.getMusicList().addAll(musicList);

                if (!MusicCache.getMusicList().isEmpty()) {
                    updatePlayingPosition();
                    mPlayingMusic = MusicCache.getMusicList().get(mPlayingPosition);
                }

                SpManager.getInstance().saveMusicListUpdateFinish(true);
                if (mListener != null) {
                    mListener.onMusicListUpdate();
                }
                EventBus.getDefault().postSticky(new LocalMusicScanSuccessEvent(musicList));
            }
        }.execute();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    public OnPlayerEventListener getOnPlayEventListener() {
        return mListener;
    }

    public void setOnPlayEventListener(OnPlayerEventListener listener) {
        mListener = listener;
    }

    public void play(int position) {
        if (MusicCache.getMusicList().isEmpty()) {
            return;
        }

        if (position < 0) {
            position = MusicCache.getMusicList().size() - 1;
        } else if (position >= MusicCache.getMusicList().size()) {
            position = 0;
        }

        mPlayingPosition = position;
        Music music = MusicCache.getMusicList().get(mPlayingPosition);
        SpManager.getInstance().saveCurrentSongId(music.getId());
        play(music);
    }

    void play(Music music) {
        mPlayingMusic = music;
        try {
            mPlayer.reset();
            mPlayer.setDataSource(music.getPath());
            mPlayer.prepareAsync();
            mPlayState = STATE_PREPARING;
            mPlayer.setOnPreparedListener(mPreparedListener);
            mPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            if (mListener != null) {
                mListener.onChange(music);
            }
            mMediaSessionManager.updateMetaData(mPlayingMusic);
            mMediaSessionManager.updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (isPreparing()) {
                start();
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mListener != null) {
                mListener.onBufferingUpdate(percent);
            }
        }
    };

    public void playPause() {
        if (isPreparing()) {
            stop();
        } else if (isPlaying()) {
            pause();
        } else if (isPausing()) {
            start();
        } else {
            play(getPlayingPosition());
        }
    }

    void start() {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (mAudioFocusManager.requestAudioFocus()) {
            mPlayer.start();
            mPlayState = STATE_PLAYING;
            mHandler.post(mPublishRunnable);
            mMediaSessionManager.updatePlaybackState();
            registerReceiver(mNoisyReceiver, mNoisyFilter);

            if (mListener != null) {
                mListener.onPlayerStart();
            }
        }
    }

    public void pause() {
        if (!isPlaying()) {
            return;
        }

        mPlayer.pause();
        mPlayState = STATE_PAUSE;
        mHandler.removeCallbacks(mPublishRunnable);
        mMediaSessionManager.updatePlaybackState();
        unregisterReceiver(mNoisyReceiver);

        if (mListener != null) {
            mListener.onPlayerPause();
        }
    }

    public void stop() {
        if (isIdle()) {
            return;
        }

        pause();
        mPlayer.reset();
        mPlayState = STATE_IDLE;
    }

    public void next() {
        if (MusicCache.getMusicList().isEmpty()) {
            return;
        }
        play(mPlayingPosition + 1);
    }

    public void prev() {
        if (MusicCache.getMusicList().isEmpty()) {
            return;
        }
        play(mPlayingPosition - 1);
    }

    /**
     * 跳转到指定的时间位置
     *
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mPlayer.seekTo(msec);
            mMediaSessionManager.updatePlaybackState();
            if (mListener != null) {
                mListener.onPublish(msec);
            }
        }
    }

    public boolean isPlaying() {
        return mPlayState == STATE_PLAYING;
    }

    public boolean isPausing() {
        return mPlayState == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return mPlayState == STATE_PREPARING;
    }

    public boolean isIdle() {
        return mPlayState == STATE_IDLE;
    }

    /**
     * 获取正在播放的本地歌曲的序号
     */
    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    /**
     * 获取正在播放的歌曲[本地|网络]
     */
    public Music getPlayingMusic() {
        return mPlayingMusic;
    }

    /**
     * 删除或下载歌曲后刷新正在播放的本地歌曲的序号
     */
    public void updatePlayingPosition() {
        int position = 0;
        long id = SpManager.getInstance().getCurrentSongId();
        for (int i = 0; i < MusicCache.getMusicList().size(); i++) {
            if (MusicCache.getMusicList().get(i).getId() == id) {
                position = i;
                break;
            }
        }
        mPlayingPosition = position;
        SpManager.getInstance().saveCurrentSongId(MusicCache.getMusicList().get(mPlayingPosition).getId());
    }

    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    public long getCurrentPosition() {
        if (isPlaying() || isPausing()) {
            return mPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying() && mListener != null) {
                mListener.onPublish(mPlayer.getCurrentPosition());
            }
            mHandler.postDelayed(this, TIME_UPDATE);
        }
    };

    @Override
    public void onDestroy() {
        mPlayer.reset();
        mPlayer.release();
        mPlayer = null;
        mAudioFocusManager.abandonAudioFocus();
        mMediaSessionManager.release();
        MusicCache.setPlayService(null);
        super.onDestroy();
        Log.i(TAG, "onDestroy: " + getClass().getSimpleName());
    }


    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    public void quit(){
        stop();
        stopSelf();
    }
}
