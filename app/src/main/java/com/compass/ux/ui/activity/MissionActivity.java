package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.maps.MapView;
import com.compass.ux.R;
import com.compass.ux.base.BaseActivity;

import com.dji.mapkit.core.maps.DJIMap;
import com.dji.mapkit.core.models.DJILatLng;
import com.dji.mapkit.core.models.annotations.DJICircle;
import com.dji.mapkit.core.models.annotations.DJICircleOptions;


import java.util.Map;

import androidx.annotation.NonNull;
import dji.ux.widget.MapWidget;




public class MissionActivity extends BaseActivity {

    private MapWidget mapWidget;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MissionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        initMap(savedInstanceState);
    }


    private void initMap(Bundle savedInstanceState) {
        mapWidget = findViewById(R.id.map_widget);
        mapWidget.initAMap(new MapWidget.OnMapReadyListener() {
            @Override
            public void onMapReady(@NonNull DJIMap map) {
                map.getUiSettings().setZoomControlsEnabled(false);//禁用右下角地图视角放大缩小
            }
        });
        mapWidget.onCreate(savedInstanceState);
    }

    float missionLength = 0;


    @Override
    protected void onResume() {
        super.onResume();
        mapWidget.onResume();
    }

    @Override
    protected void onPause() {
        mapWidget.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapWidget.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapWidget.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapWidget.onLowMemory();
    }
    @Override
    public boolean useEventBus() {
        return false;
    }


}
