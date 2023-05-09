package com.compass.ux.base;

import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class BaseCallback {
    public void publish(MqttAndroidClient client, String topic, MqttMessage message) {

        if (isAlreadyConnected(client)) {

            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:MQtt为空");
        }

    }

    public boolean isAlreadyConnected(MqttAndroidClient client) {
        if (client != null) {
            try {
                boolean result = client.isConnected();
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
}
