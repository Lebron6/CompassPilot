package com.compass.ux.callback;

import android.text.TextUtils;
import com.apron.mobilesdk.state.ProtoDiagnostics;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.List;
import dji.sdk.base.DJIDiagnostics;

/**
 * 推送警告信息
 */
public class DiagnosticsInfoCallback extends BaseCallback implements DJIDiagnostics.DiagnosticsInformationCallback {
    MqttAndroidClient client;
    public DiagnosticsInfoCallback(MqttAndroidClient client) {
        this.client=client;
    }

    @Override
    public void onUpdate(List<DJIDiagnostics> list) {
        if (list != null && !list.isEmpty()) {
            String errorMsg = "";
            for (int i = 0; i < list.size(); i++) {
                if (!TextUtils.isEmpty(list.get(i).getSolution().trim())) {
                    errorMsg += list.get(i).getSolution() + ",";
                }
            }
            if (!TextUtils.isEmpty(errorMsg)) {
                errorMsg = errorMsg.substring(0, errorMsg.length() - 1);
                if (TextUtils.equals(errorMsg, "解决方案：插入SD卡") || TextUtils.equals(errorMsg, "解决方案:插入SD卡")
                        || TextUtils.equals(errorMsg, "解决方法：请联系相机研发") || TextUtils.equals(errorMsg, "解决方法:请联系相机研发")) {
                } else {
                    ProtoDiagnostics.Diagnostics diagnostics = ProtoDiagnostics.Diagnostics.newBuilder()
                            .setMsg(errorMsg).build();
                    if (isFlyClickTime()) {
                        MqttMessage diagnosticsMessage = new MqttMessage(diagnostics.toByteArray());
                        diagnosticsMessage.setQos(1);
                        publish(client, MqttConfig.MQTT_DIAGNOSTICS_TOPIC, diagnosticsMessage);
                    }
                }
            }
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
