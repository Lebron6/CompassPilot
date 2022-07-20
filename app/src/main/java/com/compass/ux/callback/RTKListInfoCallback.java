package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoBaseStationList;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import dji.common.flightcontroller.rtk.RTKBaseStationInformation;
import dji.sdk.flightcontroller.RTK;

/**
 * RTK信号列表
 */
public class RTKListInfoCallback extends BaseCallback implements RTK.RTKBaseStationListCallback {
    MqttAndroidClient client;
    public RTKListInfoCallback(MqttAndroidClient client) {
        this.client=client;
    }

    @Override
    public void onUpdate(RTKBaseStationInformation[] rtkBaseStationInformations) {
        String submit = "";
        if (rtkBaseStationInformations.length > 0) {
            for (int i = 0; i < rtkBaseStationInformations.length; i++) {
                submit += rtkBaseStationInformations[i].getBaseStationName() + "-" + rtkBaseStationInformations[i].getBaseStationID() + ",";
            }
        }
        ProtoBaseStationList.RTKBaseStationList.Builder builder=ProtoBaseStationList.RTKBaseStationList.newBuilder()
                .setRtkBaseStationInformations(submit);
        if (isFlyClickTime()){
            MqttMessage rtkMessage = new MqttMessage(builder.build().toByteArray());
            rtkMessage.setQos(1);
            publish(client, MqttConfig.MQTT_RTK_LIST_INFO_TOPIC, rtkMessage);
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
