package com.compass.ux.manager;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.BatteryAStateCallback;
import com.compass.ux.callback.BatteryBStateCallback;
import com.compass.ux.callback.DiagnosticsInfoCallback;
import com.compass.ux.xclog.XcFileLog;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.List;

import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;

/**
 * 警报
 */
public class DiagnosticsManager extends BaseManager {

    private DiagnosticsManager() {
    }

    private static class DiagnosticsManagerHolder {
        private static final DiagnosticsManager INSTANCE = new DiagnosticsManager();
    }

    public static DiagnosticsManager getInstance() {
        return DiagnosticsManager.DiagnosticsManagerHolder.INSTANCE;
    }

    public void initDiagnosticsInfo(MqttAndroidClient client) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft!=null&&aircraft.isConnected()){
            aircraft.setDiagnosticsInformationCallback(new DiagnosticsInfoCallback(client));
        }else{
            XcFileLog.getInstace().i(this.getClass().getSimpleName(),"initDiagnosticsInfo:飞机未连接");
        }
    }
}
