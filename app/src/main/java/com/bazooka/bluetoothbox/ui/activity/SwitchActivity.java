package com.bazooka.bluetoothbox.ui.activity;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.activity.BaseActivity;
import com.bazooka.bluetoothbox.bean.event.CustomCommandEvent;
import com.bazooka.bluetoothbox.listener.NoDoubleClickListener;
import com.bazooka.bluetoothbox.ui.dialog.RenameDialog;
import com.bazooka.bluetoothbox.utils.SpManager;
import com.bazooka.bluetoothbox.utils.ToastUtils;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzManagerUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/12
 *         作用：继电器开关页面
 */
public class SwitchActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_name_1)
    TextView etName1;
    @BindView(R.id.iv_switch_1)
    ImageView ivSwitch1;
    @BindView(R.id.fl_switch_1)
    FrameLayout flSwitch1;
    @BindView(R.id.iv_power_1)
    ImageView ivPower1;
    @BindView(R.id.et_name_2)
    TextView etName2;
    @BindView(R.id.iv_switch_2)
    ImageView ivSwitch2;
    @BindView(R.id.fl_switch_2)
    FrameLayout flSwitch2;
    @BindView(R.id.iv_power_2)
    ImageView ivPower2;
    @BindView(R.id.et_name_3)
    TextView etName3;
    @BindView(R.id.iv_switch_3)
    ImageView ivSwitch3;
    @BindView(R.id.fl_switch_3)
    FrameLayout flSwitch3;
    @BindView(R.id.iv_power_3)
    ImageView ivPower3;
    @BindView(R.id.et_name_4)
    TextView etName4;
    @BindView(R.id.iv_switch_4)
    ImageView ivSwitch4;
    @BindView(R.id.fl_switch_4)
    FrameLayout flSwitch4;
    @BindView(R.id.iv_power_4)
    ImageView ivPower4;

//    private MediaPlayer mediaPlayer;
    private RenameDialog mRenameDialog;

    private boolean[] isSwitchOpen = new boolean[4];


    private Handler mHandler = new Handler();

    @Override
    public void initData() {

//        mediaPlayer = MediaPlayer.create(mContext, R.raw.switchclick);
        mRenameDialog = new RenameDialog(this);
        mRenameDialog.setDemandMessage(R.string.switch_rename_demand);
        mRenameDialog.setInputRange("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        mRenameDialog.setInputFilters(new InputFilter[]{new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(10)});
    }

    @Override
    public void initView() {
        etName1.setText(SpManager.getInstance().getSwitchName(1));
        etName2.setText(SpManager.getInstance().getSwitchName(2));
        etName3.setText(SpManager.getInstance().getSwitchName(3));
        etName4.setText(SpManager.getInstance().getSwitchName(4));

        BluzManagerUtils.getInstance().queryLedAndLightState();

    }

    @Override
    public void addViewListener() {

        ivBack.setOnClickListener(v -> finish());

        mRenameDialog.setOnOkClickListener((index, name) -> {
            if (TextUtils.isEmpty(name)) {
                mRenameDialog.setHintMessageColor(Color.RED);
                return;
            }
            switch (index) {
                case 1:
                    etName1.setText(name);
                    SpManager.getInstance().saveSwitchName(1, name);
                    break;
                case 2:
                    etName2.setText(name);
                    SpManager.getInstance().saveSwitchName(2, name);
                    break;
                case 3:
                    etName3.setText(name);
                    SpManager.getInstance().saveSwitchName(3, name);
                    break;
                case 4:
                    etName4.setText(name);
                    SpManager.getInstance().saveSwitchName(4, name);
                    break;
                default:
                    break;
            }
            mRenameDialog.dismiss();
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_switch;
    }

    private void changeSwitch(int index, boolean isOpen) {
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.seekTo(0);
//        } else {
//            mediaPlayer.start();
//        }
        sendSwitchState(index, isOpen);
    }

    private void sendSwitchState(int index, boolean isOpen) {
        BluzManagerUtils.getInstance().sendSwitch(index, isOpen ? 0 : 1);
        BluzManagerUtils.getInstance().queryLedAndLightState();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mediaPlayer.release();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @OnClick({R.id.et_name_1, R.id.et_name_2, R.id.et_name_3, R.id.et_name_4})
    public void onViewClicked(View view) {
        int index = 1;
        String name = null;
        switch (view.getId()) {
            case R.id.et_name_1:
                index = 1;
                name = etName1.getText().toString();
                break;
            case R.id.et_name_2:
                index = 2;
                name = etName2.getText().toString();
                break;
            case R.id.et_name_3:
                index = 3;
                name = etName3.getText().toString();
                break;
            case R.id.et_name_4:
                index = 4;
                name = etName4.getText().toString();
                break;
            default:
                break;
        }
        mRenameDialog.show(index, name);
    }


    @OnClick({R.id.iv_switch_1, R.id.fl_switch_2, R.id.iv_switch_3, R.id.iv_switch_4})
    public void onSwitchClicked(View view) {
        view.setSelected(!view.isSelected());
        switch (view.getId()) {
            case R.id.iv_switch_1:
                ivPower1.setSelected(!ivPower1.isSelected());
                changeSwitch(0, isSwitchOpen[0]);
                break;
            case R.id.fl_switch_2:
                ivPower2.setSelected(!ivPower2.isSelected());
                changeSwitch(1, isSwitchOpen[1]);
                break;
            case R.id.iv_switch_3:
                ivPower3.setSelected(!ivPower3.isSelected());
                changeSwitch(2, isSwitchOpen[2]);
                break;
            case R.id.iv_switch_4:
                ivPower4.setSelected(!ivPower4.isSelected());
                changeSwitch(3, isSwitchOpen[3]);
                break;
            default:
                break;
        }
    }

    /**
     * 继电器结果返回成功
     * @param event 事件
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCustomCommand(CustomCommandEvent event){
        if (event.getWhat() == BluzManagerUtils.KEY_ANS_LIGHT_CONTROL_STATE) {

            String data = String.format("%4s", Integer.toBinaryString(event.getParam2())).replaceAll(" ", "0");
            Logger.d(data);
            char[] datas = data.toCharArray();
            ivSwitch1.setSelected(datas[3] == '1');
            ivPower1.setSelected(datas[3] == '1');
            isSwitchOpen[0] = datas[3] == '1';

            ivSwitch2.setSelected(datas[2] == '1');
            ivPower2.setSelected(datas[2] == '1');
            isSwitchOpen[1] = datas[2] == '1';

            ivSwitch3.setSelected(datas[1] == '1');
            ivPower3.setSelected(datas[1] == '1');
            isSwitchOpen[2] = datas[1] == '1';

            ivSwitch4.setSelected(datas[0] == '1');
            ivPower4.setSelected(datas[0] == '1');
            isSwitchOpen[3] = datas[0] == '1';
        }
    }


}
