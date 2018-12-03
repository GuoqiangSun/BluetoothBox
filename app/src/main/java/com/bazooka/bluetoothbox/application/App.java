package com.bazooka.bluetoothbox.application;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;

import com.bazooka.bluetoothbox.BuildConfig;
import com.bazooka.bluetoothbox.cache.db.DbHelper;
import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.greenrobot.greendao.query.QueryBuilder;


/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/8/31
 *         作用：
 */
public class App extends Application {

    private static App instance;
    private AudioManager mAudioManager;
    private int maxMusicVolume = 31;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("SmartADH5_LOG")
                //方法数只打印 1 行
                .methodCount(1)
                .build();

        //设置 Logger 是否打印日志
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });

        DbHelper.getInstance().init(this);
        //AndroidUtilCode 初始化
        Utils.init(this);
        //Facebook Stetho
        Stetho.initializeWithDefaults(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //返回特定流的最大音量指数
        maxMusicVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

        //GreenDao 相关
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG ;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;
    }

    public static App getContext() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            //返回特定数据流的当前音量索引
            int currVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (currVolume != maxMusicVolume) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMusicVolume, 0);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };



}
