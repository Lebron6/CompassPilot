package com.compass.ux.manager;

import android.text.TextUtils;
import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.GimbalAStateCallBack;
import com.compass.ux.callback.GimbalBStateCallBack;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.Communication;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.xclog.XcFileLog;
import java.util.List;
import dji.common.error.DJIError;
import dji.common.gimbal.Axis;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;
import static dji.common.gimbal.Axis.PITCH;
import static dji.common.gimbal.Axis.YAW;
import org.eclipse.paho.android.service.MqttAndroidClient;

/**
 * 云台
 */
public class GimbalManager extends BaseManager {
    private GimbalManager() {
    }

    private static class GimbalControlHolder {
        private static final GimbalManager INSTANCE = new GimbalManager();
    }

    public static GimbalManager getInstance() {
        return GimbalControlHolder.INSTANCE;
    }


    public void initGimbalInfo(MqttAndroidClient client) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbals.get(0).setStateCallback(new GimbalAStateCallBack(client));
                if (gimbals.size() > 1) {
                    gimbals.get(1).setStateCallback(new GimbalBStateCallBack(client));
                }
                gimbals.get(0).getControllerSpeedCoefficient(YAW, new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        LocalSource.getInstance().setGimbal_yaw_speed(integer + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                gimbals.get(0).getControllerSpeedCoefficient(PITCH, new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        LocalSource.getInstance().setGimbal_pitch_speed(integer + "");
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //云台俯仰限位扩展
                gimbals.get(0).getPitchRangeExtensionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LocalSource.getInstance().setPitchRangeExtension(aBoolean);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            } else {
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "initGimbalInfo:gimbal is null");
            }
        } else {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "initGimbalInfo:aircraft disconnect");
        }
    }

    //摄像头左右 -320~320
    public void cameraLeftAndRight(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String angle = message.getPara().get(Constant.ANGLE);
                if (!TextUtils.isEmpty(angle)) {
                    Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.RELATIVE_ANGLE).time(2);
                    builder.yaw(Float.parseFloat(angle));
                    builder.build();
                    gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                sendCorrectMsg2Server(mqttAndroidClient, message, "云台移动中...");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                            }
                        }
                    });
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "angle error");
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //摄像头上下 30~-90
    public void cameraUpAndDown(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String angle = message.getPara().get(Constant.ANGLE);
                if (!TextUtils.isEmpty(angle)) {
                    Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.RELATIVE_ANGLE).time(2);
                    builder.pitch(Float.parseFloat(angle));
                    builder.build();

                    gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                sendCorrectMsg2Server(mqttAndroidClient, message, "云台移动中...");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                            }
                        }
                    });
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "angle error");
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //摄像头上下 (绝对角度) 30~-90
    public void cameraUpAndDownByAbsoluteAngle(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String angle = message.getPara().get(Constant.ANGLE);
                if (!TextUtils.isEmpty(angle)) {
                    Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
                    builder.pitch(Float.parseFloat(angle));
                    builder.build();
                    gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                sendCorrectMsg2Server(mqttAndroidClient, message, "云台移动...");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                            }
                        }
                    });
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "angle error");
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }


    //水平归中
    public void levelToCenterType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                gimbal.resetYaw(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "resetYaw success");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //归中
    public void centerType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                gimbal.reset(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "云台已归中");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //垂直归中
    public void verticalCenterType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                Rotation.Builder builder = new Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).time(2);
                builder.pitch(Float.parseFloat("0"));
                builder.build();
                gimbal.rotate(builder.build(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "云台已垂直归中");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //设置偏航俯仰速度
    public void setGimbalSpeed(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String type = message.getPara().get(Constant.TYPE);
                String value = message.getPara().get(Constant.VALUE);
                Axis axis = PITCH;
                switch (type) {
                    case "0":
                        axis = PITCH;
                        break;
                    case "1":
                        axis = YAW;
                        break;
                }
                gimbal.setControllerSpeedCoefficient(axis, Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            switch (type) {
                                case "0":
                                    LocalSource.getInstance().setGimbal_pitch_speed(value);
                                    break;
                                case "1":
                                    LocalSource.getInstance().setGimbal_yaw_speed(value);
                                    break;
                            }
                            sendCorrectMsg2Server(mqttAndroidClient, message, "云台俯仰速度已更新");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
                //设置初始化云台工作模式为跟随模式
                gimbals.get(0).setMode(GimbalMode.find(2), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        LocalSource.getInstance().setGimbalMode(2);
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //恢复出厂
    public void restoreFactorySettings(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                gimbal.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "云台已恢复出厂设置");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }

    }


    //自动校准
    public void startCalibration(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {

        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                gimbal.startCalibration(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "开始自动校准");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //云台偏航缓启停
    public void setControllerSmoothingFactor(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String type = message.getPara().get(Constant.TYPE);
                String value = message.getPara().get(Constant.VALUE);
                Axis axis = PITCH;
                switch (type) {
                    case "0":
                        axis = PITCH;
                        break;
                    case "1":
                        axis = YAW;
                        break;
                }
                gimbal.setControllerSmoothingFactor(axis, Integer.parseInt(value), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "云台开始偏航");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //设置扩展云台俯仰范围
    public void setPitchRangeExtensionEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String type = message.getPara().get(Constant.TYPE);
                if (!TextUtils.isEmpty(type)) {
                    gimbal.setPitchRangeExtensionEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                LocalSource.getInstance().setPitchRangeExtension(type.equals("1") ? true : false);
                                sendCorrectMsg2Server(mqttAndroidClient, message, "云台扩展状态已变更");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                            }
                        }
                    });
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //设置云台模式
    public void setMode(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {

        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                String type = message.getPara().get(Constant.TYPE);
                if (gimbal != null && !TextUtils.isEmpty(type)) {
                    gimbal.setMode(GimbalMode.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                LocalSource.getInstance().setGimbalMode(Integer.parseInt(type));
                                sendCorrectMsg2Server(mqttAndroidClient, message, "云台模式已变更");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                            }
                        }
                    });
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "gimbal is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }

    }

//    //云台转速
//    public void holderSpeedType(DataInfo.Message message) {
//        DataCache.getInstance().setHolderSpeedType(message.getHolderControl().getSpeed());
//        sendCorrectMsg2Server(mqttAndroidClient,message, "云台转速已更新");
//    }


}
