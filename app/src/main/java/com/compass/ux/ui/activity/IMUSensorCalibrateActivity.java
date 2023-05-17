package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.compass.ux.api.BaseUrl;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityEquipmentDetailsBinding;
import com.compass.ux.databinding.ActivityImuSensorCalibrateBinding;
import com.compass.ux.entity.EquipmentDetailsData;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;

import java.util.HashSet;

import dji.common.flightcontroller.CalibrationOrientation;
import dji.common.flightcontroller.imu.IMUState;
import dji.common.flightcontroller.imu.MultipleOrientationCalibrationHint;
import dji.common.flightcontroller.imu.OrientationCalibrationState;
import dji.sdk.flightcontroller.FlightController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IMUSensorCalibrateActivity extends BaseActivity {
    private ActivityImuSensorCalibrateBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ActivityImuSensorCalibrateBinding.class);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityImuSensorCalibrateBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    private void initView() {
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setIMUStateCallback(new IMUState.Callback() {
                @Override
                public void onUpdate(@NonNull IMUState imuState) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            imuState.getCurrentSideStatus()
                            mBinding.isbCalibrate.setProgress(Math.abs(imuState.getCalibrationProgress()));
                            switch (imuState.getCalibrationState()) {
                                case NONE:
                                    break;
                                case CALIBRATING:
                                    HashSet<CalibrationOrientation> orientationsCalibrated = imuState.getMultipleOrientationCalibrationHint().getOrientationsCalibrated();
                                    if (orientationsCalibrated.contains(CalibrationOrientation.LEFT_DOWN)){

                                    }

                                    //                                    switch (imuState.getMultipleOrientationCalibrationHint().getOrientationsCalibrated()) {
//                                        case CalibrationOrientation
//                                                .BOTTOM_DOWN:
//
//                                            break;
//                                    }
                                    break;
                                case SUCCESSFUL:

                                    break;
                                case FAILED:

                                    break;
                            }

                        }
                    });
                }
            });
        }
    }

}
