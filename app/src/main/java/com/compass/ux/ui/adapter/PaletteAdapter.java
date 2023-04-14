package com.compass.ux.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.compass.ux.R;
import com.compass.ux.base.BaseAdapter;
import com.compass.ux.base.BaseHolder;
import com.compass.ux.databinding.ItemGalleryBinding;
import com.compass.ux.databinding.ItemPaletteBinding;
import com.compass.ux.entity.MyGallyData;
import com.compass.ux.entity.PaletteSource;

import java.util.List;


/**
 * 调色板
 */

public class PaletteAdapter extends BaseAdapter<String, ItemPaletteBinding> {

    List<PaletteSource> myGallyDataList;

    @Override
    protected void onBindingData(BaseHolder<ItemPaletteBinding> holder, String s, int position) {
        holder.getViewBinding().tvName.setText(myGallyDataList.get(position).getName());
        holder.getViewBinding().ivPalette.setBackgroundResource(myGallyDataList.get(position).getImgRes());
        holder.getViewBinding().cbPale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    myGallyDataList.get(position).setChecked(true);
                } else {
                    myGallyDataList.get(position).setChecked(false);
                }
            }
        });
        holder.getViewBinding().cbPale.setChecked(myGallyDataList.get(position).isChecked());

        holder.getViewBinding().ivPalette.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                for (int i = 0; i < myGallyDataList.size(); i++) {
                    myGallyDataList.get(i).setChecked(false);
                    setData(myGallyDataList);
                }
                if (holder.getViewBinding().cbPale.isChecked() == true) {
                    holder.getViewBinding().cbPale.setChecked(false);
                } else {
                    holder.getViewBinding().cbPale.setChecked(true);

                }
            }
        });
    }

    @Override
    protected ItemPaletteBinding onBindingView(ViewGroup viewGroup) {
        ItemPaletteBinding itemGalleryBinding = ItemPaletteBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return itemGalleryBinding;
    }

    public void setData(List<PaletteSource> datas) {
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