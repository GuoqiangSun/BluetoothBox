package com.bazooka.bluetoothbox.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.service.PlayService;
import com.bazooka.bluetoothbox.ui.view.AnimateText;
import com.bazooka.bluetoothbox.utils.PermissionReq;
import com.bazooka.bluetoothbox.utils.SpManager;

import java.util.ArrayList;

import butterknife.BindView;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.permission.PermissionGroup;
import cn.com.swain.baselib.permission.PermissionHelper;
import cn.com.swain.baselib.permission.PermissionRequest;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：启动页
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_logo_1)
    ImageView ivLogo1;
    @BindView(R.id.tv_logo_2)
    TextView tvLogo2;
    @BindView(R.id.tv_logo_3)
    AnimateText tvLogo3;

    private final static int REQUEST_BLUETOOTH_ON = 100;
    /**
     * 蓝牙打开操作执行完成
     */
    private boolean isBluetoothOpenFinish;
    /**
     * 动画是否执行完成
     */
    private boolean isAnimFinished;

    private Handler mHandler = new Handler();

    private ServiceConnection mPlayServiceConnection;


    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initData() {
        setType(TYPE_SPLASH);
        requestPermission();
        SpManager.getInstance().removeLedBrightness();
        SpManager.getInstance().removeLedSpeed();
    }

    @Override
    public void initView() {
        SpManager.getInstance().saveMusicListUpdateFinish(false);

        Intent serviceIntent = new Intent(mContext, PlayService.class);
        startService(serviceIntent);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(serviceIntent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);

        ObjectAnimator oaLogo1 = ObjectAnimator.ofFloat(ivLogo1, "alpha", 0f, 1.0f);
        ObjectAnimator oaLogo2 = ObjectAnimator.ofFloat(tvLogo2, "alpha", 0f, 1.0f);

        AnimatorSet mAnimSet = new AnimatorSet();
        mAnimSet.playTogether(oaLogo1, oaLogo2);
        mAnimSet.setDuration(1500);
        mAnimSet.start();

        mAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvLogo3.setText(getString(R.string.logo2));
                tvLogo3.setTime(100);
                tvLogo3.startAnimate(() -> {
                    Log.v("abc", " animate finish ");
                    startMain();
                    startMain = true;
                });
            }
        });

    }

    @Override
    public void addViewListener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mPlayServiceConnection);
    }

    private boolean startMain;

    private boolean start;

    private void startMain() {
        if (PermissionHelper.isGranted(this, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                PermissionHelper.isGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                !start) {
            start = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            SplashActivity.this.finish();
        }
    }


    @Override
    protected void onPermissionRequest(String permission, boolean granted) {
        super.onPermissionRequest(permission, granted);
        if (granted && PermissionGroup.STORAGE.equalsIgnoreCase(permission)) {

        } else if (PermissionGroup.LOCATION.equalsIgnoreCase(permission)) {
            if (!granted) {
                alert();
            } else {
                if (startMain) {
                    startMain();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH_ON) {
            isBluetoothOpenFinish = true;
            //当动画不在执行时（即执行完成）
            if (isAnimFinished) {
                startMain();
            }
        }
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            MusicCache.setPlayService(playService);
            PermissionReq.with(SplashActivity.this)
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .result(new PermissionReq.Result() {
                        @Override
                        public void onGranted() {
                            playService.updateMusicList(null);
                        }

                        @Override
                        public void onDenied() {
//                            playService.quit();
                        }
                    })
                    .request();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

}
