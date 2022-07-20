package com.compass.ux.callback;

import com.compass.ux.entity.LocalSource;
import com.orhanobut.logger.Logger;

import dji.sdk.sdkmanager.LiveStreamManager;

public class LiveShowStatusCallback implements LiveStreamManager.OnLiveErrorStatusListener,LiveStreamManager.OnLiveChangeListener{
    @Override
    public void onStatusChanged(int i) {
//        LocalSource.getInstance().setLiveStatus(i==0?1:0);
        Logger.e("LiveShowStatusCallback:"+i);
//        Movement.getInstance().setLiveStatus(i);
    }

    @Override
    public void onError(int i, String s) {
//        LocalSource.getInstance().setLiveStatus(i==0?1:0);
        Logger.e("OnLiveErrorStatusListener:"+i+s);
    }
}
