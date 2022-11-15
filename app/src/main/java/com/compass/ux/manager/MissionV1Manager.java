package com.compass.ux.manager;

import android.os.Handler;
import android.text.TextUtils;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.base.BaseManager;
import com.compass.ux.entity.MissionV2;
import com.compass.ux.tools.MapConvertUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointV2MissionOperator;

/**
 * 航线飞行任务V1(Mobile客户端控制)
 */
public class MissionV1Manager extends BaseManager {

    private List<Waypoint> waypointList = new ArrayList<>();

    public static WaypointMission.Builder waypointMissionBuilder;
    private WaypointMissionFinishedAction mFinishedAction = WaypointMissionFinishedAction.GO_HOME;
    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
//    private WaypointMissionHeadingMode mHeadingMode = WaypointMissionHeadingMode.AUTO;

    private MissionV1Manager() {
    }

    private static class WayPointMissionManagerHolder {
        private static final MissionV1Manager INSTANCE = new MissionV1Manager();
    }

    public static MissionV1Manager getInstance() {
        return MissionV1Manager.WayPointMissionManagerHolder.INSTANCE;
    }

    public void createWayPointMission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointMissionV1Operator().clearMission();
        if (waypointList != null) {
            waypointList.clear();
        }
        List<MissionV2.WayPointsBean> wayPoints = new Gson().fromJson(message.getPara().get("wayPoints"), new TypeToken<List<MissionV2.WayPointsBean>>() {
        }.getType());
        if (wayPoints!=null&&wayPoints.size()>0){
            for (int i = 0; i <wayPoints.size() ; i++) {
                Waypoint mWayoint=new Waypoint(Double.parseDouble(wayPoints.get(i).getLatitude()),Double.parseDouble(wayPoints.get(i).getLongitude()),Float.valueOf(wayPoints.get(i).getAltitude()));
                mWayoint.speed=Float.parseFloat(wayPoints.get(i).getSpeed());
                waypointList.add(mWayoint);
            }
            waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                    .headingMode(mHeadingMode)
                    .autoFlightSpeed(TextUtils.isEmpty(message.getPara().get("speed")) ? 15f : Float.parseFloat(message.getPara().get("speed")))
                    .maxFlightSpeed(TextUtils.isEmpty(message.getPara().get("speed")) ? 15f : Float.parseFloat(message.getPara().get("speed")))
                    .finishedAction(WaypointMissionFinishedAction.GO_HOME)
                    .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
            waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
        }else {
            sendErrorMsg2Server(mqttAndroidClient, message, "loadMission fail：" + "Wrong parameter");
        }
        DJIError error = getWaypointMissionV1Operator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
            Logger.e("loadWaypoint succeeded");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadWayPointMission(mqttAndroidClient,message);
                }
            },1000);
        } else {
            Logger.e("loadWaypoint failed " + error.getDescription());
        }

    }

    public void uploadWayPointMission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {

        getWaypointMissionV1Operator().uploadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            startWaypointMission(mqttAndroidClient,message);
//                        }
//                    }, 2000);
                    sendCorrectMsg2Server(mqttAndroidClient,message,"航线V1上传成功");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient,message,"上传V1航线失败:"+error.getDescription());
                    getWaypointMissionV1Operator().retryUploadMission(null);
                }
            }
        });

    }

    public void startWaypointMission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointMissionV1Operator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient,message,"开始航线任务成功");
                    Logger.e("startMission success");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient,message,"任务开始失败:"+error.getDescription());
                    Logger.e("startMission fail" + error.getDescription());
                }
            }
        });
    }

    public void stopWaypointMission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointMissionV1Operator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient,message,"航线任务已终止");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient,message,"任务终止失败:"+error.getDescription());
                }
            }
        });
    }

    public void resumeWaypointMission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointMissionV1Operator().resumeMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient,message,"航线任务已恢复");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient,message,"任务恢复失败:"+error.getDescription());
                }
            }
        });
    }

    public void pauseWaypointMission(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        getWaypointMissionV1Operator().pauseMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    sendCorrectMsg2Server(mqttAndroidClient,message,"航线任务已暂停");
                } else {
                    sendErrorMsg2Server(mqttAndroidClient,message,"任务暂停失败:"+error.getDescription());
                }
            }
        });
    }
    private WaypointMissionOperator waypointMissionOperator;

    // 获取航点任务操作器V1
    private  WaypointMissionOperator getWaypointMissionV1Operator() {
        if (waypointMissionOperator == null) {
            waypointMissionOperator=MissionControl.getInstance().getWaypointMissionOperator();
        }
        return waypointMissionOperator;
    }


}
