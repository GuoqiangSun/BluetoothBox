package com.bazooka.bluetoothbox.ui.adapter;

import android.content.Context;
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

    @Override
    protected void convert(BaseViewHolder helper, FmChannelCache item) {
        helper.setText(R.id.fm_name, item.getName())
                .setText(R.id.fm_channel, mContext.getString(R.string.fm_channel, item.getChannel() / 1000f));
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
