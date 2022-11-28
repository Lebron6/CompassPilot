package com.compass.ux.manager;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.tools.ToastUtil;
import com.google.gson.Gson;

import android.text.TextUtils;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.CameraStateCallBack;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.entity.StringsBean;
import com.compass.ux.entity.Communication;
import com.compass.ux.tools.Helper;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.greenrobot.eventbus.EventBus;

import java.util.logging.Logger;

import dji.common.camera.CameraVideoStreamSource;
import dji.common.camera.FocusState;
import dji.common.camera.PhotoTimeLapseSettings;
import dji.common.camera.SettingsDefinitions;
import dji.common.camera.WatermarkSettings;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;

import static com.compass.ux.constant.Constant.FLAG_CONNECT;
import static com.compass.ux.constant.Constant.FLAG_ZOOM_FOCAL_LENGTH;
import static com.compass.ux.tools.Utils.getbigZoomValue;
import static dji.common.camera.CameraVideoStreamSource.DEFAULT;
import static dji.common.camera.CameraVideoStreamSource.INFRARED_THERMAL;
import static dji.common.camera.CameraVideoStreamSource.WIDE;
import static dji.common.camera.CameraVideoStreamSource.ZOOM;
import static dji.common.camera.SettingsDefinitions.ExposureMode.MANUAL;
import static dji.common.camera.SettingsDefinitions.ExposureMode.PROGRAM;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.UNKNOWN;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_1;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_2;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_4;
import static dji.common.camera.SettingsDefinitions.ThermalDigitalZoomFactor.X_8;

import androidx.annotation.NonNull;

/**
 * 相机
 */
public class CameraManager extends BaseManager {

    private CameraManager() {
    }

    private static class CameraManagerHolder {
        private static final CameraManager INSTANCE = new CameraManager();
    }

    public static CameraManager getInstance() {
        return CameraManagerHolder.INSTANCE;
    }

    //初始相机状态
    public void initCameraInfo() {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            //视频流
            CommonCallbacks.CompletionCallbackWith<CameraVideoStreamSource> streamSourceCallBack = new CommonCallbacks.CompletionCallbackWith<CameraVideoStreamSource>() {
                @Override
                public void onSuccess(CameraVideoStreamSource cameraVideoStreamSource) {
                    LocalSource.getInstance().setCurrentLens(cameraVideoStreamSource.value() + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //相机模式
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode> cameraModeCallback = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.CameraMode>() {

                @Override
                public void onSuccess(SettingsDefinitions.CameraMode cameraMode) {
                    LocalSource.getInstance().setCameraMode(cameraMode.value());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //返回激光测距
            CommonCallbacks.CompletionCallbackWith<Boolean> laserCallBack = new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (aBoolean) {
                        camera.getLens(camera.getIndex()).setLaserMeasureInformationCallback(new CameraStateCallBack());
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //返回曝光模式
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureMode> exposureModeCallBack = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.ExposureMode exposureMode) {
                    LocalSource.getInstance().setExposureMode(exposureMode.value());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //iso
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO> isoCallback = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ISO>() {
                @Override
                public void onSuccess(SettingsDefinitions.ISO iso) {
                    LocalSource.getInstance().setISO(iso.value());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //shutter
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ShutterSpeed> shutterCallback = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ShutterSpeed>() {

                @Override
                public void onSuccess(SettingsDefinitions.ShutterSpeed shutterSpeed) {
                    LocalSource.getInstance().setShutter(shutterSpeed.value());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //曝光补偿
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureCompensation> exposureCompensationCallBack = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ExposureCompensation>() {

                @Override
                public void onSuccess(SettingsDefinitions.ExposureCompensation exposureCompensation) {
                    LocalSource.getInstance().setExposureCompensation(exposureCompensation.value());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //对焦模式
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.FocusMode> focusCallback = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.FocusMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.FocusMode focusMode) {
                    LocalSource.getInstance().setFocusMode(focusMode.value() + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //自动曝光是否锁定
            CommonCallbacks.CompletionCallbackWith<Boolean> aelockCallBack = new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    LocalSource.getInstance().setLockExposure(aBoolean ? "0" : "1");
                }

                @Override
                public void onFailure(DJIError djiError) {
                }
            };
            //红外变焦
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ThermalDigitalZoomFactor> thermalCallback = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.ThermalDigitalZoomFactor>() {
                @Override
                public void onSuccess(SettingsDefinitions.ThermalDigitalZoomFactor thermalDigitalZoomFactor) {
                    LocalSource.getInstance().setThermalDigitalZoom(thermalDigitalZoomFactor.value() + "");

                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            //显示模式
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.DisplayMode> displayCallBack = new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.DisplayMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.DisplayMode displayMode) {
                    LocalSource.getInstance().setHyDisplayMode(displayMode.value() + "");

                }

                @Override
                public void onFailure(DJIError djiError) {
                }
            };
            //变焦焦距
            CommonCallbacks.CompletionCallbackWith<Integer> HybridZoomFocalLengthCallBack = new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {

                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            FocusState.Callback focusStateCallback=new FocusState.Callback() {
                @Override
                public void onUpdate(@NonNull FocusState focusState) {
                    int i = (focusState.getTargetFocalLength() * 4) / 951;
                    com.orhanobut.logger.Logger.e("当前焦距："+i);
                    LocalSource.getInstance().setHybridZoom(i);
                    EventBus.getDefault().post(FLAG_ZOOM_FOCAL_LENGTH);
                }
            };
            CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.HybridZoomSpec> hybridZoomSpec=new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.HybridZoomSpec>() {
                @Override
                public void onSuccess(SettingsDefinitions.HybridZoomSpec hybridZoomSpec) {
                    com.orhanobut.logger.Logger.e("最小焦距:"+hybridZoomSpec.getMinHybridFocalLength());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            };
            camera.getCameraVideoStreamSource(streamSourceCallBack);
            camera.getMode(cameraModeCallback);
            //多镜头
            if (camera.isMultiLensCameraSupported()) {
                camera.getLens(camera.getIndex()).getISO(isoCallback);
                camera.getLens(0).getShutterSpeed(shutterCallback);
                camera.getLaserEnabled(laserCallBack);
                camera.getLens(camera.getIndex()).getExposureCompensation(exposureCompensationCallBack);
                camera.getLens(0).getFocusMode(focusCallback);
                camera.getLens(0).getExposureMode(exposureModeCallBack);
                camera.getLens(0).getAELock(aelockCallBack);
                camera.getLens(2).getThermalDigitalZoomFactor(thermalCallback);
                camera.getLens(2).getDisplayMode(displayCallBack);
                camera.getLens(0).getOpticalZoomFocalLength(HybridZoomFocalLengthCallBack);
                camera.getLens(0).setFocusStateCallback(focusStateCallback);
                camera.getLens(0).getHybridZoomSpec(hybridZoomSpec);

            }
            //单镜头
            else {
                camera.getISO(isoCallback);
                camera.getShutterSpeed(shutterCallback);
                camera.getExposureCompensation(exposureCompensationCallBack);
                camera.getFocusMode(focusCallback);
                camera.getExposureMode(exposureModeCallBack);
                camera.getAELock(aelockCallBack);
                camera.getThermalDigitalZoomFactor(thermalCallback);
                camera.getDisplayMode(displayCallBack);
                camera.getHybridZoomFocalLength(HybridZoomFocalLengthCallBack);
                camera.setFocusStateCallback(focusStateCallback);
                camera.getHybridZoomSpec(hybridZoomSpec);
            }
        } else {
            Logger.getLogger("initCameraInformation camera is null");
        }
    }

    private int getSmallZoomValue(int bigZoomFromDJ) {
        int smallZoom = (bigZoomFromDJ - 317) / ((47549 - 317) / 199 + 2);
        return smallZoom;
    }

    //控制切换 1变焦 2广角 3红外
    public void controlSwitchType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            if (camera.isMultiLensCameraSupported()) {
                CameraVideoStreamSource source = DEFAULT;
                String type = message.getPara().get(Constant.TYPE);
                switch (type) {
                    case "1":
                        source = WIDE;//广角
                        break;
                    case "2":
                        source = ZOOM;//变焦
                        break;
                    case "3":
                        source = INFRARED_THERMAL;//红外
                        break;
                }
                camera.setCameraVideoStreamSource(source, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setCurrentLens(type);
                            sendCorrectMsg2Server(mqttAndroidClient,message, "切换成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });

            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //切换云台/FPV
    public void visualAngleType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        LocalSource.getInstance().setCurrentVideoSource(type);
        EventBus.getDefault().post(Constant.VISUAL_ANGLE_TYPE);
        sendCorrectMsg2Server(mqttAndroidClient,message, "视角已切换");
    }


    //模式切换 1拍照模式 2录像模式
    public void modeSwitchType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.exitPlayback(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    String type = message.getPara().get(Constant.TYPE);
                    SettingsDefinitions.FlatCameraMode cameraMode = SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE;
                    switch (type) {
                        case "0":
                            cameraMode = SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE;
                            break;
                        case "1":
                            cameraMode = SettingsDefinitions.FlatCameraMode.VIDEO_NORMAL;
                            break;
                    }
                    camera.setFlatMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                LocalSource.getInstance().setCameraMode(Integer.parseInt(type));
                                sendCorrectMsg2Server(mqttAndroidClient,message, "切换成功");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    });
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //切换相机拍照模式
    public void setCameraShootMode(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            String type = message.getPara().get(Constant.TYPE);
            SettingsDefinitions.FlatCameraMode shootPhoto = SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE;
            switch (type) {
                case "0":
                    shootPhoto = SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE;
                    break;
                case "1":
                    shootPhoto = SettingsDefinitions.FlatCameraMode.PHOTO_TIME_LAPSE;
                    break;
            }
            camera.setFlatMode(shootPhoto, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
//                            sendCorrectMsg2Server(mqttAndroidClient,message, "切换成功");
                        if (type.equals("2")) {//延时模式
                            int interval = Integer.parseInt(message.getPara().get("interval"));//拍摄两张照片之间的时间间隔。
                            int duration = Integer.parseInt(message.getPara().get("duration"));//相机拍照的总时间。
                            String fileFormat = message.getPara().get("fileFormat");//文件格式。
                            SettingsDefinitions.PhotoTimeLapseFileFormat photoTimeLapseFileFormat = null;
                            switch (fileFormat) {
                                case "0":
                                    photoTimeLapseFileFormat = SettingsDefinitions.PhotoTimeLapseFileFormat.VIDEO;
                                    break;
                                case "1":
                                    photoTimeLapseFileFormat = SettingsDefinitions.PhotoTimeLapseFileFormat.JPEG_AND_VIDEO;
                                    break;
                            }
                            camera.setPhotoTimeLapseSettings(new PhotoTimeLapseSettings(interval, duration, photoTimeLapseFileFormat), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError == null) {
                                        sendCorrectMsg2Server(mqttAndroidClient,message, "setPhotoTimeLapseSettings success");
                                    } else {
                                        sendErrorMsg2Server(mqttAndroidClient,message, "setPhotoTimeLapseSettings :" + djiError.getDescription());
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
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //拍照
    public void startShootPhoto(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.getFlatMode(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.FlatCameraMode>() {
                @Override
                public void onSuccess(SettingsDefinitions.FlatCameraMode flatCameraMode) {
                    if (flatCameraMode == SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE) {
                        camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    sendCorrectMsg2Server(mqttAndroidClient,message, "拍摄成功");
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                }
                            }
                        });
                    } else {
                        camera.setFlatMode(SettingsDefinitions.FlatCameraMode.PHOTO_SINGLE, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError == null) {
                                                sendCorrectMsg2Server(mqttAndroidClient,message, "拍摄成功");
                                            } else {
                                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                            }
                                        }
                                    });
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, "拍照设置相机模式失败:" + djiError.getDescription());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //停止拍照
    public void stopShootPhoto(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.stopShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "停止拍照");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }

    }


    //录像
    public void startRecordVideo(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "开始录像");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //停止录像
    public void stopRecordVideo(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "停止录像");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置激光测距
    public void setLaserEnable(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                boolean flag = false;
                String type = message.getPara().get(Constant.TYPE);
                switch (type) {
                    case "0":
                        flag = true;
                        break;
                    case "1":
                        flag = false;
                        break;
                }
                camera.setLaserEnabled(flag, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            if (type.equals("0")){
                                camera.getLens(camera.getIndex()).setLaserMeasureInformationCallback(new CameraStateCallBack());
                            }
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置激光测距");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //获取激光测距状态
    public void getLaserEnable(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                camera.getLaserEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        StringsBean stringsBean = new StringsBean();
                        stringsBean.setValue(aBoolean ? "0" : "1");
                        sendCorrectMsg2Server(mqttAndroidClient,message, new Gson().toJson(stringsBean, StringsBean.class));
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }


    //设置ISO
    public void setISO(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                camera.getLens(0).setISO(SettingsDefinitions.ISO.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setISO(Integer.parseInt(type));
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }

    }

    //设置曝光补偿
    public void setExposureCom(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                camera.getLens(0).setExposureCompensation(SettingsDefinitions.ExposureCompensation.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setExposureCompensation(Integer.parseInt(type));
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }

    //设置曝光模式
    public void setExposureMode(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                SettingsDefinitions.ExposureMode exposureMode = SettingsDefinitions.ExposureMode.UNKNOWN;
                switch (type) {
                    case "1":
                        exposureMode = PROGRAM;
                        break;
                    case "4":
                        exposureMode = MANUAL;
                        break;
                }
                camera.getLens(0).setExposureMode(exposureMode, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setExposureMode(Integer.parseInt("type"));
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "product not supported");
        }
    }


    //设置shutter
    public void setShutter(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                camera.getLens(0).setShutterSpeed(SettingsDefinitions.ShutterSpeed.find(Float.parseFloat(type)), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setShutter(Float.parseFloat(type));
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        }
    }

    //设置变焦
    public void setCameraZoom(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                if (!TextUtils.isEmpty(type) && camera.isMultiLensCameraSupported()) {
                    camera.getLens(0).setHybridZoomFocalLength(getbigZoomValue(type), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                LocalSource.getInstance().setHybridZoom(Integer.parseInt(type));
                                sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    });
                } else if (!TextUtils.isEmpty(type) && camera.isHybridZoomSupported()) {
                    camera.setHybridZoomFocalLength(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                LocalSource.getInstance().setHybridZoom(Integer.parseInt(type));
                                sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    });

                } else if (!TextUtils.isEmpty(type) && camera.isTapZoomSupported()) {//光学变焦
                    camera.setTapZoomMultiplier(Integer.parseInt(type), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                LocalSource.getInstance().setHybridZoom(Integer.parseInt(type));
                                sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                            }
                        }
                    });
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
            }
        }
    }

    //设置对焦模式
    public void setFocusMode(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                SettingsDefinitions.FocusMode focusMode = SettingsDefinitions.FocusMode.AUTO;
                switch (type) {
                    case "0":
                        focusMode = SettingsDefinitions.FocusMode.MANUAL;
                        break;
                    case "1":
                        focusMode = SettingsDefinitions.FocusMode.AUTO;
                        break;
                    case "2":
                        focusMode = SettingsDefinitions.FocusMode.AFC;
                        break;
                    default:
                        focusMode = SettingsDefinitions.FocusMode.UNKNOWN;
                        break;
                }
                camera.getLens(0).setFocusMode(focusMode, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setFocusMode(type);
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置曝光锁定
    public void setAELock(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        boolean exposure = false;
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                switch (type) {
                    case "0":
                        exposure = true;
                        break;
                    case "1":
                        exposure = false;
                        break;
                }
                camera.getLens(0).setAELock(exposure, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setLockExposure(type);
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置红外的焦距
    public void setThermalDigitalZoomFactor(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isM300Product()) {
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                SettingsDefinitions.ThermalDigitalZoomFactor thermalDigitalZoomFactor;
                switch (type) {
                    case "0":
                        thermalDigitalZoomFactor = X_1;
                        break;
                    case "1":
                        thermalDigitalZoomFactor = X_2;
                        break;
                    case "2":
                        thermalDigitalZoomFactor = X_4;
                        break;
                    case "3":
                        thermalDigitalZoomFactor = X_8;
                        break;
                    default:
                        thermalDigitalZoomFactor = UNKNOWN;
                        break;
                }
                camera.getLens(2).setThermalDigitalZoomFactor(thermalDigitalZoomFactor, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError == null) {
                            LocalSource.getInstance().setThermalDigitalZoom(type);
                            sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                        }
                    }
                });
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }


    //格式化sdcard
    public void formatSDCard(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.formatSDCard(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "格式化成功");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置拍照时led开关
    public void setBeaconAutoTurnOffEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.setBeaconAutoTurnOffEnabled(type.equals("1") ? true : false, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "格式化成功");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置调色板
    public void setThermalPalette(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.getLens(2).setThermalPalette(SettingsDefinitions.ThermalPalette.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置等温度线
    public void setThermalIsothermEnabled(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            String type = message.getPara().get(Constant.TYPE);
            String min_value = message.getPara().get(Constant.MIN_VALUE);
            String max_value = message.getPara().get(Constant.MAX_VALUE);
            boolean tf = type.equals("1") ? true : false;
            camera.getLens(2).setThermalIsothermUnit(SettingsDefinitions.ThermalIsothermUnit.CELSIUS, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError != null) {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    } else {
                        camera.getLens(2).setThermalIsothermEnabled(tf, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                }
                            }
                        });
                        if (tf) {
                            camera.getLens(2).setThermalIsothermUpperValue(Integer.parseInt(max_value), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {

                                }
                            });
                            camera.getLens(2).setThermalIsothermLowerValue(Integer.parseInt(min_value), new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {

                                }
                            });
                        }
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }


    //设置水印
    public void setWatermarkSettings(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            String video = message.getPara().get("video");
            String photo = message.getPara().get("photo");
            WatermarkSettings watermarkSettings = new WatermarkSettings(video.equals("1") ? true : false, photo.equals("1") ? true : false);
            camera.setWatermarkSettings(watermarkSettings, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "设置成功");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //相机重置参数（应该是恢复出厂设置）
    public void cameraRestoreFactorySettings(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        sendCorrectMsg2Server(mqttAndroidClient,message, "已重置相机参数");
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置红外展示模式 1/2
    public void setHyDisplayMode(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String type = message.getPara().get(Constant.TYPE);
        if (Helper.isCameraModuleAvailable()) {
            Camera camera = ApronApp.getProductInstance().getCamera();
            camera.getLens(2).setDisplayMode(SettingsDefinitions.DisplayMode.find(Integer.parseInt(type)), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        if (type.equals("2")) {
                            camera.setCameraVideoStreamSource(ZOOM, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    camera.setCameraVideoStreamSource(INFRARED_THERMAL, new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError == null) {
                                                LocalSource.getInstance().setHyDisplayMode("type");
                                                sendCorrectMsg2Server(mqttAndroidClient,message, "红外模式已变更");
                                            } else {
                                                sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient,message, djiError.getDescription());
                    }
                }
            });

        } else {
            sendErrorMsg2Server(mqttAndroidClient,message, "camera is null");
        }
    }

    //设置变焦
    public void setCameraZoom(int zoomNum) {
        com.orhanobut.logger.Logger.e("设置变焦："+zoomNum);
            if (Helper.isCameraModuleAvailable()) {
                Camera camera = ApronApp.getProductInstance().getCamera();
                if (camera.isMultiLensCameraSupported()) {
                    com.orhanobut.logger.Logger.e("变焦大致："+getbigZoomValue(zoomNum+""));
                    camera.getLens(0).setHybridZoomFocalLength(getbigZoomValue(zoomNum+""), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                            } else {
                                ToastUtil.showToast("变焦失败:"+djiError.getDescription());
                            }
                        }
                    });
                } else if (camera.isHybridZoomSupported()) {
                    camera.setHybridZoomFocalLength(zoomNum, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {

                            } else {
                                ToastUtil.showToast("变焦失败:"+djiError.getDescription());
                            }
                        }
                    });

                } else if (camera.isTapZoomSupported()) {//光学变焦
                    camera.setTapZoomMultiplier(zoomNum, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {

                            } else {
                                ToastUtil.showToast("变焦失败:"+djiError.getDescription());
                            }
                        }
                    });
                }
            } else {
                ToastUtil.showToast("变焦失败:相机未连接");
            }

    }
}
