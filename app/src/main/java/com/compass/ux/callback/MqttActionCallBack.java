package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoFlightController;
import com.apron.mobilesdk.state.ProtoMessage;
import com.apron.mobilesdk.state.ProtoRegister;
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
        ToastUtil.showToast("MQtt连接成功");
        Logger.e("MQtt连接成功:" + asyncActionToken.toString());
        XcFileLog.getInstace().i(TAG, "MQtt连接成功：-------");
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.getSerialNumber(new SerialNumberCallBack(mqttAndroidClient));//获取SN码
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Logger.e("MQtt连接失败:" + exception.toString());
        XcFileLog.getInstace().i(TAG, "MQtt连接失败:" + exception.toString());
    }
}
