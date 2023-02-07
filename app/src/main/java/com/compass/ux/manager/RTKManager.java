package com.compass.ux.manager;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.RTKConnectionCallBack;
import com.compass.ux.callback.RTKListInfoCallback;
import com.compass.ux.callback.RTKStateCallback;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.Communication;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.tools.Helper;
import com.compass.ux.xclog.XcFileLog;

import org.eclipse.paho.android.service.MqttAndroidClient;

import dji.common.error.DJIError;
import dji.common.flightcontroller.rtk.NetworkServiceSettings;
import dji.common.flightcontroller.rtk.ReferenceStationSource;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.flightcontroller.RTK;
import dji.sdk.network.RTKNetworkServiceProvider;
import dji.sdk.sdkmanager.DJISDKManager;

/**
 * RTK高精度定位
 */
public class RTKManager extends BaseManager {

    private RTKManager() {
    }

    private static class RTKHolder {
        private static final RTKManager INSTANCE = new RTKManager();
    }

    public static RTKManager getInstance() {
        return RTKManager.RTKHolder.INSTANCE;
    }

    public void initRTKInfo(MqttAndroidClient client){
      FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
        if (Helper.isRtkAvailable()) {
            RTK rtk = flightController.getRTK();
            if (rtk != null) {
                //RTK开关
                rtk.getRtkEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setRtkSwitch(aBoolean?1:0);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //检测RTK状态保持是否开启
                rtk.getRTKMaintainPositioningAccuracyModeEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setRtkMaintainPositioningAccuracy(aBoolean?1:0);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                rtk.setStateCallback(new RTKStateCallback(client));
                rtk.setRtkBaseStationListCallback(new RTKListInfoCallback(client));
                rtk.setRtkConnectionStateWithBaseStationCallback(new RTKConnectionCallBack(client));
            }
        }else{
            XcFileLog.getInstace().i(this.getClass().getSimpleName(),"initRTKInfo:product not supported");
        }
    }
    //设置rtk
    public void setRtkEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isRtkAvailable()) {
            RTK rtk = ApronApp.getAircraftInstance().getFlightController().getRTK();
            rtk.setRtkEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        LocalSource.getInstance().setRtkSwitch(type.equals("1")?1:0);
                        sendCorrectMsg2Server(mqttAndroidClient,message, "RTK状态已更新");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //开始搜索基站
    public void startSearchBaseStation(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isRtkAvailable()) {
            RTK rtk = ApronApp.getAircraftInstance().getFlightController().getRTK();
            rtk.startSearchBaseStation(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "开始搜索基站");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //结束搜索基站
    public void stopSearchBaseStation(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isRtkAvailable()) {
            RTK rtk = ApronApp.getAircraftInstance().getFlightController().getRTK();
            rtk.stopSearchBaseStation(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    rtk.stopSearchBaseStation(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                sendCorrectMsg2Server(mqttAndroidClient,message, "停止搜索基站");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    });
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //连接基站
    public void connectToBaseStation(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isRtkAvailable()) {
            RTK rtk = ApronApp.getAircraftInstance().getFlightController().getRTK();
            long type = Long.parseLong(message.getPara().get(Constant.TYPE));
            rtk.connectToBaseStation(type, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "设置基站类型成功");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }


    //设置信息源
    public void setReferenceStationSource(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isRtkAvailable()) {
            RTK rtk = ApronApp.getAircraftInstance().getFlightController().getRTK();
            String type = message.getPara().get(Constant.TYPE);
            rtk.setReferenceStationSource(ReferenceStationSource.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "开始连接基站");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //连接网络RTK
    public void startNetworkService(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isRtkAvailable()) {
            RTKNetworkServiceProvider provider = DJISDKManager.getInstance().getRTKNetworkServiceProvider();
            String username = message.getPara().get("username");
            String password = message.getPara().get("password");
            String mountPoint = message.getPara().get("mountPoint");
            String ip = message.getPara().get("ip");
            int port = Integer.parseInt(message.getPara().get("port"));

            NetworkServiceSettings.Builder builder = new NetworkServiceSettings.Builder()
                    .userName(username).password(password).ip(ip)
                    .mountPoint(mountPoint).port(port);
            provider.setCustomNetworkSettings(builder.build());

            //启动网络RTK
            provider.startNetworkService(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "网络RTK已连接");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //设置rtk状态保持
    public void setRTKMaintainPositioningAccuracyModeEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isRtkAvailable()) {
            RTK rtk = ApronApp.getAircraftInstance().getFlightController().getRTK();
            rtk.setRTKMaintainPositioningAccuracyModeEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        LocalSource.getInstance().setRtkMaintainPositioningAccuracy(type.equals("1")?1:0);
                        sendCorrectMsg2Server(mqttAndroidClient,message, "RTK状态保持已更新");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }
}
