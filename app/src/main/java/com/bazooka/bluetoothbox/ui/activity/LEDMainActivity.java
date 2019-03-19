package com.bazooka.bluetoothbox.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.bean.event.ConnectedStateChangedEvent;
import com.bazooka.bluetoothbox.bean.event.CustomCommandEvent;
import com.bazooka.bluetoothbox.bean.event.FlashSendProgress;
import com.bazooka.bluetoothbox.cache.db.SendSuccessFlashHelper;
import com.bazooka.bluetoothbox.cache.db.entity.SendSuccessFlash;
import com.bazooka.bluetoothbox.ui.dialog.PromptDialogV2;
import com.bazooka.bluetoothbox.ui.view.colorpicker.ColorPickView;
import com.bazooka.bluetoothbox.utils.CalculateUtils;
import com.bazooka.bluetoothbox.utils.DialogUtils;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;
import com.blankj.utilcode.util.ConvertUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.NumberPicker;

/**
 * 作者：尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/9/15
 * 作用：LED 控制主界面
 */

public class LEDMainActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.colorPickerView)
    ColorPickView colorPickerView;
    @BindView(R.id.ll_rgb_info)
    LinearLayout llRgbInfo;
    @BindView(R.id.iv_demo)
    ImageView ivFmDemo;
    @BindView(R.id.iv_switch)
    ImageView ivSwitch;
    @BindView(R.id.sb_brightness)
    SeekBar sbBrightness;
    @BindView(R.id.sb_speed)
    SeekBar sbSpeed;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.iv_mode)
    ImageView ivMode;
    @BindView(R.id.iv_mode_left)
    ImageView ivModeLeft;
    @BindView(R.id.iv_mode_right)
    ImageView ivModeRight;
    @BindView(R.id.tv_mode_name)
    TextView tvModeName;
    @BindView(R.id.fl_color_picker)
    FrameLayout flColorPicker;
    @BindView(R.id.iv_select_color_background)
    ImageView ivSelectColorBackground;
    @BindView(R.id.iv_brightness)
    ImageView ivBrightness;
    @BindView(R.id.iv_speed)
    ImageView ivSpeed;
    @BindView(R.id.iv_fm_version_yellow)
    ImageView ivFmVersionYellow;
    @BindView(R.id.iv_fm_version_purple)
    ImageView ivFmVersionPurple;
    @BindView(R.id.iv_fm_version_blue)
    ImageView ivFmVersionBlue;
    @BindView(R.id.iv_fm_version_red)
    ImageView ivFmVersionRed;
    @BindView(R.id.iv_fm_version_orange)
    ImageView ivFmVersionOrange;
    @BindView(R.id.iv_fm_version_mazarine)
    ImageView ivFmVersionMazarine;
    @BindView(R.id.iv_fm_version_green)
    ImageView ivFmVersionGreen;
    @BindView(R.id.iv_fm_version_dark_yellow)
    ImageView ivFmVersionDarkYellow;
    @BindView(R.id.iv_fm_version_emerald)
    ImageView ivFmVersionEmerald;
    @BindView(R.id.tl_select_color)
    TableLayout tlSelectColor;

    private ProgressDialog progressDialog;
    private BluzManagerUtils mBluzManagerUtils;
    private List<SendSuccessFlash> sendSuccessFlasheList = new ArrayList<>();
    private int curPosition = 0;
    private NumberPicker mRPicker, mGPicker, mBPicker;
    private PromptDialogV2 hintDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_led_main;
    }

    @Override
    public void initData() {
        progressDialog = new ProgressDialog(mContext);
        mBluzManagerUtils = BluzManagerUtils.getInstance();

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

        sendSuccessFlasheList.clear();
        sendSuccessFlasheList.addAll(SendSuccessFlashHelper.getInstance().getAll());

        if (sendSuccessFlasheList.size() > 0) {
            tvModeName.setText(sendSuccessFlasheList.get(0).getFlashName());

        }


    }

    @Override
    public void initView() {

        int displayHeight = CalculateUtils.getDisplayHeight();//获取屏幕高度(px)
        int displayWidth = (int) (CalculateUtils.getDisplayWidth() * 3.0f / 4);//屏幕宽度的3/4
        int colorPickerWidth = Math.min(displayWidth, displayHeight / 2);
        if (!SpManager.getInstance().getConnectDeviceNAME().equals("") && SpManager.getInstance().getConnectDeviceNAME().equals("BAZ-G2-FM")) {
            colorPickerView.setVisibility(View.GONE);
            ivSelectColorBackground.setVisibility(View.GONE);
            tlSelectColor.setVisibility(View.VISIBLE);

        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(colorPickerWidth, colorPickerWidth);
            flColorPicker.setLayoutParams(params);
            colorPickerView.setBigCircle(colorPickerWidth / 2 - ConvertUtils.dp2px(17));//减去边框宽度，大致为17dp
//            colorPickerView.setBigCircle(colorPickerWidth / 2 - ConvertUtils.dp2px(30));
        }


        mRPicker = new NumberPicker(this);
        mRPicker.setWidth(1);
        mRPicker.setCycleDisable(false);
        mRPicker.setDividerVisible(false);
        mRPicker.setOffset(1);//偏移量
        mRPicker.setRange(0, 255, 1);//数字范围
        mRPicker.setSelectedItem(127);
        mRPicker.setTextColor(0xff000000);

        mGPicker = new NumberPicker(this);
        mGPicker.setWidth(1);
        mGPicker.setCycleDisable(false);
        mGPicker.setDividerVisible(false);
        mGPicker.setOffset(1);//偏移量
        mGPicker.setRange(0, 255, 1);//数字范围
        mGPicker.setSelectedItem(127);
        mGPicker.setTextColor(0xff000000);

        mBPicker = new NumberPicker(this);
        mBPicker.setWidth(1);
        mBPicker.setCycleDisable(false);
        mBPicker.setDividerVisible(false);
        mBPicker.setOffset(1);//偏移量
        mBPicker.setRange(0, 255, 1);//数字范围
        mBPicker.setSelectedItem(127);
        mBPicker.setTextColor(0xff000000);

        LinearLayout.LayoutParams pickerParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        pickerParams.weight = 1;
        View mRPickerView = mRPicker.getContentView();
        View mGPickerView = mGPicker.getContentView();
        View mBPickerView = mBPicker.getContentView();
        mRPickerView.setLayoutParams(pickerParams);
        mGPickerView.setLayoutParams(pickerParams);
        mBPickerView.setLayoutParams(pickerParams);

        //将三个选择器添加到布局中
        llRgbInfo.addView(mRPickerView, 1);
        llRgbInfo.addView(mGPickerView, 3);
        llRgbInfo.addView(mBPickerView, 5);

        int ledBrightness = SpManager.getInstance().getLedBrightness();
        int ledSpeed = SpManager.getInstance().getLedSpeed();

        if (ledBrightness == -1) {
            ledBrightness = 50;
            mBluzManagerUtils.sendBrightness(ledBrightness);
            SpManager.getInstance().saveLedBrightness(ledBrightness);
        }
        if (ledSpeed == -1) {
            ledSpeed = 50;
            mBluzManagerUtils.sendSpeed(ledSpeed);
            SpManager.getInstance().saveLedSpeed(ledSpeed);
        }
        sbBrightness.setProgress(ledBrightness);
        sbSpeed.setProgress(ledSpeed);

        if (!SpManager.getInstance().getConnectDeviceNAME().equals("") && SpManager.getInstance().getConnectDeviceNAME().equals("BAZ-G2-FM")) {
            llRgbInfo.setVisibility(View.GONE);
            ivMenu.setVisibility(View.GONE);
            if (sendSuccessFlasheList != null) {
                Iterator<SendSuccessFlash> iterator = sendSuccessFlasheList.iterator();
                while (iterator.hasNext()) {
                    SendSuccessFlash next = iterator.next();
                    if (next.getFlashName().startsWith("FLASH")) {
                        iterator.remove();
                    }
                }

            }
        }

//        SpManager.getInstance().saveDeviceName("BAZ-G2");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBluzManagerUtils.queryLedAndLightState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void addViewListener() {
        mRPicker.setOnWheelListener((index, item) -> {
            mBluzManagerUtils.sendColor(item.intValue(),
                    mGPicker.getSelectedItem().intValue(),
                    mBPicker.getSelectedItem().intValue());
            mBluzManagerUtils.queryLedAndLightState();
        });

        mGPicker.setOnWheelListener((index, item) -> {
            mBluzManagerUtils.sendColor(mRPicker.getSelectedItem().intValue(),
                    item.intValue(),
                    mBPicker.getSelectedItem().intValue());
            mBluzManagerUtils.queryLedAndLightState();
        });

        mBPicker.setOnWheelListener((index, item) -> {
            mBluzManagerUtils.sendColor(mRPicker.getSelectedItem().intValue(),
                    mGPicker.getSelectedItem().intValue(),
                    item.intValue());
            mBluzManagerUtils.queryLedAndLightState();
        });

        colorPickerView.setOnColorChangedListener(new ColorPickView.OnColorChangedListener() {
            @Override
            public void onColorChange(int red, int green, int blue) {
                mRPicker.getWheelView().setSelectedIndex(red);
                mGPicker.getWheelView().setSelectedIndex(green);
                mBPicker.getWheelView().setSelectedIndex(blue);
            }

            @Override
            public void onTouchStop(int red, int green, int blue) {
                mRPicker.getWheelView().setSelectedIndex(red);
                mGPicker.getWheelView().setSelectedIndex(green);
                mBPicker.getWheelView().setSelectedIndex(blue);
                mBluzManagerUtils.sendColor(red, green, blue);
                mBluzManagerUtils.queryLedAndLightState();
            }
        });


        sbBrightness.setOnSeekBarChangeListener(this);
        sbSpeed.setOnSeekBarChangeListener(this);
    }


    @OnClick({R.id.iv_demo, R.id.iv_switch, R.id.iv_menu, R.id.iv_back,
            R.id.iv_mode_left, R.id.iv_mode_right, R.id.iv_fm_version_yellow, R.id.iv_fm_version_purple, R.id.iv_fm_version_blue, R.id.iv_fm_version_red, R.id.iv_fm_version_orange, R.id.iv_fm_version_mazarine, R.id.iv_fm_version_green, R.id.iv_fm_version_dark_yellow, R.id.iv_fm_version_emerald})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //DEMO
            case R.id.iv_demo:
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                mBluzManagerUtils.sendDemo();
                mBluzManagerUtils.queryLedAndLightState();
                break;
            //SWITCH
            case R.id.iv_switch:
                if (BluzDeviceUtils.getInstance().getConnectionDevice() == null) {
                    hintDialog.show();
                    return;
                }
                mBluzManagerUtils.openOrCloseLed();
                mBluzManagerUtils.queryLedAndLightState();
                break;
            case R.id.iv_menu:
                showActivity(LEDCustomerActivity.class);
                break;
            case R.id.iv_back:
                finish();
                break;
            //前一个模式
            case R.id.iv_mode_left:
                if (sendSuccessFlasheList.size() > 0) {
                    int size = sendSuccessFlasheList.size();

                    if (curPosition == 0) {
                        curPosition = size - 1;
                    } else {
                        curPosition--;
                    }
                    mBluzManagerUtils.sendFlash(sendSuccessFlasheList.get(curPosition).getIndex());
                    tvModeName.setText(sendSuccessFlasheList.get(curPosition).getFlashName());
                    mBluzManagerUtils.queryLedAndLightState();
                }
                break;
            //下一个模式
            case R.id.iv_mode_right:
                if (sendSuccessFlasheList.size() > 0) {
                    int size = sendSuccessFlasheList.size();

                    if (curPosition == size - 1) {
                        curPosition = 0;
                    } else {
                        curPosition++;
                    }
                    mBluzManagerUtils.sendFlash(sendSuccessFlasheList.get(curPosition % size).getIndex());
                    tvModeName.setText(sendSuccessFlasheList.get(curPosition).getFlashName());
                    mBluzManagerUtils.queryLedAndLightState();
                }
                break;
            case R.id.iv_fm_version_yellow:
                mBluzManagerUtils.sendColor(255, 255, 0);
                break;
            case R.id.iv_fm_version_purple:
                mBluzManagerUtils.sendColor(255, 0, 255);
                break;
            case R.id.iv_fm_version_blue:
                mBluzManagerUtils.sendColor(0, 255, 255);
                break;
            case R.id.iv_fm_version_red:
                mBluzManagerUtils.sendColor(255, 40, 40);
                break;
            case R.id.iv_fm_version_orange:
                mBluzManagerUtils.sendColor(255, 100, 50);
                break;
            case R.id.iv_fm_version_mazarine:
                mBluzManagerUtils.sendColor(65, 150, 255);
                break;
            case R.id.iv_fm_version_green:
                mBluzManagerUtils.sendColor(0, 255, 100);
                break;
            case R.id.iv_fm_version_dark_yellow:
                mBluzManagerUtils.sendColor(255, 235, 53);
                break;
            case R.id.iv_fm_version_emerald:
                mBluzManagerUtils.sendColor(65, 255, 70);
                break;
            default:
                break;
        }
    }


    //以下三个方法为 OnSeekBarChangeListener 中的方法
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.sb_brightness:
                mBluzManagerUtils.sendBrightness(seekBar.getProgress());
                SpManager.getInstance().saveLedBrightness(seekBar.getProgress());
                break;
            case R.id.sb_speed:
                mBluzManagerUtils.sendSpeed(100 - seekBar.getProgress());
                SpManager.getInstance().saveLedSpeed(seekBar.getProgress());
                break;
            default:
                break;
        }
    }

    /**
     * 闪法发送完成
     *
     * @param event
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onFlashSendProgressChanged(FlashSendProgress event) {
        int progress = event.getProgress();
        int total = event.getTotal();
        if (progress == total || event.isCancel()) {
            if (progressDialog.isShowing() && isActivityForeground) {
                progressDialog.dismiss();
            }
            sendSuccessFlasheList.clear();
            sendSuccessFlasheList.addAll(SendSuccessFlashHelper.getInstance().getAll());
            if (sendSuccessFlasheList.size() > 0) {
                tvModeName.setText(sendSuccessFlasheList.get(0).getFlashName());
            }
        } else {
            if (!progressDialog.isShowing() && isActivityForeground) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.loading));
            }
        }

    }

    @Override
    @SuppressWarnings({"unused"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionStateChanged(ConnectedStateChangedEvent event) {
        super.onConnectionStateChanged(event);
        if (event.isConnected()) {
            mBluzManagerUtils.queryLedAndLightState();
        }
    }

    /**
     * 音响 LED 灯是否打开
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCustomCommand(CustomCommandEvent event) {
        if (event.getWhat() == BluzManagerUtils.KEY_ANS_LIGHT_CONTROL_STATE) {
            String binaryResult = String.format("%8s", Integer.toBinaryString(event.getParam1()))
                    .replaceAll(" ", "0");
            char ledState = binaryResult.charAt(0);
            ivSwitch.setSelected(ledState == '1');
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (colorPickerView != null) {
            colorPickerView.recycle();
        }
    }



}
