package com.compass.ux.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemCallbackBinding;
import com.compass.ux.databinding.ItemMessageBinding;


/**
 * 回复
 */

public class CallBackAdapter extends BaseAdapter<String, ItemCallbackBinding> {


    @Override
    protected void onBindingData(BaseHolder<ItemCallbackBinding> holder, String s, int position) {
//        holder.getViewBinding().tvTest.setText("111111");
    }

    @Override
    protected ItemCallbackBinding onBindingView(ViewGroup viewGroup) {
        ItemCallbackBinding itemCallbackBinding = ItemCallbackBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemCallbackBinding;
    }
}