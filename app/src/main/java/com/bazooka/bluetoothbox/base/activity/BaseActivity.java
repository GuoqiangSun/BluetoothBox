package com.bazooka.bluetoothbox.base.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.bean.event.ConnectedStateChangedEvent;
import com.bazooka.bluetoothbox.ui.activity.MainActivity;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.swain.baselib.log.Tlog;
import cn.com.swain.baselib.permission.PermissionGroup;
import cn.com.swain.baselib.permission.PermissionHelper;
import cn.com.swain.baselib.permission.PermissionRequest;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/8/31
 * 作用：Activity 基类
 */

public abstract class BaseActivity extends AppCompatActivity implements IActivity {

    protected Activity mContext;
    private Unbinder unbinder;
    private PromptDialogV2 disconnectHintDialog;

    protected boolean isActivityForeground = false;

    protected boolean isRegisterEventBus = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        mContext = this;
        this.initData();
        this.initView();
        this.addViewListener();
    }

    public void showActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        startActivity(intent);
    }

    /**
     * 布局资源 id
     *
     * @return 资源id
     */
    public abstract int getLayoutId();


    /**
     * 带返回的跳转
     *
     * @param cls         class
     * @param extras      参数
     * @param requestCode 请求码
     */
    @SuppressWarnings("unused")
    public void showActivityForResult(Class<?> cls, Bundle extras, int requestCode) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BluzManagerUtils bluzManagerUtils = BluzManagerUtils.getInstance();
        int currVolume = bluzManagerUtils.getCurrVolume();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                int maxVolume = SpManager.getInstance().getMaxVolume();
                bluzManagerUtils.setVolume(currVolume >= maxVolume ? maxVolume : ++currVolume);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                bluzManagerUtils.setVolume(currVolume == 0 ? 0 : --currVolume);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected static final int TYPE_MAIN = 1;
    protected static final int TYPE_SPLASH = 2;
    private int type;

    protected void setType(int type) {
        this.type = type;
    }

    @SuppressWarnings({"unused"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionStateChanged(ConnectedStateChangedEvent event) {
        if (!event.isConnected()) {
            if (this.type == TYPE_MAIN || this.type == TYPE_SPLASH) {
                showBluetoothDisconnectDialog();
            } else {
                finish();
            }
        }
    }

    public void showBluetoothDisconnectDialog() {
        if (!isActivityForeground) {
            return;
        }
        if (disconnectHintDialog == null) {
            disconnectHintDialog = new PromptDialogV2(mContext);
            disconnectHintDialog.setHintMessage(getString(R.string.failed_to_connect_bluetooth));
            disconnectHintDialog.setNegativeText(getString(R.string.retry));
            disconnectHintDialog.setPositiveText(getString(R.string.cancel));
            disconnectHintDialog.setOnButtonClickListener(new PromptDialogV2.OnButtonClickListener() {
                @Override
                public void onPositiveClick() {
                    disconnectHintDialog.dismiss();
                }

                @Override
                public void onNegativeClick() {
                    MainActivity.showActivity(mContext, true);
                    disconnectHintDialog.dismiss();
                }
            });
        }
        disconnectHintDialog.show();
        disconnectHintDialog.setNegativeBackground(R.drawable.bg_light_blue_n);
        disconnectHintDialog.setPositiveBackground(R.drawable.bg_dark_grey_n);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isRegisterEventBus) {
            EventBus.getDefault().register(this);
        }
        isActivityForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRegisterEventBus) {
            EventBus.getDefault().unregister(this);
        }
        isActivityForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        unbinder = null;
        if (mPermissionRequest != null) {
            mPermissionRequest.release();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissionRequest != null) {
            mPermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String TAG = "abc";

    private PermissionRequest mPermissionRequest;

    protected void requestPermission() {

        Context applicationContext = getApplicationContext();

        ArrayList<String> permissions = new ArrayList<>(2);

        if (!PermissionHelper.isGranted(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || !PermissionHelper.isGranted(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            permissions.add(PermissionGroup.STORAGE);

        } else {
        }

        if (!PermissionHelper.isGranted(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                || !PermissionHelper.isGranted(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Tlog.v(TAG, " permissions.add(PermissionGroup.LOCATION) ");
            permissions.add(PermissionGroup.LOCATION);
        } else {
            Tlog.v(TAG, " has PermissionGroup.LOCATION ");
        }

        if (permissions.size() > 0) {

            String[] per = permissions.toArray(new String[0]);

            if (mPermissionRequest == null) {
                Tlog.v(TAG, "HomeActivity new PermissionRequest() ");
                mPermissionRequest = new PermissionRequest(this);
            }

            mPermissionRequest.requestPermissions(new PermissionRequest.OnPermissionFinish() {
                @Override
                public void onAllPermissionRequestFinish() {
                    Tlog.v(TAG, "HomeActivity onPermissionRequestFinish() ");
                }
            }, new PermissionRequest.OnPermissionResult() {
                @Override
                public boolean onPermissionRequestResult(String permission, boolean granted) {
                    Tlog.v(TAG, "HomeActivity onPermissionRequestResult permission :" + permission + " granted:" + granted);
                    onPermissionRequest(permission,granted);
                    return true;
                }
            }, per);
        }
    }


    protected void onPermissionRequest(String permission, boolean granted){

    }


    protected void alert() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.permission_request_title);
        b.setMessage(R.string.permission_request);
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        b.setCancelable(false);
        b.create().show();
    }


}
