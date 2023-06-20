package com.compass.ux.ui.activity;

import static dji.sdk.codec.DJICodecManager.VideoSource.CAMERA;
import static dji.sdk.codec.DJICodecManager.VideoSource.FPV;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.R;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.DataCache;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.entity.MqttLoginOutResult;
import com.compass.ux.manager.AccountManager;
import com.compass.ux.manager.AirLinkManager;
import com.compass.ux.manager.AssistantManager;
import com.compass.ux.manager.BatteryManager;
import com.compass.ux.manager.CameraManager;
import com.compass.ux.manager.DiagnosticsManager;
import com.compass.ux.manager.FlightManager;
import com.compass.ux.manager.GimbalManager;
import com.compass.ux.manager.MissionManager;
import com.compass.ux.manager.RTKManager;
import com.compass.ux.manager.StreamManager;
import com.compass.ux.tools.DisplayUtil;
import com.compass.ux.tools.DroneHelper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.view.LongTouchBtn;
import com.compass.ux.ui.view.UavPalette;
import com.compass.ux.ui.view.UavSettingView;
import com.compass.ux.xclog.XcFileLog;
import com.dji.mapkit.core.maps.DJIMap;
import com.dji.mapkit.core.models.DJILatLng;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.ux.widget.MapWidget;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightActivity extends BaseActivity implements TextureView.SurfaceTextureListener {

    private static boolean isAppStarted = false;

    public static boolean isStarted() {
        return isAppStarted;
    }

    VideoFeeder videoFeeder;
    DJICodecManager codecManager;
    Aircraft aircraft;
    TextureView mTextureView;
    TextureView modifiedVideoStreamPreview;

    private LongTouchBtn btnUp, btnDown, btnForward, btnBackward, btnLeft, btnRight;

    private TextView tvFPS, tvBitRate, tvStreamUrl, tvFirmwareVersion;
    private TextView tvZoomPlus, tvZoomNum, tvZoomReduce;

    private Canvas canvas;
    private Bitmap edgeBitmap;

    private DroneHelper droneHelper;
    private Button btn_login;
    private Button btn_startlive;

    private DJIMap aMap;
    private MapWidget mapWidget;
    private boolean isMapMini = true;
    private int deviceWidth;
    private int deviceHeight;
    RelativeLayout layout_previewer_container;
    ViewGroup parentView;

    TextView tvTest;
    TextView tvPalette;
    private UavSettingView uavSettingView;
    private UavPalette uavPaletteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);
        isAppStarted = true;
        initViews();
        needConnect();

        droneHelper = new DroneHelper();
        initDJIManager();
        intiVirtualStick();
        refreshSDKRelativeUI();
        initMap(savedInstanceState);
    }

    private void initMap(Bundle savedInstanceState) {
        mapWidget = findViewById(R.id.map_widget);
        mapWidget.initAMap(new MapWidget.OnMapReadyListener() {
            @Override
            public void onMapReady(@NonNull DJIMap map) {
                aMap = map;
                map.setOnMapClickListener(new DJIMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(DJILatLng latLng) {
                        onViewClick(mapWidget);
                    }
                });
                map.getUiSettings().setZoomControlsEnabled(false);
            }
        });
        mapWidget.onCreate(savedInstanceState);
        mapWidget.setFlightPathVisible(true);
        mapWidget.setFlightPathColor(getResources().getColor(R.color.colorTheme));
        mapWidget.setDirectionToHomeVisible(false);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        deviceHeight = outPoint.y;
        deviceWidth = outPoint.x;
    }

    private void onViewClick(View view) {
        if (view == mTextureView && !isMapMini) {
            resizeFPVWidget(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, deviceWidth, deviceHeight, DisplayUtil.dp2px(this, 125), DisplayUtil.dp2px(this, 76), 0);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = true;
        } else if (view == mapWidget && isMapMini) {
            resizeFPVWidget(DisplayUtil.dp2px(this, 125), DisplayUtil.dp2px(this, 76), 0, 2);
            ResizeAnimation mapViewAnimation = new ResizeAnimation(mapWidget, DisplayUtil.dp2px(this, 125), DisplayUtil.dp2px(this, 76), deviceWidth, deviceHeight, 0);
            mapWidget.startAnimation(mapViewAnimation);
            isMapMini = false;

        }
    }

    private void resizeFPVWidget(int width, int height, int margin, int fpvInsertPosition) {
        RelativeLayout.LayoutParams fpvParams = (RelativeLayout.LayoutParams) layout_previewer_container.getLayoutParams();
        fpvParams.height = height;
        fpvParams.width = width;
        fpvParams.leftMargin = margin;
        fpvParams.bottomMargin = margin;
        if (isMapMini) {
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, 10);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        } else {
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 10);
            fpvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 10);
            fpvParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        layout_previewer_container.setLayoutParams(fpvParams);

        parentView.removeView(layout_previewer_container);
        parentView.addView(layout_previewer_container, fpvInsertPosition);
    }

    private class ResizeAnimation extends Animation {

        private View mView;
        private int mToHeight;
        private int mFromHeight;

        private int mToWidth;
        private int mFromWidth;
        private int mMargin;

        private ResizeAnimation(View v, int fromWidth, int fromHeight, int toWidth, int toHeight, int margin) {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            mView = v;
            mMargin = margin;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            p.rightMargin = mMargin;
            p.bottomMargin = mMargin;
            mView.requestLayout();
        }

    }


    private void initViews() {
        tvFirmwareVersion = findViewById(R.id.tv_firmware_version);
        tvBitRate = findViewById(R.id.tv_bitRate);
        tvFPS = findViewById(R.id.tv_Fps);
        tvStreamUrl = findViewById(R.id.tv_stream_url);

        btn_login = findViewById(R.id.btn_login);
        btn_startlive = findViewById(R.id.btn_start_live);
        btn_startlive.setOnClickListener(onClickListener);
        btn_login.setOnClickListener(onClickListener);
        mTextureView = findViewById(R.id.video_previewer_surface);
        modifiedVideoStreamPreview = findViewById(R.id.modified_videostream_preview);
        btnUp = findViewById(R.id.btn_up);
        btnDown = findViewById(R.id.btn_down);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnForward = findViewById(R.id.btn_forward);
        btnBackward = findViewById(R.id.btn_backward);
        tvZoomPlus = findViewById(R.id.tv_zoom_plus);
        tvZoomReduce = findViewById(R.id.tv_zoom_reduce);
        tvZoomPlus.setOnClickListener(onClickListener);
        tvZoomReduce.setOnClickListener(onClickListener);
        tvZoomNum = findViewById(R.id.tv_zoom_num);
        layout_previewer_container = findViewById(R.id.layout_previewer_container);
        parentView = (ViewGroup) findViewById(R.id.root_view);
        tvTest = (TextView) findViewById(R.id.tv_test);
        uavSettingView = (UavSettingView) findViewById(R.id.uav_setting);
        uavPaletteView = (UavPalette) findViewById(R.id.uav_palette);
        tvPalette = (TextView) findViewById(R.id.tv_palette);
        uavSettingView.setContex(this);
        tvTest.setOnClickListener(onClickListener);
        tvPalette.setOnClickListener(onClickListener);
        mTextureView.setOnClickListener(onClickListener);
        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(this);
        }
        mTextureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewClick(mTextureView);
            }
        });

        int height = QMUIDisplayHelper.getScreenHeight(this);
        int width = QMUIDisplayHelper.getScreenWidth(this);
        ViewGroup.LayoutParams params = uavSettingView.getLayoutParams();
        params.width = (int) (width * 0.6);
        params.height = (int) (height);
        uavSettingView.setLayoutParams(params);

    }

    int zoomNum;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    AccountManager.getInstance().loginAccount(FlightActivity.this);
                    break;
                case R.id.btn_start_live:
                    StreamManager.getInstance().initStreamManager();
                    StreamManager.getInstance().startLiveShow(null, null);
                    break;
                case R.id.tv_zoom_plus:
                    CameraManager.getInstance().setCameraZoom(LocalSource.getInstance().getHybridZoom() + 2);
                    break;
                case R.id.tv_zoom_reduce:
                    CameraManager.getInstance().setCameraZoom(LocalSource.getInstance().getHybridZoom() - 1);
                    break;
                case R.id.tv_test:
                    uavSettingView.Toggle();
                    break;
                case R.id.tv_palette:
                    uavPaletteView.Toggle();
                    break;
                case R.id.video_previewer_surface:
                    if (uavSettingView.Toggle() == true) {
                        uavSettingView.Toggle();
                    }
                    if (uavPaletteView.Toggle() == true) {
                        uavPaletteView.Toggle();
                    }
                    break;
            }
        }
    };
    int time = 0;
    boolean connectStatus;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant.FLAG_CONNECT:
                initDJIManager();
                connectStatus = true;
                break;
            case Constant.FLAG_DISCONNECT:
                Logger.e("断开连接---");
//                connectStatus = false;
//                Handler mHandler = new Handler();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (connectStatus == false) {
//                            if (time == 10) {
//                                SystemManager.getInstance().restartApp();
//                            } else {
//                                time = time + 1;
//                                try {
//                                    mHandler.postDelayed(this, 1000);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//
//                }, 1000);

                break;
            case Constant.VISUAL_ANGLE_TYPE:
                changeFPVOrGimbalView();
                break;
            case Constant.FLAG_START_DETECT_ARUCO:

                break;
            case Constant.FLAG_DOWN_LAND:

                break;
            case Constant.FLAG_STREAM_URL:
                tvStreamUrl.setText("rtmpAddr: " + DataCache.getInstance().getRtmp_address());
                break;
            case Constant.FLAG_ZOOM_FOCAL_LENGTH:
                if (LocalSource.getInstance().getHybridZoom() < 2) {
                    tvZoomNum.setText(2 + "x");
                } else {
                    tvZoomNum.setText(LocalSource.getInstance().getHybridZoom() + "x");
                }
                break;
        }
    }

    private int getSmallZoomValue(int bigZoomFromDJ) {
        int smallZoom = (bigZoomFromDJ - 317) / ((47549 - 317) / 199 + 2);
        return smallZoom;
    }

    //更新当前连接信息
    private void refreshSDKRelativeUI() {
        aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null && aircraft.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(aircraft.getFirmwarePackageVersion())) {
                        tvFirmwareVersion.setText("固件版本: N/A");
                    } else {
                        tvFirmwareVersion.setText("固件版本: " + aircraft.getFirmwarePackageVersion());
                    }
                }
            });
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (DJISDKManager.getInstance().getLiveStreamManager() != null) {
                        tvFPS.setText("fps: " + DJISDKManager.getInstance().getLiveStreamManager().getLiveVideoFps());
                        tvBitRate.setText("bitRate: " + DJISDKManager.getInstance().getLiveStreamManager().getLiveVideoBitRate() + "kpbs");
                    }
                    try {
                        mHandler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
    }

    private void initDJIManager() {
        aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null && aircraft.isConnected()) {
            //图传
            initPreviewer();
            //飞控参数、RTK开关、避障等
            initFlightController();
            //遥控器、图传等
            initAirLink();
            //图传链路
            initOcuSyncLink();
            //避障、感知
            initAssistant();
            //电池信息
            initBattery();
            //云台
            initGimbal();
            //相机
            initCamera();
            //RTK
            initRTK();
            //警告信息
            initDiagnosticsInfomation();
            //推流
            initLiveStreamManager();
            //航线任务
            initMission();
            Button viewById = findViewById(R.id.btn_sl);

        } else {
            showToast("aircraft disconnect");
        }
    }

    private void initMission() {
        MissionManager.getInstance().initMissionInfo(mqttAndroidClient);
    }

    private void initCamera() {
        CameraManager.getInstance().initCameraInfo();
    }

    private void initAssistant() {
        AssistantManager.getInstance().initAssistant();
    }

    private void initRTK() {
        RTKManager.getInstance().initRTKInfo(mqttAndroidClient);
    }

    private void initGimbal() {
        GimbalManager.getInstance().initGimbalInfo(mqttAndroidClient);
    }

    private void initDiagnosticsInfomation() {
        DiagnosticsManager.getInstance().initDiagnosticsInfo(mqttAndroidClient);
    }

    private void initBattery() {
        BatteryManager.getInstance().initBatteryInfo(mqttAndroidClient);
    }

    private void initAirLink() {
        AirLinkManager.getInstance().initLinkInfo(mqttAndroidClient);
    }

    private void initOcuSyncLink() {
        AirLinkManager.getInstance().initOcuSyncLink();
    }

    private void initFlightController() {
        FlightManager.getInstance().initFlightInfo(this, mqttAndroidClient);
    }

    private void initLiveStreamManager() {
        StreamManager.getInstance().initStreamManager();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppStarted = false;
//        try {
//            publish(MqttConfig.MQTT_REGISTER_TOPIC);

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        offLine();
    }

    private void offLine() {
        if (!TextUtils.isEmpty(ApronApp.SERIAL_NUMBER)) {
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.createRequest2().mqttOffline(PreferenceUtils.getInstance().getUserToken(), ApronApp.SERIAL_NUMBER).enqueue(new Callback<MqttLoginOutResult>() {
                @Override
                public void onResponse(Call<MqttLoginOutResult> call, Response<MqttLoginOutResult> response) {
                    if (response.body() != null) {
                        switch (response.body().getCode()) {
                            case "200":
                                Logger.e("飞手已下线" + "----");
                                break;
                            default:
                                Logger.e("飞手下线失败" + response.body().getMsg());
                                break;
                        }
                    } else {
                        Logger.e("飞手下线失败" + "网络异常");
                    }
                }

                @Override
                public void onFailure(Call<MqttLoginOutResult> call, Throwable t) {
                    Logger.e("飞手下线失败" + "网络异常"+ t.toString());
                }
            });
        }
    }
//    public void publish(String topic) throws MqttException {
//        if (isAlreadyConnected()) {
//            ProtoMessage.Message.Builder builder=ProtoMessage.Message.newBuilder();
//            builder.setMethod("offline").setRequestTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//            MqttMessage registerMessage = new MqttMessage(builder.build().toByteArray());
//            registerMessage.setQos(1);
//            mqttAndroidClient.publish(topic, registerMessage);
//        } else {
//            Logger.e("终止失败","种植失败");
//            XcFileLog.getInstace().e(TAG, "推送失败：MQtt未连接");
//        }
//    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(FlightActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isAlreadyConnected() {
        if (mqttAndroidClient != null) {
            try {
                boolean result = mqttAndroidClient.isConnected();
                if (result) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    private void initPreviewer() {
        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(this);
        }
        videoFeeder = VideoFeeder.getInstance();
        if (videoFeeder != null) {
            videoFeeder.getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
            videoFeeder.setTranscodingDataRate(8f);
        }
    }

    VideoFeeder.VideoDataListener mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {
        @Override
        public void onReceive(byte[] videoBuffer, int size) {
            if (codecManager != null) {
                codecManager.sendDataToDecoder(videoBuffer, size);
            }
        }
    };

    //改变云台视角
    public void changeFPVOrGimbalView() {
        if (codecManager != null) {
            DJICodecManager.VideoSource source = CAMERA;
            switch (LocalSource.getInstance().getCurrentVideoSource()) {
                case "1": //FPV视角
                    source = FPV;
                    break;
                case "0": //云台视角
                    source = CAMERA;
                    break;
            }
            codecManager.switchSource(source);
        } else {
            Toast.makeText(this, "切换视角失败,解码器初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (codecManager == null) {
            codecManager = new DJICodecManager(this, surface, width, height);
            //For M300RTK, you need to actively request an I frame.
//            codecManager.resetKeyFrame();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //Do nothing
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed");
        if (codecManager != null) {
            codecManager.cleanSurface();
            codecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    private void intiVirtualStick() {
        FlightController flightController = droneHelper.fetchFlightController();
        if (flightController != null) {
            droneHelper.setVerticalModeToVelocity();
            btnUp.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, 0f, 0f, 1f);
                }
            }, 200);
            btnDown.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, 0f, 0f, -1f);
                }
            }, 200);
            btnForward.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, 0.5f, 0f, 0f);
                }
            }, 200);
            btnBackward.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, -0.5f, 0f, 0f);
                }
            }, 200);

            btnLeft.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(-0.8f, 0f, 0f, 0f);
                }
            }, 200);

            btnRight.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0.8f, 0f, 0f, 0f);
                }
            }, 200);
        }

    }


}
