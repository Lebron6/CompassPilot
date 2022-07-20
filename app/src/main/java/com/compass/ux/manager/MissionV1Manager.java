package com.compass.ux.manager;

import android.os.Handler;

import com.compass.ux.tools.MapConvertUtils;
import com.orhanobut.logger.Logger;

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
public class MissionV1Manager {

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

    public void createWayPointMission() {
        getWaypointMissionV1Operator().clearMission();
        if (waypointList != null) {
            waypointList.clear();
        }
        Waypoint mWaypoint1 = new Waypoint(120.6383088098564, 31.282999077497713, 80);
        Waypoint mWaypoint2 = new Waypoint(120.6383088098564, 31.283132027935732, 80);
        Waypoint mWaypoint3 = new Waypoint(120.63829271660231, 31.283223717783724, 80);
        Waypoint mWaypoint4 = new Waypoint(120.63845901356123, 31.28319621083872, 80);
//        waypointList.add(mWaypoint1);
//        waypointList.add(mWaypoint2);
        waypointList.add(mWaypoint3);
        waypointList.add(mWaypoint4);
        waypointMissionBuilder = new WaypointMission.Builder().finishedAction(mFinishedAction)
                .headingMode(mHeadingMode)
                .autoFlightSpeed(6f)
                .maxFlightSpeed(6f)
                .finishedAction(WaypointMissionFinishedAction.GO_HOME)
                .flightPathMode(WaypointMissionFlightPathMode.NORMAL);
        waypointMissionBuilder.waypointList(waypointList).waypointCount(waypointList.size());
        DJIError error = getWaypointMissionV1Operator().loadMission(waypointMissionBuilder.build());
        if (error == null) {
            Logger.e("loadWaypoint succeeded");
            uploadWayPointMission();
        } else {
            Logger.e("loadWaypoint failed " + error.getDescription());
        }

    }

    public void uploadWayPointMission() {

        getWaypointMissionV1Operator().uploadMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startWaypointMission();
                        }
                    }, 2000);
                } else {
                    getWaypointMissionV1Operator().retryUploadMission(null);
                }
            }
        });

    }

    public void startWaypointMission() {
        getWaypointMissionV1Operator().startMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (error == null) {
                    Logger.e("startMission success");
                } else {
                    Logger.e("startMission fail" + error.getDescription());
                }
            }
        });
    }

    public void stopWaypointMission() {
        getWaypointMissionV1Operator().stopMission(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
//                Toast.makeText(this,"aircraft disconnect!",Toast.LENGTH_SHORT).show();
//
//                ToastUtil.showToast("Mission Stop: " + (error == null ? "Successfully" : error.getDescription()));
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
