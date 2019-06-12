package com.bazooka.bluetoothbox.ui.adapter;

import android.graphics.Color;
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
    protected void convert(BaseViewHolder holder, BluzManagerData.PListEntry item) {

        if (position != -1) {
            if (holder.getLayoutPosition() == position) {
                holder.setTextColor(R.id.tv_name, selectColor);
                holder.setTextColor(R.id.tv_info, selectColor);
            } else {
                holder.setTextColor(R.id.tv_name, normalColor);
                holder.setTextColor(R.id.tv_info, normalColor);
            }
        }

        holder.setText(R.id.tv_name, TextUtils.isEmpty(item.artist) ? "unknown" : item.artist);
        holder.setText(R.id.tv_info, item.name);

    }
}
