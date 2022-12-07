package com.compass.ux.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemGalleryBinding;
import com.compass.ux.entity.MyGallyData;

import java.util.List;

import dji.sdk.media.MediaFile;


/**
 * 相册
 */

public class GalleryAdapter extends BaseAdapter<String, ItemGalleryBinding> {

    List<MyGallyData> myGallyDataList;

    @Override
    protected void onBindingData(BaseHolder<ItemGalleryBinding> holder, String s, int position) {
        holder.getViewBinding().tvName.setText(myGallyDataList.get(position).getMediaFile().getFileName());
        holder.getViewBinding().tvTime.setText(myGallyDataList.get(position).getMediaFile().getDateCreated());
        holder.getViewBinding().ivGallery.setImageBitmap(myGallyDataList.get(position).getMediaFile().getThumbnail());
        holder.getViewBinding().cbPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    myGallyDataList.get(position).setChecked(true);
                } else {
                    myGallyDataList.get(position).setChecked(false);
                }
            }
        });
        holder.getViewBinding().cbPhoto.setChecked(myGallyDataList.get(position).isChecked());
        holder.getViewBinding().ivGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getViewBinding().cbPhoto.isChecked() == true) {
                    holder.getViewBinding().cbPhoto.setChecked(false);
                } else {
                    holder.getViewBinding().cbPhoto.setChecked(true);
                }
            }
        });
    }

    @Override
    protected ItemGalleryBinding onBindingView(ViewGroup viewGroup) {
        ItemGalleryBinding itemGalleryBinding = ItemGalleryBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemGalleryBinding;
    }

    public void setData(List<MyGallyData> datas) {
        this.myGallyDataList = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (myGallyDataList != null) {
            return myGallyDataList.size();
        } else {
            return 0;
        }
    }
}