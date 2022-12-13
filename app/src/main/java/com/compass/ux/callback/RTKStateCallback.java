package com.compass.ux.callback;

import androidx.annotation.NonNull;
import com.apron.mobilesdk.state.ProtoRTKState;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import dji.common.flightcontroller.RTKState;

/**
 * 推送RTK状态
 */
public class RTKStateCallback extends BaseCallback implements RTKState.Callback {

    MqttAndroidClient client;

    public RTKStateCallback(MqttAndroidClient client) {
        this.client = client;
    }

    @Override
    public void onUpdate(@NonNull RTKState rtkState) {
        ProtoRTKState.RTKState.Builder builder=ProtoRTKState.RTKState.newBuilder().
                setAircraftAltitude(rtkState.getAircraftAltitude())
                .setBsAltitude(rtkState.getBaseStationAltitude())
                .setDistanceToHomePoint(rtkState.getDistanceToHomePoint())
                .setEllipsoidHeight(rtkState.getEllipsoidHeight())
                .setHeading(rtkState.getHeading())
                .setPositioningSolution(ProtoRTKState.RTKState.PositioningSolution.values()[rtkState.getHeadingSolution().ordinal()])
                .setIsHeadingValid(rtkState.isHeadingValid())
                .setIsRTKBeingUsed(rtkState.isRTKBeingUsed())
                .setTakeOffAltitude(rtkState.getTakeOffAltitude())
                .setSatelliteCount(rtkState.getSatelliteCount());
        if (isFlyClickTime()){
            MqttMessage rtkMessage = new MqttMessage(builder.build().toByteArray());
            rtkMessage.setQos(1);
            publish(client, MqttConfig.MQTT_RTK_STATE_TOPIC, rtkMessage);
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
