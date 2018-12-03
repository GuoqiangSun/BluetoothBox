package com.bazooka.bluetoothbox.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.actions.ibluz.manager.BluzManagerData;
import com.bazooka.bluetoothbox.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/28
 *         作用：usb 模式音乐列表界面
 */

public class UsbMusicAdapter extends BaseQuickAdapter<BluzManagerData.PListEntry, BaseViewHolder> {

    public UsbMusicAdapter(@Nullable List<BluzManagerData.PListEntry> data) {
        super(R.layout.item_music, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, BluzManagerData.PListEntry item) {

        holder.setText(R.id.tv_name, TextUtils.isEmpty(item.artist) ? "unknown" : item.artist);
        holder.setText(R.id.tv_info, item.name);

    }
}
