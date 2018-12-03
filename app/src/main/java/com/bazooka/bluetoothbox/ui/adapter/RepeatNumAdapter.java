package com.bazooka.bluetoothbox.ui.adapter;

import android.support.annotation.Nullable;

import com.bazooka.bluetoothbox.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/26
 *         作用：
 */

public class RepeatNumAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RepeatNumAdapter(@Nullable List<String> data) {
        super(R.layout.item_repeat_picker, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, String num) {
        holder.setText(R.id.tv_num, num);
    }
}
