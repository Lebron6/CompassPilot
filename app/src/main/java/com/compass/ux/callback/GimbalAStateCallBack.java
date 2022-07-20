package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoBattery;
import com.apron.mobilesdk.state.ProtoGimbal;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.LocalSource;

import androidx.annotation.NonNull;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import dji.common.gimbal.GimbalState;

/**
 * 云台状态
 */
public class GimbalAStateCallBack extends BaseCallback implements GimbalState.Callback{
    MqttAndroidClient client;
    public GimbalAStateCallBack(MqttAndroidClient client) {
        this.client=client;
    }

    @Override
    public void onUpdate(@NonNull GimbalState gimbalState) {
        ProtoGimbal.Gimbal.Builder builder = ProtoGimbal.Gimbal.newBuilder()
                .setGimBalMode(ProtoGimbal.Gimbal.GimbalMode.values()[gimbalState.getMode().ordinal()])
                .setPitch(gimbalState.getAttitudeInDegrees().getPitch())
                .setRoll(gimbalState.getAttitudeInDegrees().getRoll())
                .setYaw(gimbalState.getAttitudeInDegrees().getYaw());

        if (isFlyClickTime()){
            MqttMessage gimbalMessage = new MqttMessage(builder.build().toByteArray());
            gimbalMessage.setQos(1);
            publish(client, MqttConfig.MQTT_GIMBAL_A_TOPIC, gimbalMessage);
        }
    }
    private static long lastTime;
    public boolean isFlyClickTime() {
        long time = System.currentTimeMillis();
        if (time - lastTime > 1000) {
            lastTime = time;
            return true;
        }
        return false;

    }
}
