package com.compass.ux.manager;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.BatteryAStateCallback;
import com.compass.ux.callback.BatteryBStateCallback;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.List;

import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;

/**
 * 电池
 */
public class BatteryManager extends BaseManager {
    private BatteryManager() {
    }

    private static class BatteryManagerHolder {
        private static final BatteryManager INSTANCE = new BatteryManager();
    }

    public static BatteryManager getInstance() {
        return BatteryManager.BatteryManagerHolder.INSTANCE;
    }

    public void initBatteryInfo(MqttAndroidClient client) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft!=null&&aircraft.isConnected()){
            List<Battery> batteries = aircraft.getBatteries();
            if (batteries != null && batteries.size() > 0) {
                Battery batteryA = batteries.get(0);
                batteryA.setStateCallback(new BatteryAStateCallback(batteryA,client));
                if (batteries.size() > 1) {
                    Battery batteryB = batteries.get(1);
                    batteryB.setStateCallback(new BatteryBStateCallback(batteryB,client));
                }
            } else {
                XcFileLog.getInstace().i(this.getClass().getSimpleName(),"initBatteryInfo:batteries is null");
            }
        }else{
            XcFileLog.getInstace().i(this.getClass().getSimpleName(),"initBatteryInfo:飞机未连接");
        }
    }
}
