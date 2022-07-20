package com.compass.ux.manager;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.tools.DJIWaypointV2ErrorMessageUtils;
import com.google.gson.Gson;

import android.text.TextUtils;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.WaypointV2ActionListener;
import com.compass.ux.callback.WaypointV2MissionOperatorListener;
import com.compass.ux.constant.Constant;
import com.compass.ux.entity.MissionV2;
import com.compass.ux.entity.Communication;
import com.compass.ux.tools.MapConvertUtils;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import dji.common.error.DJIWaypointV2Error;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.mission.waypointv2.Action.ActionState;
import dji.common.mission.waypointv2.Action.ActionTypes;
import dji.common.mission.waypointv2.Action.InterruptRecoverActionType;
import dji.common.mission.waypointv2.Action.WaypointActuator;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlRotateYawParam;
import dji.common.mission.waypointv2.Action.WaypointAircraftControlStartStopFlyParam;
import dji.common.mission.waypointv2.Action.WaypointCameraActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointCameraZoomParam;
import dji.common.mission.waypointv2.Action.WaypointGimbalActuatorParam;
import dji.common.mission.waypointv2.Action.WaypointReachPointTriggerParam;
import dji.common.mission.waypointv2.Action.WaypointTrigger;
import dji.common.mission.waypointv2.Action.WaypointV2Action;
import dji.common.mission.waypointv2.Action.WaypointV2AssociateTriggerParam;
import dji.common.mission.waypointv2.WaypointV2;
import dji.common.mission.waypointv2.WaypointV2Mission;
import dji.common.mission.waypointv2.WaypointV2MissionState;
import dji.common.mission.waypointv2.WaypointV2MissionTypes;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointV2MissionOperator;

import static com.compass.ux.tools.Utils.getbigZoomValue;

import org.eclipse.paho.android.service.MqttAndroidClient;

/**
 * 航线飞行任务v2
 */
public class MissionManager extends BaseManager {

    private WaypointV2MissionOperator waypointV2MissionOperator;

    private MissionManager() {
    }

    private static class WayPointMissionManagerHolder {
        private static final MissionManager INSTANCE = new MissionManager();
    }

    public static MissionManager getInstance() {
        return WayPointMissionManagerHolder.INSTANCE;
    }

    public void initMissionInfo(MqttAndroidClient client) {
        if (getWaypointV2MissionOperator() != null) {
            getWaypointV2MissionOperator().addActionListener(new WaypointV2ActionListener());
            getWaypointV2MissionOperator().addWaypointEventListener(new WaypointV2MissionOperatorListener(client));
        }
    }

    public WaypointV2MissionOperator getWaypointV2MissionOperator() {
        if (waypointV2MissionOperator == null) {
            waypointV2MissionOperator = MissionControl.getInstance().getWaypointMissionV2Operator();
        }
        return waypointV2MissionOperator;
    }

    //开始航线任务
    public void startType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
        }
        startWaypointsV2Mission(message, mqttAndroidClient);
    }

    //继续航线任务(只有航线任务状态为INTERRUPTED时可调用)
    public void continueType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointV2MissionOperator().recoverMission(InterruptRecoverActionType.find(1), new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient, message, "recoverMission success：" + "The mission has been recovered");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "recoverMission fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                }
            }
        });
    }

    //暂停航线任务(只有航线任务状态为EXECUTING时可调用)
    public void suspendType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointV2MissionOperator().interruptMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient, message, "interruptMission success：" + "The mission has been interrupted");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "interruptMission fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                }
            }
        });

    }

    //结束航线任务(只有航线任务状态为EXECUTING或者INTERRUPTED时可调用)
    public void endType(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointV2MissionOperator().stopMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient, message, "stopMission success：" + "The mission has been stopped");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "stopMission fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                }
            }
        });
    }


    public void createWayPointV2Mission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        List<MissionV2.WayPointsBean> wayPoints = new Gson().fromJson(message.getPara().get("wayPoints"), new TypeToken<List<MissionV2.WayPointsBean>>() {
        }.getType());
        if (wayPoints != null && wayPoints.size() > 0) {
            List<WaypointV2> waypointV2List = new ArrayList<>();
            for (int i = 0; i < wayPoints.size(); i++) {
                double[] latLng = MapConvertUtils.getDJILatLng(Double.parseDouble(wayPoints.get(i).getLatitude()),
                        Double.parseDouble(wayPoints.get(i).getLongitude()));
                WaypointV2 waypoint = new WaypointV2.Builder()
                        .setDampingDistance(20f)
                        .setCoordinate(new LocationCoordinate2D(latLng[0], latLng[1]))
                        .setAltitude(Double.valueOf(wayPoints.get(i).getAltitude()))
                        .setFlightPathMode(WaypointV2MissionTypes.WaypointV2FlightPathMode.find(Integer.parseInt(wayPoints.get(i).getFlightPathMode())))
                        .setHeadingMode(WaypointV2MissionTypes.WaypointV2HeadingMode.find(Integer.parseInt(wayPoints.get(i).getHeadingMode())))
                        .setTurnMode(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(wayPoints.get(i).getTurnMode())))
                        .setAutoFlightSpeed(Float.parseFloat(wayPoints.get(i).getSpeed()))
                        .build();
                waypointV2List.add(waypoint);
            }
            WaypointV2Mission.Builder waypointV2MissionBuilder = new WaypointV2Mission.Builder();
            waypointV2MissionBuilder.setMissionID(new Random().nextInt(65535))
                    .setMaxFlightSpeed(TextUtils.isEmpty(message.getPara().get("speed")) ? 15f : Float.parseFloat(message.getPara().get("speed")))
                    .setAutoFlightSpeed(TextUtils.isEmpty(message.getPara().get("speed")) ? 15f : Float.parseFloat(message.getPara().get("speed")))
                    .setFinishedAction(WaypointV2MissionTypes.MissionFinishedAction.GO_HOME)
                    .setGotoFirstWaypointMode(WaypointV2MissionTypes.MissionGotoWaypointMode.SAFELY)
                    .setExitMissionOnRCSignalLostEnabled(true)
                    .setRepeatTimes(1)
                    .addwaypoints(waypointV2List);
            WaypointV2Mission waypointV2Mission = waypointV2MissionBuilder.build();
            //加载
            getWaypointV2MissionOperator().loadMission(waypointV2Mission, new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    if (djiWaypointV2Error == null) {
                        uploadWayPointV2Mission(message, mqttAndroidClient);
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient, message, "loadMission fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                    }
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "loadMission fail：" + "Wrong parameter");
        }
    }

    //上传航点任务
    private void uploadWayPointV2Mission(ProtoMessage.Message message, MqttAndroidClient mqttAndroidClient) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
        }
        getWaypointV2MissionOperator().uploadMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    uploadWayPointV2Action(message, mqttAndroidClient);
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "uploadMission fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                }
            }
        });
    }

    private WaypointTrigger waypointActionTrigger;
    private WaypointActuator waypointActionActuator;
    private int actionID;

    //上传航点动作
    private void uploadWayPointV2Action(ProtoMessage.Message message, MqttAndroidClient mqttAndroidClient) {
        List<MissionV2.WayPointsBean> wayPoints = new Gson().fromJson(message.getPara().get("wayPoints"), new TypeToken<List<MissionV2.WayPointsBean>>() {
        }.getType());
        List<WaypointV2Action> actionList = new ArrayList<>();
        for (int i = 0; i < wayPoints.size(); i++) {
            MissionV2.WayPointsBean wayPoint = wayPoints.get(i);
            List<MissionV2.WayPointsBean.WayPointActionBean> wayPointActions = wayPoint.getWayPointAction();
            for (int k = 0; k < wayPointActions.size(); k++) {
                MissionV2.WayPointsBean.WayPointActionBean action = wayPointActions.get(k);
                if ("0".equals(action.getActionType())) {
                    wayPointActions.remove(k);
                    wayPointActions.add(0, action);
                    break;
                }
            }
            for (int j = 0; j < wayPointActions.size(); j++) {
                actionID += 1;
                MissionV2.WayPointsBean.WayPointActionBean pointAction = wayPointActions.get(j);
                if (j == 0) {
                    waypointActionTrigger = new WaypointTrigger.Builder()
                            .setTriggerType(ActionTypes.ActionTriggerType.REACH_POINT)
                            .setReachPointParam(new WaypointReachPointTriggerParam.Builder()
                                    .setStartIndex(i)
                                    .setAutoTerminateCount(i)
                                    .build())
                            .build();
                } else {
                    waypointActionTrigger = new WaypointTrigger.Builder()
                            .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                            .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
                                    .setAssociateActionID(actionID - 1)
                                    .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
                                    .setWaitingTime(Float.parseFloat(pointAction.getWaitingTime() == null ? "0" : pointAction.getWaitingTime()))
                                    .build())
                            .build();
                }
                switch (pointAction.getActionType()) {
                    case "0"://悬停
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                        .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                        .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                .setStartFly(false)
                                                .build())
                                        .build())
                                .build();
                        break;
                    case "1"://继续飞行
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                        .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                        .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                                .setStartFly(true)
                                                .build())
                                        .build())
                                .build();
                        break;
                    case "2"://旋转
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                                .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                        .setAircraftControlType(ActionTypes.AircraftControlType.ROTATE_YAW)
                                        .setRotateYawParam(new WaypointAircraftControlRotateYawParam.Builder()
                                                .setYawAngle(Float.parseFloat(pointAction.getYawAngle()))
                                                .setDirection(WaypointV2MissionTypes.WaypointV2TurnMode.find(Integer.parseInt(pointAction.getDirection())))
//                                            .setRelative()
                                                .build())
                                        .build())
                                .build();
                        break;
                    case "3"://云台
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.GIMBAL)
                                .setGimbalActuatorParam(new WaypointGimbalActuatorParam.Builder()
                                        .operationType(ActionTypes.GimbalOperationType.ROTATE_GIMBAL)
                                        .rotation(new Rotation.Builder()
                                                .mode(RotationMode.ABSOLUTE_ANGLE)
                                                .pitch(Float.parseFloat(pointAction.getPitch() == null ? "0" : pointAction.getPitch()))
                                                .time(3)
                                                .build())
                                        .build())
                                .build();
                        break;
                    case "4"://变焦
                        //修改变焦数据为从前端拿2-200自己计算然后放入官方的sdk
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                        .setCameraOperationType(ActionTypes.CameraOperationType.ZOOM)
                                        .setZoomParam(new WaypointCameraZoomParam.Builder()
                                                .setFocalLength(getbigZoomValue(pointAction.getFocalLength()))
                                                .build())
                                        .build())
                                .build();
                        break;
                    case "5"://拍照
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                        .setCameraOperationType(ActionTypes.CameraOperationType.SHOOT_SINGLE_PHOTO)
                                        .build())
                                .build();
                        break;
                    case "6"://开始录像
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                        .setCameraOperationType(ActionTypes.CameraOperationType.START_RECORD_VIDEO)
                                        .build())
                                .build();
                        break;
                    case "7"://结束录像
                        waypointActionActuator = new WaypointActuator.Builder()
                                .setActuatorType(ActionTypes.ActionActuatorType.CAMERA)
                                .setCameraActuatorParam(new WaypointCameraActuatorParam.Builder()
                                        .setCameraOperationType(ActionTypes.CameraOperationType.STOP_RECORD_VIDEO)
                                        .build())
                                .build();
                        break;
                }
                WaypointV2Action waypointAction = new WaypointV2Action.Builder()
                        .setActionID(actionID)
                        .setTrigger(waypointActionTrigger)
                        .setActuator(waypointActionActuator)
                        .build();
                actionList.add(waypointAction);
                //如果是悬停并且当前是航点的最后一个动作
                if ("0".equals(wayPoint.getWayPointAction().get(0).getActionType()) && j == wayPoint.getWayPointAction().size() - 1) {
                    actionID++;
                    waypointActionTrigger = new WaypointTrigger.Builder()
                            .setTriggerType(ActionTypes.ActionTriggerType.ASSOCIATE)
                            .setAssociateParam(new WaypointV2AssociateTriggerParam.Builder()
                                    .setAssociateActionID(actionID - 1)
                                    .setAssociateType(ActionTypes.AssociatedTimingType.AFTER_FINISHED)
                                    .setWaitingTime(Float.parseFloat(
                                            wayPoint.getWayPointAction().get(0).getWaitingTime()))
                                    .build())
                            .build();
                    waypointActionActuator = new WaypointActuator.Builder()
                            .setActuatorType(ActionTypes.ActionActuatorType.AIRCRAFT_CONTROL)
                            .setAircraftControlActuatorParam(new WaypointAircraftControlParam.Builder()
                                    .setAircraftControlType(ActionTypes.AircraftControlType.START_STOP_FLY)
                                    .setFlyControlParam(new WaypointAircraftControlStartStopFlyParam.Builder()
                                            .setStartFly(true)
                                            .build())
                                    .build())
                            .build();

                    WaypointV2Action waypointAction0 = new WaypointV2Action.Builder()
                            .setActionID(actionID)//0会报错sdkbug
                            .setTrigger(waypointActionTrigger)
                            .setActuator(waypointActionActuator)
                            .build();
                    actionList.add(waypointAction0);
                }
            }
        }

        if (actionList==null||actionList.size()==0){
            sendCorrectMsg2Server(mqttAndroidClient, message, "uploadAction success：" + "Can start mission");
        }else{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getWaypointV2MissionOperator().uploadWaypointActions(actionList, new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
                @Override
                public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                    if (djiWaypointV2Error == null) {
                        sendCorrectMsg2Server(mqttAndroidClient, message, "uploadAction success：" + "Can start mission");
//                            startWaypointsV2Mission(message);
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient, message, "uploadAction fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                    }
                }
            });
        }

    }

    /*******************************************************************************************************************************/

    private void startWaypointsV2Mission(ProtoMessage.Message message, MqttAndroidClient mqttAndroidClient) {
        getWaypointV2MissionOperator().startMission(new CommonCallbacks.CompletionCallback<DJIWaypointV2Error>() {
            @Override
            public void onResult(DJIWaypointV2Error djiWaypointV2Error) {
                if (djiWaypointV2Error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient, message, "startMission success");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient, message, "startMission fail：" + DJIWaypointV2ErrorMessageUtils.getDJIWaypointV2ErrorMsg(djiWaypointV2Error));
                }
            }
        });
    }
}
