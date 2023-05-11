package com.compass.ux.ui.fragment.setting.sensor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentBatteryInfoBinding;
import com.compass.ux.databinding.FragmentSensorStatusBinding;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.activity.IMUSensorCalibrateActivity;

import dji.common.error.DJIError;
import dji.common.flightcontroller.CompassState;
import dji.common.flightcontroller.imu.IMUState;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;


/**
 * 传感器状态
 */
public class SensorStatusFragment extends BaseFragment {

    FragmentSensorStatusBinding mBinding;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSensorStatusBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }

    @Override
    protected void initDatas() {
        mBinding.rgHandleType.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.layoutCalibrateCompass.setOnClickListener(onClickListener);
        mBinding.layoutCalibrateIMU.setOnClickListener(onClickListener);

        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.setIMUStateCallback(new IMUState.Callback() {
                @Override
                public void onUpdate(@NonNull IMUState imuState) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (imuState.getIndex()){
                                    case 0:
                                        mBinding.tvImuOneSpeed.setText(imuState.getAccelerometerValue()+"");
                                        mBinding.tvGyroscopeOne.setText(imuState.getGyroscopeValue()+"");
                                        break;
                                    case 1:
                                        mBinding.tvImuTwoSpeed.setText(imuState.getAccelerometerValue()+"");
                                        mBinding.tvGyroscopeTwo.setText(imuState.getGyroscopeValue()+"");
                                        break;
                                }
                            }
                        });
                    }
                }
            });

            flightController.getCompass().setCompassStateCallback(new CompassState.Callback() {
                @Override
                public void onUpdate(@NonNull CompassState compassState) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.tvCompassGr.setText(compassState.getSensorValue()+"");
                            }
                        });
                    }
                }
            });

        } else {
            ToastUtil.showToast("未检测到设备");
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_calibrate_compass:
                    if (Helper.isFlightControllerAvailable()) {
                        FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                        flightController.getCompass().startCalibration(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showToast("开始指南针校准");
                                            }
                                        });
                                    }
                                } else {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showToast("开始指南针校准失败");
                                            }
                                        });
                                    }
                                }
                            }
                        });

                    } else {
                        ToastUtil.showToast("未检测到设备");
                    }
                    break;
                case R.id.layout_calibrate_IMU:
                    if (Helper.isFlightControllerAvailable()) {
                        FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                        flightController.startIMUCalibration(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showToast("开始IMU校准");
                                            }
                                        });
                                    }
                                } else {
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ToastUtil.showToast("开始IMU校准失败");
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    } else {
                        ToastUtil.showToast("未检测到设备");
                    }
//                    if (getActivity()!=null){
//                        IMUSensorCalibrateActivity.actionStart(getActivity());
//
//                    }
                    break;
            }
        }
    };

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_j:
                    mBinding.layoutImu.setVisibility(View.VISIBLE);
                    mBinding.layoutZnz.setVisibility(View.GONE);
                    break;
                case R.id.rb_c:
                    mBinding.layoutImu.setVisibility(View.GONE);
                    mBinding.layoutZnz.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

}
