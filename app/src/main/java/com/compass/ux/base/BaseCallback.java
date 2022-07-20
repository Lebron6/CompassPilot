package com.compass.ux.base;

import com.compass.ux.xclog.XcFileLog;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class BaseCallback {
    public void publish(MqttAndroidClient client, String topic, MqttMessage message) {

        if (client.isConnected()) {
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:" + topic + e.toString());
            }
        } else {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:MQtt未连接");
        }

    }
}
