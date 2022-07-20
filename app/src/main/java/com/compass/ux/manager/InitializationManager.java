package com.compass.ux.manager;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.base.BaseManager;
import com.compass.ux.entity.Communication;
import com.compass.ux.entity.LocalSource;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;

/**
 * 账号
 */
public class InitializationManager extends BaseManager {

    private InitializationManager() {
    }

    private static class InitializationSettingsManagerHolder {
        private static final InitializationManager INSTANCE = new InitializationManager();
    }

    public static InitializationManager getInstance() {
        return InitializationManager.InitializationSettingsManagerHolder.INSTANCE;
    }

    public void sendInitializationSettings(MqttAndroidClient client, ProtoMessage.Message message){
        sendCorrectMsg2Server(client,message,new Gson().toJson(LocalSource.getInstance()));
    }
}
