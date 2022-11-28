package com.compass.ux.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemMessageBinding;
import com.compass.ux.databinding.ItemTaskTypeBinding;
import com.compass.ux.tools.RecyclerViewHelper;


/**
 * 消息
 */

public class MessageAdapter extends BaseAdapter<String, ItemMessageBinding> {

    Context context;
    public MessageAdapter(Context context) {
        this.context=context;
    }

    @Override
    protected void onBindingData(BaseHolder<ItemMessageBinding> holder, String s, int position) {
        CallBackAdapter adapter = new CallBackAdapter();
        RecyclerViewHelper.initRecyclerViewV(context, holder.getViewBinding().rvCallback, false, adapter);
    }

    @Override
    protected ItemMessageBinding onBindingView(ViewGroup viewGroup) {
        ItemMessageBinding itemMessageBinding = ItemMessageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemMessageBinding;
    }
}