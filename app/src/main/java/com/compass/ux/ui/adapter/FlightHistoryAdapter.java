package com.compass.ux.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemEquipmentTypeBinding;
import com.compass.ux.databinding.ItemFlightHistoryBinding;
import com.compass.ux.ui.activity.EquipmentDetailsActivity;
import com.compass.ux.ui.activity.FlightHistoryActivity;


/**
 * 设备状态
 */

public class FlightHistoryAdapter extends BaseAdapter<String, ItemFlightHistoryBinding> {
    Context context;
    public FlightHistoryAdapter(Context context) {
        this.context=context;
    }

    @Override
    protected void onBindingData(BaseHolder<ItemFlightHistoryBinding> holder, String s, int position) {
        holder.getViewBinding().layoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlightHistoryActivity.actionStart(context);
            }
        });
    }

    @Override
    protected ItemFlightHistoryBinding onBindingView(ViewGroup viewGroup) {
        ItemFlightHistoryBinding itemFlightHistoryBinding = ItemFlightHistoryBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemFlightHistoryBinding;
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}