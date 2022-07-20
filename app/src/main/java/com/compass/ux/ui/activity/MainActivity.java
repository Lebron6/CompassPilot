package com.compass.ux.ui.activity;

import static dji.sdk.codec.DJICodecManager.VideoSource.CAMERA;
import static dji.sdk.codec.DJICodecManager.VideoSource.FPV;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.apron.mobilesdk.state.ProtoFlightController;
import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.LocalSource;
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
import com.compass.ux.ui.view.VideoFeedView;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.LiveStreamManager;

public class MainActivity extends BaseActivity {

    private static boolean isAppStarted = false;

    public static boolean isStarted() {
        return isAppStarted;
    }

    VideoFeeder videoFeeder;
    DJICodecManager codecManager;
    FlightController flightController;
    Aircraft aircraft;
    LiveStreamManager liveStreamManager;

    VideoFeedView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAppStarted = true;
        initViews();
        needConnect();
        initDJIManager();
    }

    public void doClick(View view) {

        ProtoFlightController.FlightController.Builder builder = ProtoFlightController.FlightController.newBuilder();
        //是否正在飞行
        builder.setIsFlying(false);
        //风速
        builder.setWindSpeed(5);
        builder.setWindDirection(ProtoFlightController.FlightController.WindDirection.values()[1]);
        builder.setGoHomeLength(1000.00);
        //电机状态
        builder.setAreMotorsOn(true);
        //飞行高度
        builder.setAltitude(100f);
        //飞机当前坐标
        builder.setLatitude(0.1);
        builder.setLongitude(0.1);
        //飞机原点位置相对于海平面的相对高度
        builder.setTakeoffLocationAltitude(0);
        //获取方向度数
        builder.setCompassHeading(10.1f);
        //返航点经纬度
        builder.setHomeLocationLatitude(0.0);
        builder.setHomeLocationLongitude(0.0);
        //返航高度
        builder.setGoHomeHeight(77);
        //GPS信号
        builder.setGPSsignalLevel(ProtoFlightController.FlightController.GPSSignalLevel.values()[1]);
        //飞机当前的定向模式。
        builder.setOrientationMode(ProtoFlightController.FlightController.OrientationMode.values()[1]);
        builder.setFlightWindWarning(ProtoFlightController.FlightController.FlightWindWarning.values()[2]);
        //根据剩余电池寿命建议采取的措施
        builder.setBatteryThresholdBehavior(ProtoFlightController.FlightController.BatteryThresholdBehavior.values()[1]);
        builder.setFlightModeString("888");
        builder.setYaw(3.3);
        builder.setRoll(3.3);
        builder.setPitch(3.3);
        builder.setVelocityX(2);
        builder.setVelocityY(3);
        builder.setVelocityZ(4);
        builder.setFlightTimeInSeconds(2);
        builder.setSatelliteCount(2);
        builder.setIsIMUPreheating(true);
        builder.setIsUltrasonicBeingUsed(true);
        builder.setUltrasonicHeightInMeters(3.3f);
        builder.setIsVisionPositioningSensorBeingUsed(true);
        builder.setIsLowerThanBatteryWarningThreshold(true);
        builder.setIsLowerThanSeriousBatteryWarningThreshold(true);
        builder.setFlightCount(4);
        builder.setGoHomeExecutionState(ProtoFlightController.FlightController.GoHomeExecutionState.values()[1]);
        MqttMessage flightMessage = new MqttMessage(builder.build().toByteArray());
        flightMessage.setQos(1);
        publish(mqttAndroidClient, MqttConfig.MQTT_FLIGHT_STATE_TOPIC, flightMessage);

    }


    public void publish(MqttAndroidClient client, String topic, MqttMessage message) {
        Logger.e(this.getClass().getSimpleName() + "开始推送:");

        if (client.isConnected()) {
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
                Logger.e(this.getClass().getSimpleName() + "推送失败:" + topic + e.toString());
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:" + topic + e.toString());
            }
        } else {
            Logger.e(this.getClass().getSimpleName(), "推送失败:MQtt未连接");
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:MQtt未连接");
        }

    }

    private void initViews() {
        mTextureView = findViewById(R.id.video_previewer_surface);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant
                    .FLAG_CONNECT:
                initDJIManager();
                break;
            case Constant.VISUAL_ANGLE_TYPE:
                changeFPVOrGimbalView();
                break;
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
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initPreviewer() {
        videoFeeder = VideoFeeder.getInstance();
        if (videoFeeder != null) {
            mTextureView.registerLiveVideo(videoFeeder.getPrimaryVideoFeed(), mqttAndroidClient);
            videoFeeder.setTranscodingDataRate(8f);
        }
    }

    //改变云台视角
    public void changeFPVOrGimbalView() {
        codecManager = mTextureView.getCodecManager();
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
}