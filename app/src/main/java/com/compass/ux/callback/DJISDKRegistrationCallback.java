package com.compass.ux.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.compass.ux.entity.MessageEvent;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

import static com.compass.ux.constant.Constant.FLAG_CONNECT;
import static com.compass.ux.constant.Constant.FLAG_DISCONNECT;

public class DJISDKRegistrationCallback implements DJISDKManager.SDKManagerCallback {

    private Context context;
    private String TAG = "DJISDKRegistrationCallback";

    public DJISDKRegistrationCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onRegister(DJIError djiError) {
        if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
            Logger.e("Register sdk success");
            DJISDKManager.getInstance().startConnectionToProduct();
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Register sdk fails, check network is available", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onProductDisconnect() {
        Logger.e(TAG+":onProductDisconnect");
        EventBus.getDefault().post(FLAG_DISCONNECT);
    }

    @Override
    public void onProductConnect(BaseProduct baseProduct) {
        Logger.e(TAG+":onProductConnect");
        EventBus.getDefault().post(FLAG_CONNECT);
    }

    @Override
    public void onProductChanged(BaseProduct baseProduct) {
        Logger.e(TAG+":onProductChanged");

    }

    @Override
    public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent oldComponent,
                                  BaseComponent newComponent) {
        Logger.e("onComponentChange:" + String.format("组件变化 键:%s,旧组件:%s,"
                + "新组件:%s", componentKey, oldComponent, newComponent
        ));
        if (newComponent != null) {
            newComponent.setComponentListener(new BaseComponent.ComponentListener() {
                @Override
                public void onConnectivityChange(boolean isConnected) {
                    EventBus.getDefault().post(FLAG_CONNECT);
                }
            });
        }
        EventBus.getDefault().post(FLAG_CONNECT);
    }

    @Override
    public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {
        Logger.e(TAG+":DJIMSDK init process:" + i);
    }

    @Override
    public void onDatabaseDownloadProgress(long l, long l1) {

    }
}
