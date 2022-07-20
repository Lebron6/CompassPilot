package com.compass.ux.base;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.xclog.XcFileLog;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseManager {

    public void sendErrorMsg2Server(MqttAndroidClient client, ProtoMessage.Message message, String msg) {

        if (client.isConnected()) {
            ProtoMessage.Message.Builder protoBuilder = ProtoMessage.Message.newBuilder();
            protoBuilder.setCode(-1).setEquipmentId(message.getEquipmentId())
                    .setRequestId(message.getRequestId())
                    .setMethod(message.getMethod()).setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))
                    .setResult(msg);
            MqttMessage mqttMessage = new MqttMessage(protoBuilder.build().toByteArray());
            mqttMessage.setQos(1);
            try {
                client.publish(MqttConfig.MQTT_FLIGHT_CONTROLLER_REPLY_TOPIC, mqttMessage);
            } catch (MqttException e) {
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "回复失败：MQtt连接异常" + e.toString());
                e.printStackTrace();
            }
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), message.getMethod() + "调用失败" + msg);
        } else {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "回复失败：MQtt未连接");
        }
    }

    public void sendCorrectMsg2Server(MqttAndroidClient client, ProtoMessage.Message message, String msg) {
        if (client.isConnected()) {
            ProtoMessage.Message.Builder protoBuilder = ProtoMessage.Message.newBuilder();
            protoBuilder.setCode(200).setEquipmentId(message.getEquipmentId())
                    .setRequestId(message.getRequestId())
                    .setMethod(message.getMethod()).setResponseTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()))
                    .setResult(msg);
            MqttMessage mqttMessage = new MqttMessage(protoBuilder.build().toByteArray());
            mqttMessage.setQos(1);
            try {
                client.publish(MqttConfig.MQTT_FLIGHT_CONTROLLER_REPLY_TOPIC, mqttMessage);
            } catch (MqttException e) {
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "回复失败：MQtt连接异常" + e.toString());
                e.printStackTrace();
            }
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), message.getMethod() + "调用成功" + msg);
        } else {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "回复失败：MQtt未连接");
        }
    }

}
