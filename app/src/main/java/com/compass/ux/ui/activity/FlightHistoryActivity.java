package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.compass.ux.R;
import com.compass.ux.api.BaseUrl;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityFlightHistoryBinding;
import com.compass.ux.entity.FlightHistoryDetails;
import com.compass.ux.entity.FlightPoints;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.dji.mapkit.core.camera.DJICameraUpdateFactory;
import com.dji.mapkit.core.models.DJICameraPosition;
import com.dji.mapkit.core.models.DJILatLng;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightHistoryActivity extends BaseActivity {

    AMap aMap;
    private ActivityFlightHistoryBinding mBinding;
    private static String ID = "id";

    public static void actionStart(Context context, String id) {
        Intent intent = new Intent(context, FlightHistoryActivity.class);
        intent.putExtra(ID, id);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityFlightHistoryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
        mBinding.mapView.onCreate(savedInstanceState);
        aMap = mBinding.mapView.getMap();
    }

    private void initView() {
        mBinding.layoutFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.jzVideo.SAVE_PROGRESS = false;
        getEquipmentData();
        getFlightPoints();
    }


    private int time=0;
    private void getFlightPoints() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.createRequest2().flightPoints(PreferenceUtils.getInstance().getUserToken(),
                getIntent().getStringExtra(ID)).enqueue(new Callback<FlightPoints>() {
            @Override
            public void onResponse(Call<FlightPoints> call, Response<FlightPoints> response) {
                if (response.body()!=null&&response.body().getCode().equals("200")) {
                  setDataToTextView(response.body().getResults().getFlightPointInfoVos());
                    List<LatLng> points = new ArrayList<>();
                    for (int i = 0; i < response.body().getResults().getPoints().size(); i++) {
                        LatLng latLng = new LatLng(
                                Double.parseDouble(response.body().getResults().getPoints().get(i).get(1)),
                                Double.parseDouble(response.body().getResults().getPoints().get(i).get(0)));
                        points.add(latLng);
                    }
                    aMap.addPolyline(new PolylineOptions().
                            addAll(points).width(5).color(getResources().getColor(R.color.blue)));
//                    LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
//                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            CameraPosition.fromLatLngZoom(
                                    new LatLng(
                                            Double.valueOf(points.get(0).latitude),
                                            Double.valueOf(points.get(0).longitude)),
                                    16)));
                    SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
                    // 设置滑动的图标
                    smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_air));
                    LatLng drivePoint = points.get(0);
                    Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
                    points.set(pair.first, drivePoint);
                    List<LatLng> subList = points.subList(pair.first, points.size());
                    // 设置滑动的轨迹左边点
                    smoothMarker.setPoints(subList);
                    // 设置滑动的总时间
                    smoothMarker.setTotalDuration(response.body().getResults().getPoints().size());
                    // 开始滑动
                    smoothMarker.startSmoothMove();
                }
            }

            @Override
            public void onFailure(Call<FlightPoints> call, Throwable t) {
                ToastUtil.showToast("网络异常:获取飞行历史点位信息失败");
            }
        });
    }

     Timer timer;
    private void setDataToTextView(List<FlightPoints.ResultsDTO.FlightPointInfoVosDTO> flightPointInfoVos) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time=time+1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.tvBattery.setText(flightPointInfoVos.get(time).getPersentOne()+"   "
                                +flightPointInfoVos.get(time).getVoltageOne());
                        mBinding.tvGpsLevel.setText(flightPointInfoVos.get(time).getSatelliteCount()+"级");
                        mBinding.tvDistance.setText(flightPointInfoVos.get(time).getOriginDistance());
                        mBinding.tvWind.setText(flightPointInfoVos.get(time).getWindSpeed());
                        mBinding.tvVSpeed.setText(flightPointInfoVos.get(time).getVerticalSpeed());
                        mBinding.tvHSpeed.setText(flightPointInfoVos.get(time).getHorizontalSpeed());
                        if (time == flightPointInfoVos.size()-1) {
                            timer.cancel();//  调用cancel关闭倒计时
                        }
                    }
                });

            }
        }, 0,1000);
    }

    public void getEquipmentData() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.createRequest2().historyDetail(PreferenceUtils.getInstance().getUserToken(),
                getIntent().getStringExtra(ID)).enqueue(new Callback<FlightHistoryDetails>() {
            @Override
            public void onResponse(Call<FlightHistoryDetails> call, Response<FlightHistoryDetails> response) {
                if (response.body()!=null&&response.body().getCode().equals("200")) {
                    if (!TextUtils.isEmpty(response.body().getResults().getVideoPath())) {
                        StringBuilder stringBuilder = new StringBuilder(response.body().getResults().getVideoPath());
                        String s = stringBuilder.substring(43, response.body().getResults().getVideoPath().length());
                        mBinding.jzVideo.setUp("http://36.154.125.57" + s, "");
                        mBinding.jzVideo.startButton.performClick();

                    }
                    mBinding.tvPlayer.setText(response.body().getResults().getOperator());
                    mBinding.tvFlyMode.setText(response.body().getResults().getFlightModel());
                    mBinding.tvFlyTime.setText(response.body().getResults().getFlightTime());
                    mBinding.tvFlyDuration.setText(response.body().getResults().getFlyingTime());
//                    mBinding.tvFlyAddr.setText(response.body().getResults().getAddress());

                }
            }

            @Override
            public void onFailure(Call<FlightHistoryDetails> call, Throwable t) {
                ToastUtil.showToast("网络异常:获取飞行历史详情信息失败");
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mBinding.jzVideo.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mBinding.mapView.onDestroy();
        if (timer!=null){
            timer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.jzVideo.releaseAllVideos();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mBinding.mapView.onPause();

    }
}
