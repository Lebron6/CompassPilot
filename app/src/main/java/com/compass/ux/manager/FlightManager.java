package com.compass.ux.manager;

import android.content.Context;
import android.text.TextUtils;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.FlightControllerStateCallBack;
import com.compass.ux.callback.SerialNumberCallBack;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.Communication;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.tools.Helper;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.logging.Logger;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.flightcontroller.GravityCenterState;
import dji.common.flightcontroller.LEDsSettings;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.KeyManager;
import dji.keysdk.callback.GetCallback;
import dji.sdk.flightcontroller.FlightController;

/**
 * 飞控
 */
public class FlightManager extends BaseManager {

    private FlightManager() {
    }

    private static class FlightControlHolder {
        private static final FlightManager INSTANCE = new FlightManager();
    }

    public static FlightManager getInstance() {
        return FlightControlHolder.INSTANCE;
    }

    public void initFlightInfo(Context context,MqttAndroidClient mqttAndroidClient){
        if (Helper.isFlightControllerAvailable()) {

            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            flightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            flightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);//相对于自己

            //低电量值，严重低电量
            flightController.getLowBatteryWarningThreshold(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    LocalSource.getInstance().setLowBatteryWarning(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getSeriousLowBatteryWarningThreshold(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    LocalSource.getInstance().setSeriousLowBatteryWarning(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getSmartReturnToHomeEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    LocalSource.getInstance().setSmartReturnToHomeEnabled(aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            flightController.getLEDsEnabledSettings(new CommonCallbacks.CompletionCallbackWith<LEDsSettings>() {
                @Override
                public void onSuccess(LEDsSettings leDsSettings) {
                    LocalSource.getInstance().setBeacons(leDsSettings.areBeaconsOn() ? "1" : "0");
                    LocalSource.getInstance().setFront(leDsSettings.areFrontLEDsOn() ? "1" : "0");
                    LocalSource.getInstance().setRear(leDsSettings.areRearLEDsOn() ? "1" : "0");
                    LocalSource.getInstance().setStatusIndicator(leDsSettings.isStatusIndicatorOn() ? "1" : "0");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            flightController.getGoHomeHeightInMeters(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    LocalSource.getInstance().setGoHomeHeightInMeters(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getMaxFlightHeight(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    LocalSource.getInstance().setMaxFlightHeight(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getMaxFlightRadiusLimitationEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    LocalSource.getInstance().setMaxFlightRadiusLimitationEnabled(aBoolean);
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getMaxFlightRadius(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    LocalSource.getInstance().setMaxFlightRadius(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            if (KeyManager.getInstance() != null) {
                KeyManager.getInstance().getValue(FlightControllerKey.create(FlightControllerKey.COLLISION_AVOIDANCE_ENABLED), new GetCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        LocalSource.getInstance().setCollisionAvoidance((boolean) o);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                    }
                });
            }
            flightController.setStateCallback(new FlightControllerStateCallBack(mqttAndroidClient));
        } else {
            Logger.getLogger("initFlightInfo Flight is null");
        }
    }

    //起飞
    public void takeOffType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.startTakeoff(djiError -> {
                if (djiError == null) {
                    sendCorrectMsg2Server(mqttAndroidClient,message, "起飞成功");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //上升/下降
    public void risingAndFallType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String speed = message.getPara().get(Constant.SPEED);
            if (!TextUtils.isEmpty(speed)) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                0, 0, 0, Float.parseFloat(speed)
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    sendCorrectMsg2Server(mqttAndroidClient,message, "上升中...");
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                }
                            }
                        }
                );
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }


    }


    //左旋/右旋
    public void leftAndRightHanded(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String speed = message.getPara().get(Constant.SPEED);
            if (!TextUtils.isEmpty(speed)) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                0, 0, Float.parseFloat(speed), 0
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    sendCorrectMsg2Server(mqttAndroidClient,message, "自旋中...");
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                }
                            }
                        }
                );
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }


    //向前/向后
    public void forwardAndBackType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String speed = message.getPara().get(Constant.SPEED);
            if (!TextUtils.isEmpty(speed)) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                0, Float.parseFloat(speed), 0, 0
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    sendCorrectMsg2Server(mqttAndroidClient,message, "移动中...");
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                }
                            }
                        }
                );
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //向左/向右
    public void leftAndRightType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String speed = message.getPara().get(Constant.SPEED);
            if (!TextUtils.isEmpty(speed)) {
                flightController.sendVirtualStickFlightControlData(
                        new FlightControlData(
                                Float.parseFloat(speed), 0, 0, 0
                        ), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    sendCorrectMsg2Server(mqttAndroidClient,message, "移动中...");
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                }
                            }
                        }
                );
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //自动返回
    public void autoReturnType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.startGoHome(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "开始返航");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //取消自动返回
    public void cancelAutoReturnType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.cancelGoHome(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "开始返航");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //降落
    public void startLanding(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.startLanding(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                sendCorrectMsg2Server(mqttAndroidClient,message, "降落...");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    }
            );
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //取消降落
    public void cancelLanding(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.cancelLanding(
                    new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                sendCorrectMsg2Server(mqttAndroidClient,message, "取消降落...");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    }
            );
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }


    //获取sdk控制无人机权限
    public void enableVirtualStick(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已获取控制权");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //取消sdk控制无人机权限
    public void disableVirtualStick(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setVirtualStickModeEnabled(false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已取消控制权");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //控制LED灯光显示
    public void setLEDsEnabledSettings(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String beacons = message.getPara().get(Constant.BEACONS);//信标
            String front = message.getPara().get(Constant.FRONT);//前置LED
            String rear = message.getPara().get(Constant.REAR);//后置LED
            String statusIndicator = message.getPara().get(Constant.STATUS_INDICATOR);//状态指示灯
            if (!TextUtils.isEmpty(beacons)) {
                LEDsSettings.Builder builder = new LEDsSettings.Builder().
                        beaconsOn(beacons.equals("1") ? true : false)
                        .frontLEDsOn(front.equals("1") ? true : false).
                                rearLEDsOn(rear.equals("1") ? true : false).
                                statusIndicatorOn(statusIndicator.equals("1") ? true : false);
                flightController.setLEDsEnabledSettings(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient,message, "灯光状态已更新");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //设置失控状态做的操作
    public void setConnectionFailBehavior(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "失控后动作已更新");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }


    //设置返航高度
    public void setGoHomeHeight(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setGoHomeHeightInMeters(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已设置返航高度");
                        LocalSource.getInstance().setGoHomeHeightInMeters(type);
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //设置限高20~500
    public void setMaxHeight(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setMaxFlightHeight(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已设置限高");
                        LocalSource.getInstance().setMaxFlightHeight(type);

                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }


    //设置中心校准
    public void setGravityCenterState(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setGravityCenterStateCallback(new GravityCenterState.Callback() {
                @Override
                public void onUpdate(GravityCenterState gravityCenterState) {
                    if (gravityCenterState.getCalibrationState() != GravityCenterState.GravityCenterCalibrationState.SUCCESSFUL) {
                        sendErrorMsg2Server(mqttAndroidClient,message, "设置中心校准失败");
                    } else {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "设置中心校准成功");
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }


    //设置是否启用最大半径限制
    public void setMaxFlightRadiusLimit(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String type = message.getPara().get(Constant.TYPE);
            String value = message.getPara().get(Constant.VALUE);
            flightController.setMaxFlightRadiusLimitationEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        if (type.equals("1") && !TextUtils.isEmpty(value)) {
                            flightController.setMaxFlightRadius(Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError == null) {
                                        sendCorrectMsg2Server(mqttAndroidClient,message, "已设置最大飞行半径");
                                    } else {
                                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                    }
                                }
                            });
                        }
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //设置IMU
    public void setIMUStart(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.startIMUCalibration(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "开始中心校准");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //设置低电量
    public void setLowBattery(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String type = message.getPara().get(Constant.TYPE);
            flightController.setLowBatteryWarningThreshold(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已设置低电量预警阈值");
                        LocalSource.getInstance().setLowBatteryWarning(type);
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //设置严重低电量
    public void setSeriousLowBatteryWarningThreshold(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String type = message.getPara().get(Constant.TYPE);
            flightController.setSeriousLowBatteryWarningThreshold(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已设置严重低电量预警阈值");
                        LocalSource.getInstance().setSeriousLowBatteryWarning(type);

                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //设置智能返航
    public void setSmartReturnToHomeEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            String type = message.getPara().get(Constant.TYPE);
            flightController.setSmartReturnToHomeEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "智能返航设置已变更");
                        LocalSource.getInstance().setSmartReturnToHomeEnabled(type.equals("1") ? true : false);
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }

    }

    //获取飞行状态
    public void isFlying(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            sendCorrectMsg2Server(mqttAndroidClient,message, flightController.getState().isFlying() ? "1" : "0");
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }

    //获取返航点经纬度
    public void getSLongLat(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {

        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.getHomeLocation(new CommonCallbacks.CompletionCallbackWith<LocationCoordinate2D>() {
                @Override
                public void onSuccess(LocationCoordinate2D locationCoordinate2D) {
                    sendCorrectMsg2Server(mqttAndroidClient,message, locationCoordinate2D.getLatitude()+","+locationCoordinate2D.getLongitude());
                }
                @Override
                public void onFailure(DJIError djiError) {
                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "flightController is null");
        }
    }
}
