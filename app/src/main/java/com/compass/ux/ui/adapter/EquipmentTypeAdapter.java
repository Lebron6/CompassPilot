package com.compass.ux.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemEquipmentTypeBinding;
import com.compass.ux.ui.activity.EquipmentDetailsActivity;


/**
 * 设备状态
 */

public class EquipmentTypeAdapter extends BaseAdapter<String, ItemEquipmentTypeBinding> {
    Context context;
    public EquipmentTypeAdapter(Context context) {
        this.context=context;
    }

    @Override
    protected void onBindingData(BaseHolder<ItemEquipmentTypeBinding> holder, String s, int position) {
        holder.getViewBinding().layoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EquipmentDetailsActivity.actionStart(context);
            }
        });
    }

    @Override
    protected ItemEquipmentTypeBinding onBindingView(ViewGroup viewGroup) {
        ItemEquipmentTypeBinding itemEquipmentTypeBinding = ItemEquipmentTypeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemEquipmentTypeBinding;
    }
}