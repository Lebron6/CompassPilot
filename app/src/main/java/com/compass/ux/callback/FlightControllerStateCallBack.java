package com.compass.ux.callback;

import static dji.keysdk.FlightControllerKey.WIND_DIRECTION;
import static dji.keysdk.FlightControllerKey.WIND_SPEED;

import androidx.annotation.NonNull;

import com.apron.mobilesdk.state.ProtoFlightController;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.tools.LocationUtils;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
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
        builder.setFlightModeString(state.getFlightModeString());
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

        if (isFlyClickTime()) {
            MqttMessage flightMessage = new MqttMessage(builder.build().toByteArray());
            flightMessage.setQos(1);
            publish(mqttAndroidClient, MqttConfig.MQTT_FLIGHT_STATE_TOPIC, flightMessage);
        }
    }

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
