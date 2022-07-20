package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoUplink;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import dji.common.airlink.SignalQualityCallback;

/**
 * 遥控器信号
 */
public class UplinkSignalQualityCallback extends BaseCallback implements SignalQualityCallback {

    MqttAndroidClient mqttAndroidClient;

    public UplinkSignalQualityCallback(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    public void onUpdate(int i) {
        ProtoUplink.Uplink.Builder builder = ProtoUplink.Uplink.newBuilder();
        if (i <= 0) {
            builder.setQuality(0);
        } else if (i > 0 && i <= 20) {
            builder.setQuality(20);
        } else if (i > 20 && i <= 40) {
            builder.setQuality(2);
        } else if (i > 40 && i <= 60) {
            builder.setQuality(3);
        } else if (i > 60 && i <= 80) {
            builder.setQuality(4);
        } else if (i > 80 && i <= 100) {
            builder.setQuality(5);
        }
        if (isFlyClickTime()) {
            MqttMessage batteryMessage = new MqttMessage(builder.build().toByteArray());
            batteryMessage.setQos(1);
            publish(mqttAndroidClient, MqttConfig.MQTT_UPLINK_TOPIC, batteryMessage);
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
