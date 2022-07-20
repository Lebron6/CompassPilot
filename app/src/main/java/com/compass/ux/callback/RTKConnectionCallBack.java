package com.compass.ux.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.apron.mobilesdk.state.ProtoRTKConnectionState;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import dji.common.flightcontroller.rtk.RTKBaseStationInformation;
import dji.common.flightcontroller.rtk.RTKConnectionStateWithBaseStationReferenceSource;
import dji.sdk.flightcontroller.RTK;

public class RTKConnectionCallBack extends BaseCallback implements RTK.RTKConnectionStateWithBaseStationReferenceSourceCallback{

    MqttAndroidClient client;
    public RTKConnectionCallBack(MqttAndroidClient client) {
        this.client=client;
    }

    @Override
    public void onUpdate(@NonNull RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource, @Nullable RTKBaseStationInformation rtkBaseStationInformation) {
        ProtoRTKConnectionState.RTKConnectionState.Builder builder=ProtoRTKConnectionState.RTKConnectionState.newBuilder()
                .setRtkConnectionStateWithBaseStationReferenceSource(ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource.values()[rtkConnectionStateWithBaseStationReferenceSource.ordinal()])
                .setBaseStationID(rtkBaseStationInformation.getBaseStationID()+"")
                .setBaseStationName(rtkBaseStationInformation.getBaseStationName())
                .setSignalLevel(rtkBaseStationInformation.getSignalLevel());
        if (isFlyClickTime()){
            MqttMessage message = new MqttMessage(builder.build().toByteArray());
            message.setQos(1);
            publish(client, MqttConfig.MQTT_RTK_CONNECTION_STATE_TOPIC, message);
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
