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
        initData();
    }


    FragmentActivity activity;

//    public void setContex(FragmentActivity activity) {
//        this.activity = activity;
//
//    }


    public boolean Toggle() {
        if (visible == View.GONE) {
            visible = View.VISIBLE;
            QMUIViewHelper.slideIn(this, 300, null, true, QMUIDirection.BOTTOM_TO_TOP);
            return true;
        } else {
            visible = View.GONE;
            QMUIViewHelper.slideOut(this, 300, null, true, QMUIDirection.TOP_TO_BOTTOM);
            return false;

        }
    }

    private void initData() {
        sourceList = new ArrayList<>();
        PaletteSource WHITE_HOT = new PaletteSource();
        WHITE_HOT.setName("白热");
        WHITE_HOT.setImgRes(R.mipmap.white_hot);
        sourceList.add(WHITE_HOT);

        PaletteSource BLACK_HOT = new PaletteSource();
        BLACK_HOT.setName("黑热");
        BLACK_HOT.setImgRes(R.mipmap.black_hot);
        sourceList.add(BLACK_HOT);



        PaletteSource FUSION = new PaletteSource();
        FUSION.setName("熔岩");
        FUSION.setImgRes(R.mipmap.rongyan);
        sourceList.add(FUSION);

        PaletteSource RAINBOW = new PaletteSource();
        RAINBOW.setName("彩虹1");
        RAINBOW.setImgRes(R.mipmap.caihong);
        sourceList.add(RAINBOW);

        PaletteSource IRONBOW_1 = new PaletteSource();
        IRONBOW_1.setName("铁红");
        IRONBOW_1.setImgRes(R.mipmap.tiehong);
        sourceList.add(IRONBOW_1);

        PaletteSource ICE_FIRE = new PaletteSource();
        ICE_FIRE.setName("北极");
        ICE_FIRE.setImgRes(R.mipmap.beiji);
        sourceList.add(ICE_FIRE);


        PaletteSource COLOR_2 = new PaletteSource();
        COLOR_2.setName("热铁");
        COLOR_2.setImgRes(R.mipmap.retie);
        sourceList.add(COLOR_2);



        PaletteSource RAINBOW2 = new PaletteSource();
        RAINBOW2.setName("彩虹2");
        RAINBOW2.setImgRes(R.mipmap.caihongtwo);
        sourceList.add(RAINBOW2);

        adapter = new PaletteAdapter();
        adapter.setData(sourceList);
        RecyclerViewHelper.initRecyclerViewH(activity, recyclerView,false, adapter);
    }

}
