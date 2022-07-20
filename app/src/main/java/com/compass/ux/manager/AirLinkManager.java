package com.compass.ux.manager;

import android.text.TextUtils;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.DownlinkSignalQualityCallback;
import com.compass.ux.callback.UplinkSignalQualityCallback;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.entity.Communication;
import com.compass.ux.tools.Helper;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;

import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.airlink.PhysicalSource;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.airlink.AirLink;
import dji.sdk.airlink.OcuSyncLink;

/**
 * 链路
 */
public class AirLinkManager extends BaseManager {

    private AirLinkManager() {
    }

    private static class AirLinkManagerHolder {
        private static final AirLinkManager INSTANCE = new AirLinkManager();
    }

    public static AirLinkManager getInstance() {
        return AirLinkManager.AirLinkManagerHolder.INSTANCE;
    }

    public void initLinkInfo(MqttAndroidClient mqttAndroidClient) {
        AirLink airLink = ApronApp.getAircraftInstance().getAirLink();
        if (airLink != null) {
            airLink.setUplinkSignalQualityCallback(new UplinkSignalQualityCallback(mqttAndroidClient));
            airLink.setDownlinkSignalQualityCallback(new DownlinkSignalQualityCallback(mqttAndroidClient));
        }
    }

    //设置图传链路
    public void initOcuSyncLink() {
        AirLink airLink = ApronApp.getAircraftInstance().getAirLink();
        if (airLink != null) {
            if (airLink.isOcuSyncLinkSupported()) {
                OcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
                if (ocuSyncLink != null) {
                    ocuSyncLink.assignSourceToPrimaryChannel(PhysicalSource.LEFT_CAM, PhysicalSource.FPV_CAM, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                Logger.e("initOcuSyncLink:" + djiError.getDescription());
                            } else {
                                LocalSource.getInstance().setCurrentVideoSource(2 + "");//初始化默认云台视角
                            }
                        }
                    });
                    ocuSyncLink.getChannelBandwidth(new CommonCallbacks.CompletionCallbackWith<OcuSyncBandwidth>() {
                        @Override
                        public void onSuccess(OcuSyncBandwidth ocuSyncBandwidth) {
                            switch (ocuSyncBandwidth.value()) {
                                case 0:
                                    LocalSource.getInstance().setChannelBandwidth("20MHz");
                                    break;
                                case 1:
                                    LocalSource.getInstance().setChannelBandwidth("10MHz");
                                    break;
                                case 2:
                                    LocalSource.getInstance().setChannelBandwidth("40MHz");
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });

                    //工作频段 2.4g 5.8g 双频
                    ocuSyncLink.getFrequencyBand(new CommonCallbacks.CompletionCallbackWith<OcuSyncFrequencyBand>() {
                        @Override
                        public void onSuccess(OcuSyncFrequencyBand ocuSyncFrequencyBand) {
                            switch (ocuSyncFrequencyBand.getValue()) {
                                case 0:
                                    LocalSource.getInstance().setFrequencyBand("双频");
                                    break;
                                case 1:
                                    LocalSource.getInstance().setFrequencyBand("2.4G");
                                    break;
                                case 2:
                                    LocalSource.getInstance().setFrequencyBand("5.8G");
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                        }
                    });
                } else {
                    Logger.e("ocuSyncLink is null");
                }
            } else {
                Logger.e("aircraft not supported ocuSyncLink");

            }
        }
    }

    //设置工作频段
    public void setFrequencyBand(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isAirlinkAvailable()) {
            OcuSyncLink ocuSyncLink = ApronApp.getProductInstance().getAirLink().getOcuSyncLink();
            String type = message.getPara().get(Constant.TYPE);
            if (!TextUtils.isEmpty(type)) {
                if (ocuSyncLink != null) {
                    ocuSyncLink.setFrequencyBand(OcuSyncFrequencyBand.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                switch (type) {
                                    case "0":
                                        LocalSource.getInstance().setFrequencyBand("双频");
                                        break;
                                    case "1":
                                        LocalSource.getInstance().setFrequencyBand("2.4G");
                                        break;
                                    case "2":
                                        LocalSource.getInstance().setFrequencyBand("5.8G");
                                        break;
                                }
                                sendCorrectMsg2Server(mqttAndroidClient, message, "设置成功");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                            }
                        }
                    });
                }
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "product not supported");
        }
    }
}
