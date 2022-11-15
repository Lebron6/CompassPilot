package com.compass.ux.callback;

import static com.compass.ux.constant.Constant.FLAG_DOWN_LAND;
import static com.compass.ux.constant.Constant.FLAG_START_DETECT_ARUCO;
import static dji.keysdk.FlightControllerKey.WIND_DIRECTION;
import static dji.keysdk.FlightControllerKey.WIND_SPEED;

import androidx.annotation.NonNull;

import com.apron.mobilesdk.state.ProtoDownlink;
import com.apron.mobilesdk.state.ProtoFlightController;
import com.apron.mobilesdk.state.ProtoMissionExecution;
import com.apron.mobilesdk.state.ProtoUplink;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.DataCache;
import com.compass.ux.tools.LocationUtils;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.GoHomeExecutionState;
import dji.common.flightcontroller.WindDirection;
import dji.keysdk.DiagnosticsKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.sdk.flightcontroller.Compass;

/**
 * 飞控
 */
public class FlightControllerStateCallBack extends BaseCallback implements FlightControllerState.Callback {
    private MqttAndroidClient mqttAndroidClient;
    DiagnosticsKey diagnosticsKey = DiagnosticsKey.create(DiagnosticsKey.SYSTEM_STATUS);

    public FlightControllerStateCallBack(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }


    @Override
    public void onUpdate(@NonNull FlightControllerState state) {

        ProtoFlightController.FlightController.Builder builder = ProtoFlightController.FlightController.newBuilder();
        //是否正在飞行
        builder.setIsFlying(state.isFlying());
        //风速
        Object windSpeed = KeyManager.getInstance().getValue((FlightControllerKey.create(WIND_SPEED)));
        if (windSpeed != null && windSpeed instanceof Integer) {
            builder.setWindSpeed((int) windSpeed);
        }
        //风向
        Object windDirection = KeyManager.getInstance().getValue((FlightControllerKey.create(WIND_DIRECTION)));
        if (windDirection != null && windDirection instanceof WindDirection) {
            builder.setWindDirection(ProtoFlightController.FlightController.WindDirection.values()[((WindDirection) windDirection).ordinal()]);
        }
        //返航距离
        double distance = LocationUtils.getDistance(state.getHomeLocation().getLongitude() + "",
                state.getHomeLocation().getLatitude() + ""
                , state.getAircraftLocation().getLongitude() + "",
                state.getAircraftLocation().getLatitude() + "");
        builder.setGoHomeLength(distance);
        //电机状态
        builder.setAreMotorsOn(state.areMotorsOn());
        //飞行高度
        float flyingHeight = state.getAircraftLocation().getAltitude();
        builder.setAltitude(flyingHeight);
        //飞机当前坐标
        if ((state.getAircraftLocation().getLatitude() + "").equals("NaN")) {
            builder.setLatitude(0.0);
        } else {
            builder.setLatitude(state.getAircraftLocation().getLatitude());
        }
        if ((state.getAircraftLocation().getLongitude() + "").equals("NaN")) {
            builder.setLongitude(0.0);
        } else {
            builder.setLongitude(state.getAircraftLocation().getLongitude());
        }
        //飞机原点位置相对于海平面的相对高度
        if ((state.getTakeoffLocationAltitude() + "").equals("NaN")) {
            builder.setTakeoffLocationAltitude(0);
        } else {
            builder.setTakeoffLocationAltitude(state.getTakeoffLocationAltitude());
        }
        //获取方向度数
        Compass compass = ApronApp.getAircraftInstance().getFlightController().getCompass();
        if (compass != null) {
            builder.setCompassHeading(compass.getHeading());
        }
        //返航点经纬度
        if ((state.getHomeLocation().getLatitude() + "").equals("NaN")) {
            builder.setHomeLocationLatitude(0.0);
        } else {
            builder.setHomeLocationLatitude(state.getHomeLocation().getLatitude());
        }
        if ((state.getHomeLocation().getLongitude() + "").equals("NaN")) {
            builder.setHomeLocationLongitude(0.0);
        } else {
            builder.setHomeLocationLongitude(state.getHomeLocation().getLongitude());
        }
        //返航高度
        builder.setGoHomeHeight(state.getGoHomeHeight());
        //GPS信号
        builder.setGPSsignalLevel(ProtoFlightController.FlightController.GPSSignalLevel.values()[state.getGPSSignalLevel().ordinal()]);
        //飞机当前的定向模式。
        builder.setOrientationMode(ProtoFlightController.FlightController.OrientationMode.values()[state.getOrientationMode().ordinal()]);
        builder.setFlightWindWarning(ProtoFlightController.FlightController.FlightWindWarning.values()[state.getFlightWindWarning().ordinal()]);
        //根据剩余电池寿命建议采取的措施
        builder.setBatteryThresholdBehavior(ProtoFlightController.FlightController.BatteryThresholdBehavior.values()[state.getBatteryThresholdBehavior().ordinal()]);
      if (state.getFlightModeString()!=null){
          builder.setFlightModeString(state.getFlightModeString());
      }
        builder.setYaw(state.getAttitude().yaw);
        builder.setRoll(state.getAttitude().roll);
        builder.setPitch(state.getAttitude().pitch);
        builder.setVelocityX(state.getVelocityX());
        builder.setVelocityY(state.getVelocityY());
        builder.setVelocityZ(state.getVelocityZ());
        builder.setFlightTimeInSeconds(state.getFlightTimeInSeconds());
        builder.setSatelliteCount(state.getSatelliteCount());
        builder.setIsIMUPreheating(state.isIMUPreheating());
        builder.setIsUltrasonicBeingUsed(state.isUltrasonicBeingUsed());
        builder.setUltrasonicHeightInMeters(state.getUltrasonicHeightInMeters());
        builder.setIsVisionPositioningSensorBeingUsed(state.isVisionPositioningSensorBeingUsed());
        builder.setIsLowerThanBatteryWarningThreshold(state.isLowerThanBatteryWarningThreshold());
        builder.setIsLowerThanSeriousBatteryWarningThreshold(state.isLowerThanSeriousBatteryWarningThreshold());
        builder.setFlightCount(state.getFlightCount());
        builder.setGoHomeExecutionState(ProtoFlightController.FlightController.GoHomeExecutionState.values()[state.getGoHomeExecutionState().ordinal()]);

        //飞机飞行状态
        KeyManager.getInstance().getValue(diagnosticsKey, new GetCallback() {
            @Override
            public void onSuccess(@NonNull Object o) {
                builder.setSystemStatus(o.toString());
            }

            @Override
            public void onFailure(@NonNull DJIError djiError) {
            }
        });
        ProtoMissionExecution.MissionExecution.Builder missionBuilder = ProtoMissionExecution.MissionExecution.newBuilder();
        missionBuilder.setTargetMissionWaypointSize(DataCache.getInstance().getTargetWaypointIndex());
        missionBuilder.setWaypointV2MissionExecuteState(
                ProtoMissionExecution.MissionExecution.WaypointV2MissionExecuteState.values()[DataCache.getInstance().getMissionExecuteState()]);
        missionBuilder.setTargetMissionWaypointSize(DataCache.getInstance().getMissionWaypointSize());

        ProtoDownlink.Downlink.Builder downLinkBuilder = ProtoDownlink.Downlink.newBuilder();
        downLinkBuilder.setQuality(DataCache.getInstance().getDownlinkQuality());

        ProtoUplink.Uplink.Builder upLinkBuilder = ProtoUplink.Uplink.newBuilder();
        upLinkBuilder.setQuality(DataCache.getInstance().getUplinkQuality());

        if (isFlyClickTime()) {
            //推送飞行状态
            MqttMessage flightMessage = new MqttMessage(builder.build().toByteArray());
            flightMessage.setQos(1);
            publish(mqttAndroidClient, MqttConfig.MQTT_FLIGHT_STATE_TOPIC, flightMessage);
            //推送航线状态
            MqttMessage missionInfo = new MqttMessage(missionBuilder.build().toByteArray());
            missionInfo.setQos(1);
            publish(mqttAndroidClient, MqttConfig.MQTT_MISSION_EXECUTE_STATE_TOPIC, missionInfo);
            //推送图传信号
            MqttMessage downLinkInfo = new MqttMessage(downLinkBuilder.build().toByteArray());
            downLinkInfo.setQos(1);
            publish(mqttAndroidClient, MqttConfig.MQTT_DOWNlINK_TOPIC, downLinkInfo);
            //推送遥控器信号
            MqttMessage upLinkInfo = new MqttMessage(upLinkBuilder.build().toByteArray());
            upLinkInfo.setQos(1);
            publish(mqttAndroidClient, MqttConfig.MQTT_UPLINK_TOPIC, upLinkInfo);
        }

        //当飞机处于降落的状态
//        if (state.getGoHomeExecutionState().equals(GoHomeExecutionState.GO_DOWN_TO_GROUND) && state.isFlying() == true) {
//            //开始视觉识别降落
//            //当前高度<20米并且>0.3米，并且没有发送开始视觉识别的时候,
//            // 发送一次开始视觉识别，将isSendDetect变成已发送的状态
//            if (flyingHeight < 10 && flyingHeight > 0.15 && isSendDetect == false) {
//                EventBus.getDefault().post(FLAG_START_DETECT_ARUCO);
//                isSendDetect = true;
//            }
//        }
        //当飞机距离返航点0.3米，停止视觉识别，直接降落
        // 将isSendDetect变成未发送的状态，以便下次起飞降落可以继续触发视觉识别
//        if (flyingHeight <= 0.15 && isSendDetect == true && state.isFlying() == true) {
//            EventBus.getDefault().post(FLAG_DOWN_LAND);
//            isSendDetect = false;
//        }
    }

    private boolean isSendDetect = false;

    private static long lastTime;

    private boolean isFlyClickTime() {
        long time = System.currentTimeMillis();
        if (time - lastTime > 1000) {
            lastTime = time;
            return true;
        }
        return false;
    }
}
