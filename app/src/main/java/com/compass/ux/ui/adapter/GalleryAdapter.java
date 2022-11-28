package com.compass.ux.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemGalleryBinding;


/**
 * 相册
 */

public class GalleryAdapter extends BaseAdapter<String, ItemGalleryBinding> {


    @Override
    protected void onBindingData(BaseHolder<ItemGalleryBinding> holder, String s, int position) {
//        holder.getViewBinding().tvTest.setText("111111");
    }

    @Override
    protected ItemGalleryBinding onBindingView(ViewGroup viewGroup) {
        ItemGalleryBinding itemGalleryBinding = ItemGalleryBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemGalleryBinding;
    }
}