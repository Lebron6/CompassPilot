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
        WHITE_HOT.setName("WHITE_HOT");
        WHITE_HOT.setImgRes(R.mipmap.bg_compass);
        sourceList.add(WHITE_HOT);

        PaletteSource BLACK_HOT = new PaletteSource();
        BLACK_HOT.setName("BLACK_HOT");
        BLACK_HOT.setImgRes(R.mipmap.bg_compass);
        sourceList.add(BLACK_HOT);

        PaletteSource RED_HOT = new PaletteSource();
        RED_HOT.setName("RED_HOT");
        RED_HOT.setImgRes(R.mipmap.bg_compass);
        sourceList.add(RED_HOT);

        PaletteSource GREEN_HOT = new PaletteSource();
        GREEN_HOT.setName("GREEN_HOT");
        GREEN_HOT.setImgRes(R.mipmap.bg_compass);
        sourceList.add(GREEN_HOT);

        PaletteSource FUSION = new PaletteSource();
        FUSION.setName("FUSION");
        FUSION.setImgRes(R.mipmap.bg_compass);
        sourceList.add(FUSION);

        PaletteSource RAINBOW = new PaletteSource();
        RAINBOW.setName("RAINBOW");
        RAINBOW.setImgRes(R.mipmap.bg_compass);
        sourceList.add(RAINBOW);

        PaletteSource IRONBOW_1 = new PaletteSource();
        IRONBOW_1.setName("IRONBOW_1");
        IRONBOW_1.setImgRes(R.mipmap.bg_compass);
        sourceList.add(IRONBOW_1);

        PaletteSource IRONBOW_2 = new PaletteSource();
        IRONBOW_2.setName("IRONBOW_2");
        IRONBOW_2.setImgRes(R.mipmap.bg_compass);
        sourceList.add(IRONBOW_2);

        PaletteSource ICE_FIRE = new PaletteSource();
        ICE_FIRE.setName("ICE_FIRE");
        ICE_FIRE.setImgRes(R.mipmap.bg_compass);
        sourceList.add(ICE_FIRE);

        PaletteSource SEPIA = new PaletteSource();
        SEPIA.setName("SEPIA");
        SEPIA.setImgRes(R.mipmap.bg_compass);
        sourceList.add(SEPIA);

        PaletteSource GLOWBOW = new PaletteSource();
        GLOWBOW.setName("GLOWBOW");
        GLOWBOW.setImgRes(R.mipmap.bg_compass);
        sourceList.add(GLOWBOW);

        PaletteSource COLOR_1 = new PaletteSource();
        COLOR_1.setName("COLOR_1");
        COLOR_1.setImgRes(R.mipmap.bg_compass);
        sourceList.add(COLOR_1);

        PaletteSource COLOR_2 = new PaletteSource();
        COLOR_2.setName("COLOR_2");
        COLOR_2.setImgRes(R.mipmap.bg_compass);
        sourceList.add(COLOR_2);

        PaletteSource RAIN = new PaletteSource();
        RAIN.setName("RAIN");
        RAIN.setImgRes(R.mipmap.bg_compass);
        sourceList.add(RAIN);

        PaletteSource HOT_SPOT = new PaletteSource();
        HOT_SPOT.setName("HOT_SPOT");
        HOT_SPOT.setImgRes(R.mipmap.bg_compass);
        sourceList.add(HOT_SPOT);

        PaletteSource RAINBOW2 = new PaletteSource();
        RAINBOW2.setName("RAINBOW2");
        RAINBOW2.setImgRes(R.mipmap.bg_compass);
        sourceList.add(RAINBOW2);

        PaletteSource GRAY = new PaletteSource();
        GRAY.setName("GRAY");
        GRAY.setImgRes(R.mipmap.bg_compass);
        sourceList.add(GRAY);

        PaletteSource HOT_METAL = new PaletteSource();
        HOT_METAL.setName("HOT_METAL");
        HOT_METAL.setImgRes(R.mipmap.bg_compass);
        sourceList.add(HOT_METAL);

        PaletteSource COLD_SPOT = new PaletteSource();
        COLD_SPOT.setName("COLD_SPOT");
        COLD_SPOT.setImgRes(R.mipmap.bg_compass);
        sourceList.add(COLD_SPOT);


        adapter = new PaletteAdapter();
        adapter.setData(sourceList);
        RecyclerViewHelper.initRecyclerViewH(activity, recyclerView,false, adapter);
    }

}
