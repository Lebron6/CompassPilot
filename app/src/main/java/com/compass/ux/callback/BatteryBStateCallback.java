package com.compass.ux.callback;


import com.apron.mobilesdk.state.ProtoBattery;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.LocalSource;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.battery.Battery;

/**
 * 电池B
 */
public class BatteryBStateCallback extends BaseCallback implements BatteryState.Callback{
    Battery battery;
    MqttAndroidClient client;

    public BatteryBStateCallback(Battery battery, MqttAndroidClient client) {
        this.battery = battery;
        this.client = client;
    }

    @Override
    public void onUpdate(BatteryState state) {
        ProtoBattery.Battery.Builder builder = ProtoBattery.Battery.newBuilder();
        builder.setVoltage(state.getVoltage());
        builder.setNumberOfDischarges(state.getNumberOfDischarges());
        builder.setTemperature(state.getTemperature());
        builder.setLifetimeRemaining(state.getLifetimeRemaining());
        builder.setChargeRemainingInPercent(state.getChargeRemainingInPercent());
        if (state.getConnectionState()!=null){
            builder.setConnectionState(ProtoBattery.Battery.ConnectionState.values()[state.getConnectionState().ordinal()]);
        }
        battery.getCellVoltages(new CommonCallbacks.CompletionCallbackWith<Integer[]>() {
            @Override
            public void onSuccess(Integer[] integers) {
                builder.setCellVoltage(Arrays.toString(integers));
                if (isFlyClickTime()){
                    MqttMessage batteryMessage = new MqttMessage(builder.build().toByteArray());
                    batteryMessage.setQos(1);
                    Logger.e("当前主题,"+MqttConfig.MQTT_BATTERY_B_TOPIC);
                    publish(client, MqttConfig.MQTT_BATTERY_B_TOPIC, batteryMessage);
                }
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });

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
