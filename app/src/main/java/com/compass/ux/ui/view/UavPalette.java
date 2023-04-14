package com.compass.ux.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.compass.ux.R;
import com.compass.ux.entity.PaletteSource;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.ui.activity.GalleryActivity;
import com.compass.ux.ui.adapter.PaletteAdapter;
import com.compass.ux.ui.fragment.setting.AvoidingObstaclesFragment;
import com.compass.ux.ui.fragment.setting.BatteryFragment;
import com.compass.ux.ui.fragment.setting.FlightControllerFragment;
import com.compass.ux.ui.fragment.setting.GimbalSettingFragment;
import com.compass.ux.ui.fragment.setting.GraphicFragment;
import com.compass.ux.ui.fragment.setting.RemoteControlFragment;
import com.qmuiteam.qmui.util.QMUIDirection;
import com.qmuiteam.qmui.util.QMUIViewHelper;

import java.util.ArrayList;
import java.util.List;

public class UavPalette extends LinearLayout {
    int visible = View.GONE;//默认显示
    private Context _context;
    private RecyclerView recyclerView;
    PaletteAdapter adapter;
    private List<PaletteSource> sourceList;

    public UavPalette(final Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        // 加载布局
        View view = LayoutInflater.from(context).inflate(R.layout.layout_uav_palette, this, true);//最后参数必须为true
        recyclerView = view.findViewById(R.id.rv_palette);
        sourceList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            PaletteSource paletteSource = new PaletteSource();
            paletteSource.setImgRes(R.mipmap.bg_compass);
            paletteSource.setName("罗盘");
            sourceList.add(paletteSource);
        }
        adapter = new PaletteAdapter();
        adapter.setData(sourceList);
        RecyclerViewHelper.initRecyclerViewH(activity, recyclerView,false, adapter);

    }

    FragmentActivity activity;

//    public void setContex(FragmentActivity activity) {
//        this.activity = activity;
//
//    }


    public void Toggle() {
        if (visible == View.GONE) {
            visible = View.VISIBLE;
            QMUIViewHelper.slideIn(this, 300, null, true, QMUIDirection.BOTTOM_TO_TOP);
        } else {
            visible = View.GONE;
            QMUIViewHelper.slideOut(this, 300, null, true, QMUIDirection.TOP_TO_BOTTOM);
        }
    }


}
