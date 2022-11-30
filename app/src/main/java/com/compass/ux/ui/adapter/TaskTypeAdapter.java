package com.compass.ux.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemGalleryBinding;
import com.compass.ux.databinding.ItemTaskTypeBinding;


/**
 * 任务类型
 */

public class TaskTypeAdapter extends BaseAdapter<String, ItemTaskTypeBinding> {


    @Override
    protected void onBindingData(BaseHolder<ItemTaskTypeBinding> holder, String s, int position) {
//        holder.getViewBinding().tvTest.setText("111111");
    }

    @Override
    protected ItemTaskTypeBinding onBindingView(ViewGroup viewGroup) {
        ItemTaskTypeBinding itemGalleryBinding = ItemTaskTypeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemGalleryBinding;
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}