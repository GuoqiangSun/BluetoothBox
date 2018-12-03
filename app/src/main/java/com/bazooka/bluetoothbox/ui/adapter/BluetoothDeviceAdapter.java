package com.bazooka.bluetoothbox.ui.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.bean.BluetoothDeviceBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.actions.ibluz.factory.BluzDeviceFactory.ConnectionState;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/17
 *         作用：蓝牙设备列表适配器
 */

public class BluetoothDeviceAdapter extends BaseQuickAdapter<BluetoothDeviceBean, BaseViewHolder>{

    public BluetoothDeviceAdapter(@Nullable List<BluetoothDeviceBean> data) {
        super(R.layout.item_bluetooth_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDeviceBean item) {
        String deviceName = item.getDevice().getName();
        helper.setText(R.id.tv_device_name, deviceName != null ? deviceName : item.getDevice().getAddress());
        boolean isConnected = false;
        int textInfoResId ;
        switch (item.getState()) {
            case ConnectionState.SPP_DISCONNECTED:
            case ConnectionState.A2DP_DISCONNECTED:
                textInfoResId = R.string.notice_device_disconnected;
                isConnected = false;
                break;
            case ConnectionState.SPP_CONNECTING:
            case ConnectionState.A2DP_CONNECTING:
            case ConnectionState.A2DP_CONNECTED:
                textInfoResId = R.string.notice_device_connecting;
                isConnected = false;
                break;
            case ConnectionState.SPP_CONNECTED:
                textInfoResId = R.string.notice_device_connected;
                isConnected = true;
                break;
            case ConnectionState.SPP_FAILURE:
            case ConnectionState.A2DP_FAILURE:
                textInfoResId = R.string.notice_device_disconnected;
                isConnected = false;
                break;
            case ConnectionState.A2DP_PAIRING:
                textInfoResId = R.string.notice_device_media_pairing;
                isConnected = false;
                break;
            default:
                textInfoResId = R.string.notice_device_disconnected;
                isConnected = false;
                break;
        }
        helper.getView(R.id.iv_state).setSelected(isConnected);
    }
}
