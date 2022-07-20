package com.compass.ux.callback;

import androidx.annotation.NonNull;

import dji.common.camera.LaserMeasureInformation;
import dji.common.camera.StorageState;

/**
 * 激光测距，存储信息
 */
public class CameraStateCallBack implements LaserMeasureInformation.Callback, StorageState.Callback {

    //激光测距
    @Override
    public void onUpdate(LaserMeasureInformation laserMeasureInformation) {

    }

    //存储
    @Override
    public void onUpdate(@NonNull StorageState storageState) {

    }
}
