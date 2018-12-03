package com.bazooka.bluetoothbox.ui.adapter;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.cache.db.entity.SelectedColor;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/12/26
 *         作用：颜色选择适配器
 */

public class ColorsAdapterV2 extends BaseMultiItemQuickAdapter<SelectedColor, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ColorsAdapterV2(List<SelectedColor> data) {
        super(data);
        addItemType(SelectedColor.ITEM_TYPE_COLOR, R.layout.item_color);
        addItemType(SelectedColor.ITEM_TYPE_ADD, R.layout.item_add);
    }

    @Override
    protected void convert(BaseViewHolder holder, SelectedColor item) {
        switch (holder.getItemViewType()) {
            case SelectedColor.ITEM_TYPE_COLOR:
                holder.setBackgroundColor(R.id.v_color, item.getColor());
                holder.setVisible(R.id.iv_remove_color, item.isRemove());
                holder.addOnLongClickListener(R.id.v_color);
                holder.addOnClickListener(R.id.v_color);

                break;
            case SelectedColor.ITEM_TYPE_ADD:
                holder.addOnClickListener(R.id.iv_add_color);
                break;
            default:
                break;
        }
    }
}
