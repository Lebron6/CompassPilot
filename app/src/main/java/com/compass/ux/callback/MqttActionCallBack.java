package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoFlightController;
import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.Communication;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.xclog.XcFileLog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import dji.sdk.flightcontroller.FlightController;

public class MqttActionCallBack implements IMqttActionListener {

    private final String TAG = "MqttActionCallBack";
    private MqttAndroidClient mqttAndroidClient;

    public MqttActionCallBack(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        ToastUtil.showToast("MQtt连接成功,飞手已上线");
        Logger.e("MQtt连接成功:" + asyncActionToken.toString());
        XcFileLog.getInstace().i(TAG, "MQtt连接成功：-------");
//        if (Helper.isFlightControllerAvailable()) {
//            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
//            flightController.getSerialNumber(new SerialNumberCallBack(mqttAndroidClient));//获取SN码
//        }
        try {
            mqttAndroidClient.subscribe(MqttConfig.MQTT_REGISTER_REPLY_TOPIC, 1);//订阅主题:注册
            mqttAndroidClient.subscribe(MqttConfig.MQTT_FLIGHT_CONTROLLER_TOPIC, 1);//订阅主题:飞控            publish(topic,"注册",0);
            publish(MqttConfig.MQTT_REGISTER_TOPIC);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void publish(String topic) throws MqttException {
        if (mqttAndroidClient.isConnected()) {
            Logger.e("下线成功","下线成功");
            ProtoMessage.Message.Builder builder=ProtoMessage.Message.newBuilder();
            builder.setMethod("offline").setRequestTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            MqttMessage registerMessage = new MqttMessage(builder.build().toByteArray());
            registerMessage.setQos(1);
            mqttAndroidClient.publish(topic, registerMessage);
        } else {
            XcFileLog.getInstace().e(TAG, "推送失败：MQtt未连接");
        }
    }
    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Logger.e("MQtt连接失败:" + exception.toString());
        XcFileLog.getInstace().i(TAG, "MQtt连接失败:" + exception.toString());
    }
}
