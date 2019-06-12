package com.bazooka.bluetoothbox.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.cache.db.entity.FmChannelCache;
import com.bazooka.bluetoothbox.listener.FmItemDleteListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author 尹晓童
 * 邮箱：yinxtno1@yeah.net
 * 时间：2017/11/22
 * 作用：Fm频道列表适配器
 */

public class FmChannelAdapter extends BaseQuickAdapter<FmChannelCache, BaseViewHolder> {

    private Context mContext;
    private FmItemDleteListener listener;

    public FmChannelAdapter(Context context, @Nullable List<FmChannelCache> data) {
        super(R.layout.item_fm, data);
        mContext = context;

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
    protected void convert(BaseViewHolder helper, FmChannelCache item) {

        if (position != -1) {
            if (helper.getLayoutPosition() == position) {
                helper.setTextColor(R.id.fm_name, selectColor);
                helper.setTextColor(R.id.fm_channel, selectColor);
            } else {
                helper.setTextColor(R.id.fm_name, normalColor);
                helper.setTextColor(R.id.fm_channel, normalColor);
            }
        }

        int layoutPosition = helper.getLayoutPosition();
        helper.setText(R.id.fm_name, "ST " + String.valueOf(layoutPosition + 1));
        helper.setText(R.id.fm_channel, mContext.getString(R.string.fm_channel, item.getChannel() / 1000f));

        helper.getView(R.id.fm_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//刪除該選項
                listener.delete(helper.getAdapterPosition());
            }
        });

    }

    public void setFmitemDeleteListener(FmItemDleteListener listener) {
        this.listener = listener;
    }
}
