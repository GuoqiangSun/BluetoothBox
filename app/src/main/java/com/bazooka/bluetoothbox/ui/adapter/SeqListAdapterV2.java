package com.bazooka.bluetoothbox.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.PopupWindow;

import com.bazooka.bluetoothbox.R;
import com.bazooka.bluetoothbox.base.recyclerview.MultiDraggableAdapter;
import com.bazooka.bluetoothbox.cache.db.entity.LedFlash;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2017/11/24
 *         作用：闪法列表适配器
 */

public class SeqListAdapterV2 extends MultiDraggableAdapter<LedFlash, BaseViewHolder> {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private PopupWindow mEditPopup;

    public SeqListAdapterV2(@Nullable List<LedFlash> data) {
        super(data);
        addItemType(LedFlash.ITEM_TYPE_LED_FLASH, R.layout.item_seq_list);
        addItemType(LedFlash.ITEM_TYPE_LINE, R.layout.item_line);
    }

    @Override
    protected void convert(BaseViewHolder holder, LedFlash item) {
        int position = holder.getLayoutPosition();
        switch (holder.getItemViewType()) {
            case LedFlash.ITEM_TYPE_LED_FLASH:
                if(position == 0) {
                    holder.setVisible(R.id.iv_remove, false);
                    holder.setVisible(R.id.iv_upward, false);
                    holder.setVisible(R.id.iv_downward, false);
                } else {
                    holder.setVisible(R.id.iv_remove, true);
                    holder.setVisible(R.id.iv_upward, true);
                    holder.setVisible(R.id.iv_downward, true);
                }
                holder.setText(R.id.tv_index, item.getSort() + ".");
                holder.setText(R.id.tv_seq_name, item.getName());
                holder.setText(R.id.tv_time, format.format(new Date(item.getModificationTime())));
                holder.addOnClickListener(R.id.iv_remove);
                holder.addOnClickListener(R.id.iv_upward);
                holder.addOnClickListener(R.id.iv_downward);
                holder.addOnClickListener(R.id.tv_seq_name);
                break;
            case LedFlash.ITEM_TYPE_LINE:
                break;
            default:
                break;
        }

    }

}
