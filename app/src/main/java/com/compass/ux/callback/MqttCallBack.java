package com.compass.ux.callback;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.DataCache;
import com.compass.ux.manager.AccountManager;
import com.compass.ux.manager.AirLinkManager;
import com.compass.ux.manager.AssistantManager;
import com.compass.ux.manager.CameraManager;
import com.compass.ux.manager.FlightManager;
import com.compass.ux.manager.GimbalManager;
import com.compass.ux.manager.InitializationManager;
import com.compass.ux.manager.MissionManager;
import com.compass.ux.manager.MissionV1Manager;
import com.compass.ux.manager.PlayBackManager;
import com.compass.ux.manager.RTKManager;
import com.compass.ux.manager.SpeakerManager;
import com.compass.ux.manager.StreamManager;
import com.compass.ux.manager.SystemManager;
import com.compass.ux.xclog.XcFileLog;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import dji.sdk.sdkmanager.DJISDKManager;

public class MqttCallBack implements MqttCallbackExtended {

    private MqttAndroidClient mqttAndroidClient;

    public MqttCallBack(MqttAndroidClient mqttAndroidClient) {
        this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    public void connectionLost(Throwable cause) {
        if (cause != null) {
            Logger.e("监听到MQtt断开连接:" + cause.toString());
        }
        XcFileLog.getInstace().i("监听到MQtt断开连接：", "-----");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        ProtoMessage.Message message = null;
        try {
            message = ProtoMessage.Message.parseFrom(mqttMessage.getPayload());
        } catch (InvalidProtocolBufferException e) {
            Logger.e("接收异常:" + e.toString());
            e.printStackTrace();
        }
        Logger.e("测试监听：" + topic + "：" + new Gson().toJson(message));
        switch (message.getMethod()) {
            //推流地址
            case Constant.LIVE_PATH:
                Logger.e("获取推流地址" + message.getPara().get("desRtmpUrl"));
                DataCache.getInstance().setRtmp_address(message.getPara().get("desRtmpUrl"));
                EventBus.getDefault().post(Constant.FLAG_STREAM_URL);
                break;
            //起飞
            case Constant.START_TAKE_OFF:
                FlightManager.getInstance().takeOffType(mqttAndroidClient, message);
                break;
            //获取控制权限
            case Constant.ENABLE_VIRTUAL_STICK:
                FlightManager.getInstance().enableVirtualStick(mqttAndroidClient, message);
                break;
            //取消控制权限
            case Constant.DISABLE_VIRTUAL_STICK:
                FlightManager.getInstance().disableVirtualStick(mqttAndroidClient, message);
                break;
            //降落
            case Constant.START_LANDING:
                FlightManager.getInstance().startLanding(mqttAndroidClient, message);
                break;
            //取消降落
            case Constant.CANCEL_LANDING:
                FlightManager.getInstance().cancelLanding(mqttAndroidClient, message);
                break;
            //云台左右
            case Constant.CAMERA_LEFT_AND_RIGHT:
                GimbalManager.getInstance().cameraLeftAndRight(mqttAndroidClient, message);
                break;
            //云台上下
            case Constant.CAMERA_UP_AND_DOWN:
                GimbalManager.getInstance().cameraUpAndDown(mqttAndroidClient, message);
                break;
            //云台上下固定度数
            case Constant.CAMERA_UP_AND_DOWN_BY_ABSOLUTE_ANGLE:
                GimbalManager.getInstance().cameraUpAndDownByAbsoluteAngle(mqttAndroidClient, message);
                break;
            //云台回正
            case Constant.CAMERA_CENTER:
                GimbalManager.getInstance().centerType(mqttAndroidClient, message);
                break;
            //云台垂直回正
            case Constant.CAMERA_CENTER_PITCH:
                GimbalManager.getInstance().verticalCenterType(mqttAndroidClient, message);
                break;
            //云台水平回正
            case Constant.CAMERA_CENTER_YAW:
                GimbalManager.getInstance().levelToCenterType(mqttAndroidClient, message);
                break;
            //上升下降
            case Constant.RISE_AND_FALL:
                FlightManager.getInstance().risingAndFallType(mqttAndroidClient, message);
                break;
            //向左向右自转
            case Constant.TURN_LEFT_AND_TURN_RIGHT:
                FlightManager.getInstance().leftAndRightHanded(mqttAndroidClient, message);
                break;
            //前进后退
            case Constant.FLY_FORWARD_AND_BACK:
                FlightManager.getInstance().forwardAndBackType(mqttAndroidClient, message);
                break;
            //往左往右
            case Constant.FLY_LEFT_AND_RIGHT:
                FlightManager.getInstance().leftAndRightType(mqttAndroidClient, message);
                break;
            //航点规划V2
            case Constant.WAYPOINT_PLAN_V2:
                if (ApronApp.isM300Product()) {
                    MissionManager.getInstance().createWayPointV2Mission(mqttAndroidClient, message);
                } else {
                    MissionV1Manager.getInstance().createWayPointMission(mqttAndroidClient, message);
                }
                break;
            //航线自动飞行开始V2
            case Constant.WAYPOINT_FLY_START_V2:
                if (ApronApp.isM300Product()) {
                    MissionManager.getInstance().startType(mqttAndroidClient, message);
                } else {
                    MissionV1Manager.getInstance().startWaypointMission(mqttAndroidClient, message);
                }
                break;
            //航线自动飞行停止V2
            case Constant.WAYPOINT_FLY_STOP_V2:
                if (ApronApp.isM300Product()) {
                    MissionManager.getInstance().endType(mqttAndroidClient, message);
                } else {
                    MissionV1Manager.getInstance().stopWaypointMission(mqttAndroidClient, message);
                }
                break;
            //航线暂停V2
            case Constant.WAYPOINT_FLY_PAUSE_V2:
                if (ApronApp.isM300Product()) {
                    MissionManager.getInstance().suspendType(mqttAndroidClient, message);
                } else {
                    MissionV1Manager.getInstance().pauseWaypointMission(mqttAndroidClient, message);
                }
                break;
            //航线恢复V2
            case Constant.WAYPOINT_FLY_RESUME_V2:
                if (ApronApp.isM300Product()) {
                    MissionManager.getInstance().continueType(mqttAndroidClient, message);
                } else {
                    MissionV1Manager.getInstance().resumeWaypointMission(mqttAndroidClient, message);
                }
                break;
            //回家
            case Constant.GO_HOME:
                FlightManager.getInstance().autoReturnType(mqttAndroidClient, message);
                break;
            //取消回家
            case Constant.CANCEL_GO_HOME:
                FlightManager.getInstance().cancelAutoReturnType(mqttAndroidClient, message);
                break;
            //切换广角镜头变焦镜头红外镜头
            case Constant.CHANGE_LENS:
                CameraManager.getInstance().controlSwitchType(mqttAndroidClient, message);
                break;
            //切换普通视角FPV视角
            case Constant.CHANGE_CAMERA_FPV_VISUAL:
                CameraManager.getInstance().visualAngleType(mqttAndroidClient, message);
                break;
            //切换相机拍照/录像模式
            case Constant.CHANGE_CAMERA_MODE:
                CameraManager.getInstance().modeSwitchType(mqttAndroidClient, message);
                break;
            //切换相机拍照模式
            case Constant.CHANGE_CAMERA_SHOOT_MODE:
                CameraManager.getInstance().setCameraShootMode(mqttAndroidClient, message);
                break;
            //开始拍照
            case Constant.CAMERE_START_SHOOT:
                CameraManager.getInstance().startShootPhoto(mqttAndroidClient, message);
                break;
            //结束拍照
            case Constant.CAMERE_STOP_SHOOT:
                CameraManager.getInstance().stopShootPhoto(mqttAndroidClient, message);
                break;
            //开始录像
            case Constant.CAMERE_START_RECORE:
                CameraManager.getInstance().startRecordVideo(mqttAndroidClient, message);
                break;
            //结束录像
            case Constant.CAMERE_STOP_RECORE:
                CameraManager.getInstance().stopRecordVideo(mqttAndroidClient, message);
                break;
            //启用或禁用激光测距
            case Constant.SET_LASER_ENABLE:
                CameraManager.getInstance().setLaserEnable(mqttAndroidClient, message);
                break;
            //判断是否开启或警用激光测距
            case Constant.GET_LASER_ENABLE:
                CameraManager.getInstance().getLaserEnable(mqttAndroidClient, message);
                break;
            //设置灯光
            case Constant.SET_LED:
                FlightManager.getInstance().setLEDsEnabledSettings(mqttAndroidClient, message);
                break;
            //设置ISO
            case Constant.SET_ISO:
                CameraManager.getInstance().setISO(mqttAndroidClient, message);
                break;
            //设置曝光补偿
            case Constant.SET_EXPOSURE_COM:
                CameraManager.getInstance().setExposureCom(mqttAndroidClient, message);
                break;
            //设置曝光模式
            case Constant.SET_EXPOSURE_MODE:
                CameraManager.getInstance().setExposureMode(mqttAndroidClient, message);
                break;
            //设置shutter
            case Constant.SET_SHUTTER:
                CameraManager.getInstance().setShutter(mqttAndroidClient, message);
                break;
            //设置变焦
            case Constant.SET_CAMERA_ZOOM:
                CameraManager.getInstance().setCameraZoom(mqttAndroidClient, message);
                break;
            //设置对焦模式
            case Constant.SET_FOCUS_MODE:
                CameraManager.getInstance().setFocusMode(mqttAndroidClient, message);
                break;
            //曝光锁定
            case Constant.LOCK_EXPOSURE:
                CameraManager.getInstance().setAELock(mqttAndroidClient, message);
                break;
            //设置热像仪变焦
            case Constant.SET_THERMAL_DIGITAL_ZOOM:
                CameraManager.getInstance().setThermalDigitalZoomFactor(mqttAndroidClient, message);
                break;
            //获取初始数据
            case Constant.GET_INITIALIZATION_DATA:
//                get_initialization_data(communication);

                break;
            //登录
            case Constant.LOGIN:
//                AccountManager.getInstance().loginAccount();
                break;
            //注销
            case Constant.LOGINOUT:
                AccountManager.getInstance().loginOut();
                break;
            //设置失控后飞机执行的操作
            case Constant.SET_CONNECT_FAIL_BEHAVIOR:
                FlightManager.getInstance().setConnectionFailBehavior(mqttAndroidClient, message);
                break;
            //格式化内存卡
            case Constant.FORMAT_SDCARD:
                CameraManager.getInstance().formatSDCard(mqttAndroidClient, message);
                break;
            //设置拍照时led自动开关
            case Constant.SET_LED_AUTO_TURN_OFF:
                CameraManager.getInstance().setBeaconAutoTurnOffEnabled(mqttAndroidClient, message);
                break;
            //设置调色板
            case Constant.SET_THERMAL_PALETTE:
                CameraManager.getInstance().setThermalPalette(mqttAndroidClient, message);
                break;
            //设置等温线
            case Constant.SET_THERMAL_ISO_THERM_UNIT:
                CameraManager.getInstance().setThermalIsothermEnabled(mqttAndroidClient, message);
                break;
            //设置水印
            case Constant.SET_WATER_MARK_SETTINGS:
                CameraManager.getInstance().setWatermarkSettings(mqttAndroidClient, message);
                break;
            //相机重置参数（恢复出厂设置）
            case Constant.CAMERA_RFS:
                CameraManager.getInstance().cameraRestoreFactorySettings(mqttAndroidClient, message);
                break;
            //开始推流
            case Constant.START_LIVE:
                StreamManager.getInstance().startLiveShow(mqttAndroidClient, message);
                break;
            //结束推流
            case Constant.STOP_LIVE:
                StreamManager.getInstance().stopLiveShow(mqttAndroidClient, message);
                break;
            //重启推流
            case Constant.RESTART_LIVE:
                StreamManager.getInstance().restartLiveShow(mqttAndroidClient, message);
                break;
            //重启app
            case Constant.RESTART_APP:
                SystemManager.getInstance().restartApp();
                //设置返航高度
            case Constant.SET_GO_HOME_HEIGHT:
                FlightManager.getInstance().setGoHomeHeight(mqttAndroidClient, message);
                break;
            //设置限高
            case Constant.SET_MAX_HEIGHT:
                FlightManager.getInstance().setMaxHeight(mqttAndroidClient, message);
                break;
            //设置重心校准
            case Constant.SET_GRAVITY_CENTER_STATE:
                FlightManager.getInstance().setGravityCenterState(mqttAndroidClient, message);
                break;
            //设置是否启用最大飞行半径限制
            case Constant.SET_MFRL:
                FlightManager.getInstance().setMaxFlightRadiusLimit(mqttAndroidClient, message);
                break;
            //开始校准IMU
            case Constant.START_IMU:
                FlightManager.getInstance().setIMUStart(mqttAndroidClient, message);
                break;
            //获取设置的一些默认参数
            case Constant.GET_SETTING_DATA:
//                getSettingValue(communication);
                InitializationManager.getInstance().sendInitializationSettings(mqttAndroidClient, message);
                break;
            //设置低电量
            case Constant.SET_LOW_BATTERY:
                FlightManager.getInstance().setLowBattery(mqttAndroidClient, message);
                break;
            //设置严重低电量
            case Constant.SET_SERIOUS_LOW_BATTERY:
                FlightManager.getInstance().setSeriousLowBatteryWarningThreshold(mqttAndroidClient, message);
                break;
            //设置智能返航
            case Constant.SET_SMART_GOHOME:
                FlightManager.getInstance().setSmartReturnToHomeEnabled(mqttAndroidClient, message);
                break;
            //设置视觉定位
            case Constant.SET_VISION_ASSISTED:
                AssistantManager.getInstance().setVisionAssistedPositioningEnabled(mqttAndroidClient, message);
                break;
            //设置精确着陆
            case Constant.SET_PRECISION_LAND:
                AssistantManager.getInstance().setPrecisionLandingEnabled(mqttAndroidClient, message);
                break;
            //向上避障
            case Constant.SET_UPWARDS_AVOIDANCE:
                AssistantManager.getInstance().setUpwardVisionObstacleAvoidanceEnabled(mqttAndroidClient, message);
                break;
            //向下避障
            case Constant.SET_LANDING_PROTECTION:
                AssistantManager.getInstance().setLandingProtectionEnabled(mqttAndroidClient, message);
                break;
            //设置上下感知距离
            case Constant.SET_MAX_PERCEPTION_DISTANCE:
                AssistantManager.getInstance().setMaxPerceptionDistance(mqttAndroidClient, message);
                break;
            //设置上下避障安全距离
            case Constant.SET_AVOIDANCE_DISTANCE:
                AssistantManager.getInstance().setVisualObstaclesAvoidanceDistance(mqttAndroidClient, message);
                break;
            //设置避障刹车功能
            case Constant.SET_ACTIVITY_OBSTACLE_AVOIDANCE:
                AssistantManager.getInstance().setHorizontalVisionObstacleAvoidanceEnabled(mqttAndroidClient, message);
                break;
            //设置偏航俯仰速度
            case Constant.SET_GIMBAL_SPEED:
                GimbalManager.getInstance().setGimbalSpeed(mqttAndroidClient, message);
                break;
            //恢复出厂
            case Constant.RESTORE_FACTORY:
                GimbalManager.getInstance().restoreFactorySettings(mqttAndroidClient, message);
                break;
            //自动校准
            case Constant.START_CALIBRATION:
                GimbalManager.getInstance().startCalibration(mqttAndroidClient, message);
                break;
            //云台偏航缓启停(0,30)
            case Constant.SET_CONTROLLER_SMOOTHING:
                GimbalManager.getInstance().setControllerSmoothingFactor(mqttAndroidClient, message);
                break;
            //rtk开关
            case Constant.SET_RTK:
                RTKManager.getInstance().setRtkEnabled(mqttAndroidClient, message);
                break;
            //rtk状态保持(4.16)
            case Constant.SET_RTK_MAINTAIN_POSITION:
                RTKManager.getInstance().setRTKMaintainPositioningAccuracyModeEnabled(mqttAndroidClient, message);
                break;
            //开始搜索基站
            case Constant.START_SET_BS:
                RTKManager.getInstance().startSearchBaseStation(mqttAndroidClient, message);
                break;
            //结束搜索基站
            case Constant.STOP_SET_BS:
                RTKManager.getInstance().stopSearchBaseStation(mqttAndroidClient, message);
                break;
            //连接基站
            case Constant.CONNECT_BS:
                RTKManager.getInstance().connectToBaseStation(mqttAndroidClient, message);
                break;
            //设置参考站源
            case Constant.SET_RSS:
                RTKManager.getInstance().setReferenceStationSource(mqttAndroidClient, message);
                break;
            //连接网络RTK
            case Constant.SET_RTK_NETWORK:
                RTKManager.getInstance().startNetworkService(mqttAndroidClient, message);
                break;
            //设置云台限位扩展
            case Constant.PITCH_RANGE_EXTENSION:
                GimbalManager.getInstance().setPitchRangeExtensionEnabled(mqttAndroidClient, message);
                break;
            //设置工作频段
            case Constant.SET_FREQUENCY_BAND:
                AirLinkManager.getInstance().setFrequencyBand(mqttAndroidClient, message);
                break;
            //设置云台模式
            case Constant.SET_GIMBAL_MODE:
                GimbalManager.getInstance().setMode(mqttAndroidClient, message);
                break;
            //设置红外展示模式
            case Constant.SET_HY_DISPLAY_MODE:
                CameraManager.getInstance().setHyDisplayMode(mqttAndroidClient, message);
                break;
            //判断是否在飞
            case Constant.GET_IS_FLYING:
                FlightManager.getInstance().isFlying(mqttAndroidClient, message);
                break;
            //获取返航点经纬度
            case Constant.GET_GO_HOME_POSITION:
                FlightManager.getInstance().getSLongLat(mqttAndroidClient, message);
                break;
            //文本喊话
            case Constant.SEND_VOICE_COMMAND:
                SpeakerManager.getInstance().sendTTS2Payload(mqttAndroidClient, message);
                break;
            case Constant.SEND_VOICE_MP3:
                SpeakerManager.getInstance().sendMP32Payload(mqttAndroidClient, message);
                break;
            case Constant.END_VOICE:
                SpeakerManager.getInstance().sendTTSStop2Payload(mqttAndroidClient, message);
                break;
            case Constant.PHOTO_ALBUM:
                PlayBackManager.getInstance().photoAlbum(mqttAndroidClient, message);
                break;
            case Constant.EXIT_PLAY_BACK:
                PlayBackManager.getInstance().exitPlayback(mqttAndroidClient, message);
                break;
            case Constant.DOWNLOAD_PHOTO_BY_NAME:
                PlayBackManager.getInstance().downLoadPhoto(mqttAndroidClient, message);
                break;
            case Constant.DELETE_PHOTO:
                PlayBackManager.getInstance().deletePhoto(mqttAndroidClient, message);
                break;
            case Constant.GET_PREVIEW_BY_NAME:
                PlayBackManager.getInstance().downLoadPreview(mqttAndroidClient, message);
                break;
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        try {
            if (reconnect) {//重新订阅
                Logger.e("重新订阅");
                mqttAndroidClient.subscribe(MqttConfig.MQTT_FLIGHT_CONTROLLER_TOPIC, 1);//订阅主题:飞控            publish(topic,"注册",0);
            }
        } catch (Exception e) {
            Logger.e("重新订阅失败");
        }
    }
}
