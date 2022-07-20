package com.compass.ux.callback;


import com.compass.ux.base.BaseManager;
import com.compass.ux.entity.Communication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dji.common.error.DJIWaypointV2Error;
import dji.common.mission.waypointv2.Action.ActionDownloadEvent;
import dji.common.mission.waypointv2.Action.ActionExecutionEvent;
import dji.common.mission.waypointv2.Action.ActionUploadEvent;

public class WaypointV2ActionListener extends BaseManager implements dji.sdk.mission.waypoint.WaypointV2ActionListener {

    Communication message;

    public WaypointV2ActionListener() {
    }

    public WaypointV2ActionListener(Communication message) {
        this.message = message;
    }

    @Override
    public void onDownloadUpdate(@NonNull ActionDownloadEvent actionDownloadEvent) {

    }

    @Override
    public void onUploadUpdate(@NonNull ActionUploadEvent actionUploadEvent) {
//        if (actionUploadEvent.getCurrentState().equals(ActionState.READY_TO_UPLOAD)) {
//            uploadWaypointAction();
//        }
//        if (actionUploadEvent.getPreviousState() == ActionState.UPLOADING
//                && actionUploadEvent.getCurrentState() == ActionState.READY_TO_EXECUTE) {
//            sendCorrectMsg2Server(message, "action listener: canStartMission");
//        }
    }


    @Override
    public void onExecutionUpdate(@NonNull ActionExecutionEvent actionExecutionEvent) {

    }

    @Override
    public void onExecutionStart(int i) {

    }

    @Override
    public void onExecutionFinish(int i, @Nullable DJIWaypointV2Error djiWaypointV2Error) {

    }



    private void uploadWaypointAction() {

    }


}
