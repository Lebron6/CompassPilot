package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoBattery;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.battery.Battery;

/**
 * 电池A
 */
public class BatteryAStateCallback extends BaseCallback implements BatteryState.Callback {

    Battery battery;
    MqttAndroidClient client;

    public BatteryAStateCallback(Battery battery, MqttAndroidClient client) {
        this.battery = battery;
        this.client = client;
    }

    @Override
    public void onUpdate(BatteryState state) {
        ProtoBattery.Battery.Builder builder = ProtoBattery.Battery.newBuilder()
                .setVoltage(state.getVoltage())
                .setNumberOfDischarges(state.getNumberOfDischarges())
                .setTemperature(state.getTemperature())
                .setLifetimeRemaining(state.getLifetimeRemaining())
                .setChargeRemainingInPercent(state.getChargeRemainingInPercent());
        if (state.getConnectionState()!=null){
            builder .setConnectionState(ProtoBattery.Battery.ConnectionState.values()[state.getConnectionState().ordinal()]);

        }

        battery.getCellVoltages(new CommonCallbacks.CompletionCallbackWith<Integer[]>() {
            @Override
            public void onSuccess(Integer[] integers) {
                builder.setCellVoltage(integers.toString());
            }

            @Override
            public void onFailure(DJIError djiError) {

            }
        });
        if (isFlyClickTime()){
            MqttMessage batteryMessage = new MqttMessage(builder.build().toByteArray());
            batteryMessage.setQos(1);
            publish(client, MqttConfig.MQTT_BATTERY_A_TOPIC, batteryMessage);
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
