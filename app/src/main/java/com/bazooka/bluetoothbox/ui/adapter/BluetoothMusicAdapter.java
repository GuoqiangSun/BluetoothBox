package com.bazooka.bluetoothbox.ui.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.bean.Music;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/28
 *         作用：蓝牙 模式音乐列表界面
 */

public class BluetoothMusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {

    public BluetoothMusicAdapter(@Nullable List<Music> data) {
        super(R.layout.item_music, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Music item) {

        holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getAlbum()) ? "unknown" : item.getArtist());
        holder.setText(R.id.tv_info, item.getFileName());

    }
}
