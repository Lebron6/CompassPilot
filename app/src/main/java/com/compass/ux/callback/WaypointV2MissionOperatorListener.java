package com.compass.ux.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.apron.mobilesdk.state.ProtoMissionExecution;
import com.compass.ux.base.BaseCallback;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.DataCache;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import dji.common.error.DJIWaypointV2Error;
import dji.common.mission.waypointv2.WaypointV2MissionDownloadEvent;
import dji.common.mission.waypointv2.WaypointV2MissionExecutionEvent;
import dji.common.mission.waypointv2.WaypointV2MissionUploadEvent;

public class WaypointV2MissionOperatorListener extends BaseCallback implements dji.sdk.mission.waypoint.WaypointV2MissionOperatorListener {

    MqttAndroidClient client;

    public WaypointV2MissionOperatorListener(MqttAndroidClient client) {
        this.client = client;
    }


    @Override
    public void onDownloadUpdate(@NonNull WaypointV2MissionDownloadEvent waypointV2MissionDownloadEvent) {

    }

    @Override
    public void onUploadUpdate(@NonNull WaypointV2MissionUploadEvent waypointV2MissionUploadEvent) {
        if (waypointV2MissionUploadEvent.getError() != null) {
            Logger.e("onUploadUpdate：" + waypointV2MissionUploadEvent.getError().getDescription());
            XcFileLog.getInstace().i("onUploadUpdate：", waypointV2MissionUploadEvent.getError().getDescription());
        }
    }

    @Override
    public void onExecutionUpdate(@NonNull WaypointV2MissionExecutionEvent event) {
        if (event!=null&&event.getProgress()!=null){
            //更新客户端缓存航线执行状态
            DataCache.getInstance().setMissionExecuteState(event.getProgress().getExecuteState().ordinal());
            DataCache.getInstance().setTargetWaypointIndex(event.getProgress().getTargetWaypointIndex());
        }
        if (event.getError()!=null){
            XcFileLog.getInstace().i("onExecutionUpdate：", event.getError().getDescription());
        }
    }

    @Override
    public void onExecutionStart() {

    }

    @Override
    public void onExecutionFinish(@Nullable DJIWaypointV2Error djiWaypointV2Error) {

    }

    @Override
    public void onExecutionStopped() {

    }



}
