package com.bazooka.bluetoothbox.ui.dialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.actions.ibluz.factory.BluzDeviceFactory;
import com.actions.ibluz.factory.IBluzDevice;
import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.dialog.BaseDialogFragment;
import com.bazooka.bluetoothbox.bean.BluetoothDeviceBean;
import com.bazooka.bluetoothbox.exception.BluetoothNotSupportException;
import com.bazooka.bluetoothbox.ui.adapter.BluetoothDeviceAdapter;
import com.bazooka.bluetoothbox.utils.DialogFragmentUtils;
import com.bazooka.bluetoothbox.utils.ToastUtil;
import com.bazooka.bluetoothbox.utils.bluetooth.BluzDeviceUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/11/17
 * 作用：蓝牙搜索弹框
 */

public class BluetoothSearchDialog extends BaseDialogFragment {

    private final static int MAX_RETRY_TIMES = 5;
    private final static int REQUEST_BLUETOOTH_ON = 100;

    private static final int LOCATION_CODE = 20;

    @BindView(R.id.rv_bluetooth_devices)
    RecyclerView rvBluetoothDevices;

    private ProgressBar pbLoading;
    private BluzDeviceUtils bluzDeviceUtils;
    private List<BluetoothDeviceBean> mDeviceList = new ArrayList<>();
    private BluetoothDeviceAdapter mAdapter;
    private LoadingDialog connectingDialog;

    private boolean mDiscoveryStarted;
    private int mConnectRetryTimes;

    private Handler mHandler;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_device_search, container, false);
    }

    @Override
    public void initData() {

        connectingDialog = new LoadingDialog(getContext());

        mAdapter = new BluetoothDeviceAdapter(mDeviceList);
        bluzDeviceUtils = BluzDeviceUtils.getInstance();
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            if (mDeviceList.isEmpty()) {
                ToastUtil.showSearchFailToast();
                DialogFragmentUtils.dismissDialog(
                        ((AppCompatActivity) mContext).getSupportFragmentManager(), getTag());
            }
        }, 15 * 1000);
    }

    @Override
    public void initView() {
        rvBluetoothDevices.setAdapter(mAdapter);
        rvBluetoothDevices.setLayoutManager(new LinearLayoutManager(mContext));
        ((SimpleItemAnimator) rvBluetoothDevices.getItemAnimator()).setSupportsChangeAnimations(false);
        pbLoading = new ProgressBar(getContext());

    }

    @Override
    public void addViewListener() {

        bluzDeviceUtils.setOnDiscoveryListener(new IBluzDevice.OnDiscoveryListener() {
            @Override
            public void onConnectionStateChanged(BluetoothDevice device, int state) {
                if (device != null) {
                    BluetoothDeviceBean deviceBean = findDevice(device);
                    if (deviceBean == null) {
                        deviceBean = new BluetoothDeviceBean(device, state);
                        mDeviceList.add(deviceBean);
                    }

                    if (state == BluzDeviceFactory.ConnectionState.A2DP_FAILURE) {
                        state = BluzDeviceFactory.ConnectionState.A2DP_DISCONNECTED;
                        setCancelable(true);
                        if (!retry(device)) {
                            setCancelable(true);
                            ToastUtil.showConnectFailToast();
                            if (connectingDialog.isShowing()) {
                                connectingDialog.dismiss();
                            }
                        }
                    } else if (state == BluzDeviceFactory.ConnectionState.SPP_FAILURE) {
                        state = BluzDeviceFactory.ConnectionState.SPP_DISCONNECTED;
                        setCancelable(true);
                        if (!retry(device)) {
                            setCancelable(true);
                            ToastUtil.showConnectFailToast();
                            if (connectingDialog.isShowing()) {
                                connectingDialog.dismiss();
                            }
                        }
                    }

                    deviceBean.setState(state);
                    mAdapter.notifyItemChanged(mDeviceList.indexOf(deviceBean));

                }
            }

            @Override
            public void onDiscoveryStarted() {
                pbLoading.setVisibility(View.VISIBLE);
                mAdapter.setFooterView(pbLoading);
                Logger.d("onDiscoveryStarted");
                initBluetoothBean();
            }

            @Override
            public void onDiscoveryFinished() {
                setCancelable(true);
                if (mHandler != null) {
                    mHandler.postDelayed(() -> {
                        pbLoading.setVisibility(View.GONE);
                    }, 500);
                }
            }

            @Override
            public void onFound(BluetoothDevice device) {
//                Logger.d(device.getName() + ":" + device.getAddress());
                if (device != null) {
                    String name = device.getName();
                    if (name != null && (name.contains("G2") || name.contains("g2"))) {
                        if (findDevice(device) == null) {
                            mDeviceList.add(new BluetoothDeviceBean(device,
                                    BluzDeviceFactory.ConnectionState.SPP_DISCONNECTED));
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
//                        Logger.d("filter " + device.getName() + ":" + device.getAddress());
                        Log.v("g2", "filter " + device.getName() + ":" + device.getAddress());
                    }
                }

                mDiscoveryStarted = true;
            }
        });

        mAdapter.setOnItemClickListener((adapter, view, position) -> {

            connectingDialog.show();
            bluzDeviceUtils.getBluzDevice().connect(mDeviceList.get(position).getDevice());
        });

    }

    @Override
    protected int returnDialogStyle() {
        return R.style.SearchDialogStyle;
    }

    private boolean retry(BluetoothDevice device) {
        bluzDeviceUtils.disconnect(device);
        if (mConnectRetryTimes < MAX_RETRY_TIMES) {
            bluzDeviceUtils.retry(device);
            mConnectRetryTimes++;
            return true;
        } else {
            mConnectRetryTimes = 0;
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startDiscovery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BLUETOOTH_ON) {
            if (resultCode == Activity.RESULT_OK) {
                bluzDeviceUtils.startDiscovery();
            } else {
                dismiss();
            }
        }
    }

    private void startDiscovery() {
        mConnectRetryTimes = 0;
        mDiscoveryStarted = false;
        initBluetoothBean();

        try {
            if (bluzDeviceUtils.isEnable()) {
                bluzDeviceUtils.startDiscovery();
            } else {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_BLUETOOTH_ON);
            }
        } catch (BluetoothNotSupportException e) {
            e.printStackTrace();
        }


    }

    /**
     * 设备列表中是否已经存在该设备
     *
     * @param device 待查询设备
     * @return 存在的话，就返回该实体类，不存在返回 null
     */
    private BluetoothDeviceBean findDevice(BluetoothDevice device) {
        for (BluetoothDeviceBean deviceBean : mDeviceList) {
            if (deviceBean.getDevice().equals(device)) {
                return deviceBean;
            }
        }
        return null;
    }

    @Override
    public void dismiss() {
        if (connectingDialog.isShowing()) {
            connectingDialog.dismiss();
        }
        super.dismiss();
    }

    @Override
    protected boolean isShowTitle() {
        return false;
    }

    private void initBluetoothBean() {
        if (!mDiscoveryStarted) {
            mDeviceList.clear();
        }
        BluetoothDevice connectedDevice = BluzDeviceUtils.getInstance().getConnectionDevice();
        int state = BluzDeviceFactory.ConnectionState.SPP_DISCONNECTED;
        if (connectedDevice != null) {
            state = getDeviceState(connectedDevice, BluzDeviceFactory.ConnectionState.SPP_CONNECTED);
        } else {
            connectedDevice = BluzDeviceUtils.getInstance().getConnectedA2dpDevice();
            if (connectedDevice != null) {
                state = getDeviceState(connectedDevice, BluzDeviceFactory.ConnectionState.A2DP_CONNECTED);
            }
        }
        if (connectedDevice != null && findDevice(connectedDevice) == null) {
            mDeviceList.add(new BluetoothDeviceBean(connectedDevice, state));
        }

        mAdapter.notifyDataSetChanged();
    }


    private int getDeviceState(BluetoothDevice device, int defaultState) {
        for (BluetoothDeviceBean bean : mDeviceList) {
            if (bean.getDevice().equals(device)) {
                return bean.getState();
            }
        }

        return defaultState;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
