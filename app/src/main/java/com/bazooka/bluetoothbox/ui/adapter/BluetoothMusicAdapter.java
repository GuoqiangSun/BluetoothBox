package com.bazooka.bluetoothbox.ui.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.bean.Music;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/11/28
 * 作用：蓝牙 模式音乐列表界面
 */

public class BluetoothMusicAdapter extends BaseQuickAdapter<Music, BaseViewHolder> {

    public BluetoothMusicAdapter(@Nullable List<Music> data) {
        super(R.layout.item_music, data);
    }

    private int position = -1;

    public void select(int position) {
        int lastPosition = this.position;
        this.position = position;
        notifyItemChanged(position);
        if (lastPosition != -1) {
            notifyItemChanged(lastPosition);
        }
    }

    private int selectColor = Color.parseColor("#FC2697");
    private int normalColor = Color.parseColor("#FFFFFF");

    @Override
    protected void convert(BaseViewHolder holder, Music item) {
//        Log.v("bluz", "" + position + " - " + holder.getLayoutPosition());
        if (position != -1) {
            if (holder.getLayoutPosition() == position) {
                holder.setTextColor(R.id.tv_name, selectColor);
                holder.setTextColor(R.id.tv_info, selectColor);
            } else {
                holder.setTextColor(R.id.tv_name, normalColor);
                holder.setTextColor(R.id.tv_info, normalColor);
            }
        }

        holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getAlbum()) ? "unknown" : item.getArtist());
        holder.setText(R.id.tv_info, item.getFileName());

    }


}
