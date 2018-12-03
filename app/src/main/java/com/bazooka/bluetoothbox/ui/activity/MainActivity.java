package com.bazooka.bluetoothbox.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.bean.event.BluzManagerReadyEvent;
import com.bazooka.bluetoothbox.bean.event.ConnectedStateChangedEvent;
import com.bazooka.bluetoothbox.cache.DefaultFlashCache;
import com.bazooka.bluetoothbox.cache.MusicCache;
import com.bazooka.bluetoothbox.cache.db.LedFlashHelper;
import com.bazooka.bluetoothbox.cache.db.SendSuccessFlashHelper;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.bazooka.bluetoothbox.cache.db.entity.SendSuccessFlash;
import com.bazooka.bluetoothbox.listener.NoDoubleClickListener;
import com.bazooka.bluetoothbox.ui.dialog.BluetoothSearchDialog;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialog;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.ui.view.PullUpDragLayout;
import com.bazooka.bluetoothbox.utils.DialogFragmentUtils;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.ToastUtil;
import com.bazooka.bluetoothbox.utils.animutils.AnimationsContainer;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/14
 * 作用：主页面
 */
public class MainActivity extends BaseActivity {

    private final int HANDLER_WHAT_SEND_DEFAULT_FLASH = 0x10;

    private static final String DISCONNECT_DIALOG_TAG = "MainActivity.ClosePromptDialog";
    private static final String BLUETOOTH_SEARCH_DIALOG_TAG = "MainActivity.BluetoothDeviceSearchDialog";
    private boolean isConnect = false;
    private boolean isUserDisconnect = false;

    @BindView(R.id.pdl_root)
    PullUpDragLayout pdlRoot;
    @BindView(R.id.btn_bluetooth)
    ImageButton btnBluetooth;
    @BindView(R.id.btn_fm)
    ImageButton btnFm;
    @BindView(R.id.btn_usb)
    ImageButton btnUsb;
    @BindView(R.id.btn_aux)
    ImageButton btnAux;
    @BindView(R.id.btn_led)
    ImageButton btnLed;
    @BindView(R.id.btn_switch)
    ImageButton btnSwitch;
    @BindView(R.id.ll_button)
    LinearLayout llButton;
    @BindView(R.id.tv_bluetooth_info)
    TextView tvBluetoothInfo;
    @BindView(R.id.iv_drag)
    ImageView ivDrag;
    @BindView(R.id.iv_title)
    ImageView ivTitle;
    @BindView(R.id.iv_phone)
    ImageView ivPhone;
    @BindView(R.id.iv_bluetooth_state)
    ImageView ivBluetoothState;
    @BindView(R.id.gif)
    ImageView gifImageView;

    private PromptDialog mClosePromptDialog;
    private BluetoothSearchDialog mSearchDialog;

    private BluetoothDevice connectedDevice;

    private BluzDeviceUtils bluzDeviceUtils = BluzDeviceUtils.getInstance();
    private BluzManagerUtils mBluzManagerUtils = BluzManagerUtils.getInstance();
    private ObjectAnimator mScaleAnimator;
    private AnimationsContainer.FramesSequenceAnimation animation;

    private HandlerThread saveDefaultFlashThread;
    private Handler saveDefaultFlashHandler;
    private boolean connected;

    private PromptDialogV2 hintDialog;

    public static void showActivity(Context context, boolean openBluetoothConnect) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("openBluetoothConnect", openBluetoothConnect);
        context.startActivity(intent);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        isRegisterEventBus = false;
        EventBus.getDefault().register(this);
        initHandler();


//        {
//            ObjectAnimator alpha1 = ObjectAnimator.ofFloat(btnBluetooth, "alpha", 0f, 1f);
//            ObjectAnimator alpha2 = ObjectAnimator.ofFloat(btnFm, "alpha", 0f, 1f);
//            ObjectAnimator alpha3 = ObjectAnimator.ofFloat(btnUsb, "alpha", 0f, 1f);
////            ObjectAnimator alpha4 = ObjectAnimator.ofFloat(btnAux, "alpha", 0f, 1f);
//            ObjectAnimator alpha5 = ObjectAnimator.ofFloat(btnLed, "alpha", 0f, 1f);
////            ObjectAnimator alpha6 = ObjectAnimator.ofFloat(btnSwitch, "alpha", 0f, 1f);
//            AnimatorSet as = new AnimatorSet();
//            as.playSequentially(alpha1,alpha2, alpha3,alpha5);
//            as.start();
//        }
        {
            PropertyValuesHolder scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f, 1.0f);
            PropertyValuesHolder scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f, 1.0f);
            mScaleAnimator = ObjectAnimator.ofPropertyValuesHolder(ivBluetoothState, scaleXHolder, scaleYHolder)
                    .setDuration(1750);
            mScaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
            mScaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
        {
            mClosePromptDialog = PromptDialog.newInstance(getString(R.string.close_connect_hint),
                    getString(R.string.yes),
                    getString(R.string.cancel));

            mSearchDialog = new BluetoothSearchDialog();
        }


//        EventBus.getDefault().register(this);

        animation = AnimationsContainer.getInstance(R.array.anim_res, 30).createProgressDialogAnim(gifImageView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pdlRoot.toggleBottomView();

            }
        }, 100);


    }

    @Override
    public void initView() {
        tvBluetoothInfo.setText(getString(R.string.bluetooth_info, "______", "_______"));
        saveDefaultFlash();

    }

    private void initHandler() {
        saveDefaultFlashThread = new HandlerThread("[MainActivity.saveDefaultFlashThread]",
                Process.THREAD_PRIORITY_BACKGROUND);
        saveDefaultFlashThread.start();
        saveDefaultFlashHandler = new Handler(saveDefaultFlashThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_WHAT_SEND_DEFAULT_FLASH:
                        //保存默认闪法 和 默认的已推送闪法
                        if (LedFlashHelper.getInstance().getLedFlashCount() == 0) {
                            DefaultFlashCache cache = new DefaultFlashCache();
                            cache.clear();
                            cache.addCache();
                            cache.reset();
                            cache.clear();
                        }

                        if (SendSuccessFlashHelper.getInstance().getCount() == 0) {
                            List<LedFlash> ledFlashList = LedFlashHelper.getInstance().getAllLedFlash();
                            for (int i = 0; i < ledFlashList.size(); i++) {
                                LedFlash ledFlash = ledFlashList.get(i);
                                SendSuccessFlashHelper.getInstance().add(
                                        new SendSuccessFlash(null, ledFlash.getId(), ledFlash.getName(), i));
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean openBluetoothConnect = intent.getBooleanExtra("openBluetoothConnect", false);
        if (openBluetoothConnect) {
            if (!pdlRoot.isOpen()) {
                pdlRoot.toggleBottomView();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectedDevice = bluzDeviceUtils.getConnectionDevice();
        if (connectedDevice == null) {
            isConnect = false;
        } else {
            isConnect = true;
            SpManager.getInstance().saveDeviceAddress(connectedDevice.getAddress());
        }

    }

    @Override
    public void addViewListener() {

        //蓝牙开关点击
        ivBluetoothState.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (isConnect) {
                    mClosePromptDialog.show(getSupportFragmentManager(), DISCONNECT_DIALOG_TAG);
                } else {
                    mSearchDialog.show(getSupportFragmentManager(), BLUETOOTH_SEARCH_DIALOG_TAG);
                }
            }
        });

        pdlRoot.setOnStateListener(new PullUpDragLayout.OnStateListener() {
            @Override
            public void open() {
                ivDrag.setVisibility(View.GONE);
                ivTitle.setVisibility(View.VISIBLE);
                menuOpen();
            }

            @Override
            public void close() {
                ivDrag.setVisibility(View.VISIBLE);
                ivTitle.setVisibility(View.INVISIBLE);
                menuClose();
                //判断是否连接
                if (!isConnect) {//弹出提示框 请求连接蓝牙
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
                    hintDialog.show();
                }
            }
        });

        mClosePromptDialog.setOnButtonClickListener(new PromptDialog.OnButtonClickListener() {
            @Override
            public void onPositiveClick() {
                isUserDisconnect = true;
                bluzDeviceUtils.disconnect(bluzDeviceUtils.getConnectionDevice());
            }

            @Override
            public void onNegativeClick() {
            }
        });

        bluzDeviceUtils.setOnConnectionListener();

//
    }

    @OnClick({R.id.btn_bluetooth, R.id.btn_fm, R.id.btn_usb, R.id.btn_aux, R.id.btn_led, R.id.btn_switch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bluetooth:
                if (!pdlRoot.isOpen()) {
                    showActivity(BluetoothMusicActivity.class);
                }
                break;
            case R.id.btn_fm:
                if (!pdlRoot.isOpen()) {
                    showActivity(FmModeActivity.class);
                }
                break;
            case R.id.btn_usb:
                if (!pdlRoot.isOpen()) {
                    showActivity(UsbModeActivity.class);
                }
                break;
            case R.id.btn_aux:
                if (!pdlRoot.isOpen()) {
                    showActivity(AuxModeActivity.class);
                }
                break;
            case R.id.btn_led:
                if (!pdlRoot.isOpen()) {
                    showActivity(LEDMainActivity.class);
                }
                break;
            case R.id.btn_switch:
                if (!pdlRoot.isOpen()) {
                    showActivity(SwitchActivity.class);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 保存默认闪法
     */
    private void saveDefaultFlash() {
        saveDefaultFlashHandler.sendEmptyMessage(HANDLER_WHAT_SEND_DEFAULT_FLASH);
    }

    @SuppressWarnings({"unused"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluzManagerReady(BluzManagerReadyEvent event) {
        BluzManagerUtils bluzManagerUtils = BluzManagerUtils.getInstance();
        if (bluzManagerUtils.getBluzManager() != null) {
            DialogFragmentUtils.dismissDialog(getSupportFragmentManager(), BLUETOOTH_SEARCH_DIALOG_TAG);
            ToastUtil.showConnectSuccessToast();
            bluzManagerUtils.setSystemTime();
            isConnect = true;
            SpManager.getInstance().saveMaxVolume(bluzManagerUtils.getMaxVolume());
        }
    }

    /**
     * 蓝牙连接状态改变
     *
     * @param event 事件
     */
    @Override
    @SuppressWarnings({"unused"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionStateChanged(ConnectedStateChangedEvent event) {
        BluetoothDevice device = event.getBluetoothDevice();
        connected = event.isConnected();
        if (event.isConnected()) {
            mScaleAnimator.end();
            connectedDevice = device;
            SpManager.getInstance().saveDeviceAddress(device.getAddress());
            mBluzManagerUtils.createBluzManager(bluzDeviceUtils.getBluzDevice());
            ivPhone.setSelected(true);
            ivBluetoothState.setSelected(true);
            tvBluetoothInfo.setText(getString(R.string.bluetooth_info, device.getName(), device.getAddress()));

            if (device.getName().equals("BAZ-G2-FM")) {
                btnAux.setVisibility(View.GONE);
                btnSwitch.setVisibility(View.GONE);
                btnFm.setVisibility(View.VISIBLE);
            }
            if (device.getName().equals("BAZ-G2")) {
                btnFm.setVisibility(View.GONE);
                btnAux.setVisibility(View.VISIBLE);
                btnSwitch.setVisibility(View.VISIBLE);
            }

        } else {
            mScaleAnimator.start();
            isConnect = false;
            releaseAll();
            DialogFragmentUtils.dismissDialog(getSupportFragmentManager(), DISCONNECT_DIALOG_TAG);
            MusicCache.getPlayService().pause();
            ivPhone.setSelected(false);
            ivBluetoothState.setSelected(false);
            mScaleAnimator.start();
            tvBluetoothInfo.setText(getString(R.string.bluetooth_info, "______", "_______"));
            if (isUserDisconnect) {
                //如果是用户主动断开，不做处理，只将 isUserDisconnect 赋值为 false
                isUserDisconnect = false;
            } else {
                //如果不是用户主动断开连接，则弹出提示对话框
                showBluetoothDisconnectDialog();
            }
        }
    }

    private void menuOpen() {
        animation.start();
        if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
            //蓝牙未连接
            if (!mScaleAnimator.isStarted()) {
                mScaleAnimator.start();
            }
        }
    }

    private void menuClose() {
        animation.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (pdlRoot.isOpen()) {
            animation.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        animation.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        bluzDeviceUtils.release();
        releaseAll();

        saveDefaultFlashThread.quit();
        saveDefaultFlashHandler.removeCallbacksAndMessages(null);
        saveDefaultFlashHandler = null;
    }


    private void releaseAll() {
        mBluzManagerUtils.setOnGlobalUIChangedListener(null);
        mBluzManagerUtils.release();
        connectedDevice = null;
    }

}
