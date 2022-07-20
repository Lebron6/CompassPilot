package com.compass.ux.manager;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.entity.Communication;
import com.compass.ux.tools.Helper;
import com.orhanobut.logger.Logger;

import dji.common.error.DJIError;
import dji.common.flightcontroller.flightassistant.PerceptionInformation;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;

import static dji.common.flightcontroller.flightassistant.PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Downward;
import static dji.common.flightcontroller.flightassistant.PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Horizontal;
import static dji.common.flightcontroller.flightassistant.PerceptionInformation.DJIFlightAssistantObstacleSensingDirection.Upward;

import org.eclipse.paho.android.service.MqttAndroidClient;

/**
 * 辅助
 */
public class AssistantManager extends BaseManager {

    private AssistantManager() {
    }

    private static class AssistantHolder {
        private static final AssistantManager INSTANCE = new AssistantManager();
    }

    public static AssistantManager getInstance() {
        return AssistantManager.AssistantHolder.INSTANCE;
    }

    public void initAssistant() {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant mFlightAssistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (mFlightAssistant != null) {
                mFlightAssistant.getUpwardVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setUpwardsAvoidance(aBoolean);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
                //向下避障
                mFlightAssistant.getLandingProtectionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setLandingProtection(aBoolean);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });

                //水平避障
                mFlightAssistant.getHorizontalVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setActiveObstacleAvoidance(aBoolean);

                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //精确着陆
                mFlightAssistant.getPrecisionLandingEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setPrecisionLand(aBoolean);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //视觉定位
                mFlightAssistant.getVisionAssistedPositioningEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setVisionAssistedPosition(aBoolean);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                mFlightAssistant.getVisualObstaclesAvoidanceDistance(Upward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        LocalSource.getInstance().setAvoidanceDistanceUpward(aFloat + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightAssistant.getVisualObstaclesAvoidanceDistance(Downward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        LocalSource.getInstance().setAvoidanceDistanceDownward(aFloat + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightAssistant.getVisualObstaclesAvoidanceDistance(Horizontal, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        LocalSource.getInstance().setAvoidanceDistanceHorizontal(aFloat + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                mFlightAssistant.getMaxPerceptionDistance(Upward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        LocalSource.getInstance().setMaxPerceptionDistanceUpward(aFloat + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                mFlightAssistant.getMaxPerceptionDistance(Downward, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        LocalSource.getInstance().setMaxPerceptionDistanceDownward(aFloat + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

                mFlightAssistant.getMaxPerceptionDistance(Horizontal, new CommonCallbacks.CompletionCallbackWith<Float>() {
                    @Override
                    public void onSuccess(Float aFloat) {
                        LocalSource.getInstance().setMaxPerceptionDistanceHorizontal(aFloat + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

            } else {
                Logger.e("initAssistant flightAssistant is null");
            }
        } else {
            Logger.e("initAssistant flightController is null");
        }
    }

    //设置视觉定位
    public void setVisionAssistedPositioningEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                assistant.setVisionAssistedPositioningEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "视觉定位设置已变更");
                            LocalSource.getInstance().setVisionAssistedPosition(type.equals("1") ? true : false);
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }

    //精确着陆
    public void setPrecisionLandingEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                assistant.setPrecisionLandingEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "精准定位设置已变更");
                            LocalSource.getInstance().setPrecisionLand(type.equals("1") ? true : false);
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }

    //向上刹停
    public void setUpwardVisionObstacleAvoidanceEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                assistant.setUpwardVisionObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "上避障状态已变更");
                            LocalSource.getInstance().setUpwardsAvoidance(type.equals("1") ? true : false);
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }

    //下避障
    public void setLandingProtectionEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                assistant.setLandingProtectionEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "下避障状态已变更");
                            LocalSource.getInstance().setLandingProtection(type.equals("1") ? true : false);
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }

    //设置避障刹车功能
    public void setHorizontalVisionObstacleAvoidanceEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                assistant.setHorizontalVisionObstacleAvoidanceEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "避障状态已变更");
                            LocalSource.getInstance().setActiveObstacleAvoidance(type.equals("1") ? true : false);
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }


    //设置上下感知距离
    public void setMaxPerceptionDistance(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                String value = message.getPara().get(Constant.VALUE);
                PerceptionInformation.DJIFlightAssistantObstacleSensingDirection direction = Downward;
                switch (type) {
                    case "0":
                        direction = Downward;
                        break;
                    case "1":
                        direction = Upward;
                        break;
                    case "2":
                        direction = Horizontal;
                        break;
                }
                assistant.setMaxPerceptionDistance(Float.parseFloat(value), direction, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            switch (type) {
                                case "0":
                                    LocalSource.getInstance().setMaxPerceptionDistanceDownward(value);
                                    break;
                                case "1":
                                    LocalSource.getInstance().setMaxPerceptionDistanceUpward(value);
                                    break;
                                case "2":
                                    LocalSource.getInstance().setMaxPerceptionDistanceHorizontal(value);
                                    break;
                            }
                            sendCorrectMsg2Server(mqttAndroidClient, message, "感知距离已更新");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }

    //设置上下安全距离
    public void setVisualObstaclesAvoidanceDistance(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                String type = message.getPara().get(Constant.TYPE);
                String value = message.getPara().get(Constant.VALUE);
                PerceptionInformation.DJIFlightAssistantObstacleSensingDirection direction = Downward;
                switch (type) {
                    case "0":
                        direction = Downward;
                        break;
                    case "1":
                        direction = Upward;
                        break;
                    case "2":
                        direction = Horizontal;
                        break;
                }
                assistant.setVisualObstaclesAvoidanceDistance(Float.parseFloat(value), direction, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            switch (type) {
                                case "0":
                                    LocalSource.getInstance().setAvoidanceDistanceDownward(value);
                                    break;
                                case "1":
                                    LocalSource.getInstance().setAvoidanceDistanceUpward(value);
                                    break;
                                case "2":
                                    LocalSource.getInstance().setAvoidanceDistanceHorizontal(value);
                                    break;
                            }
                            sendCorrectMsg2Server(mqttAndroidClient, message, "安全距离已更新");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightAssistant is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
        }

    }
}
