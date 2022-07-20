package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;

/**
 * 获取设备SN码
 */
public class SerialNumberCallBack implements CommonCallbacks.CompletionCallbackWith<String>{
    private final String TAG = "SerialNumberCallBack";

    MqttAndroidClient mqttAndroidClient;
    public SerialNumberCallBack(MqttAndroidClient mqttAndroidClient) {
         this.mqttAndroidClient=mqttAndroidClient;
    }

    @Override
    public void onSuccess(String o) {
        MqttConfig.SERIAL_NUMBER=o;
        try {
            mqttAndroidClient.subscribe(MqttConfig.MQTT_REGISTER_REPLY_TOPIC, 1);//订阅主题:注册
            mqttAndroidClient.subscribe(MqttConfig.MQTT_FLIGHT_CONTROLLER_TOPIC, 1);//订阅主题:飞控            publish(topic,"注册",0);
            publish(MqttConfig.MQTT_REGISTER_TOPIC);
        } catch (MqttException e) {
            Logger.e("订阅异常" + e.toString());
            e.printStackTrace();
        }
    }


    @Override
    public void onFailure(DJIError djiError) {
        XcFileLog.getInstace().e(TAG, "获取SN码失败："+djiError.getDescription());
    }

    public void publish(String topic) throws MqttException {
        if (mqttAndroidClient.isConnected()) {
            ProtoMessage.Message.Builder builder=ProtoMessage.Message.newBuilder();
            builder.setMethod("online").setRequestTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            MqttMessage registerMessage = new MqttMessage(builder.build().toByteArray());
            registerMessage.setQos(1);
            mqttAndroidClient.publish(topic, registerMessage);
        } else {
            XcFileLog.getInstace().e(TAG, "推送失败：MQtt未连接");
        }
    }
}
