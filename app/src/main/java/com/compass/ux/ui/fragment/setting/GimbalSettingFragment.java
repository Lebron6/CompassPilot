package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.GimbalBStateCallBack;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.constant.Constant;
import com.compass.ux.databinding.FragmentBatteryInfoBinding;
import com.compass.ux.databinding.FragmentGimbalSettingBinding;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.window.DisconnectActionWindow;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.gimbal.GimbalMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;


/**
 * 云台设置
 */
public class GimbalSettingFragment extends BaseFragment {

    FragmentGimbalSettingBinding mBinding;
    private List<String> times = new ArrayList<>();
    private ArrayAdapter arrayAdapter;

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentGimbalSettingBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }


    @Override
    protected void initDatas() {
        times.add("自由模式");
        times.add("FPV");
        times.add("跟随模式");
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_question, R.id.tv_popqusetion, times);

        mBinding.layoutShowWindow.setOnClickListener(onClickListener);
        mBinding.sbGimbalMode.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.gimbalCalibration.setOnClickListener(onClickListener);
        mBinding.gimbalReset.setOnClickListener(onClickListener);
        if (Helper.isFlightControllerAvailable()) {
            Aircraft aircraft = ApronApp.getAircraftInstance();
            Gimbal gimbal = null;
            List<Gimbal> gimbals = aircraft.getGimbals();
            if (gimbals != null) {
                gimbal = gimbals.get(0);
            } else {
                gimbal = aircraft.getGimbal();
            }
            if (gimbal != null) {
                switch (LocalSource.getInstance().getGimbalMode()) {
                    case 0:
                        mBinding.tvGimbalMode.setText("自由模式");
                        break;
                    case 1:
                        mBinding.tvGimbalMode.setText("FPV");
                        break;
                    case 2:
                        mBinding.tvGimbalMode.setText("跟随模式");
                        break;
                }
                //云台俯仰限位扩展
                gimbal.getPitchRangeExtensionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {                        if (getActivity()!=null){

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbGimbalMode.setChecked(aBoolean);

                            }
                        });}
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            } else {
                ToastUtil.showToast("未检测到云台固件");
            }
        } else {
            ToastUtil.showToast("飞行器未连接");
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_show_window:
                    DisconnectActionWindow timeSelectWindow = new DisconnectActionWindow(getActivity());
                    timeSelectWindow.showView(mBinding.layoutShowWindow, arrayAdapter, listener);
                    break;
                case R.id.gimbal_calibration:
                    Aircraft aircraft = ApronApp.getAircraftInstance();
                    if (aircraft != null) {
                        Gimbal gimbal = null;
                        List<Gimbal> gimbals = aircraft.getGimbals();
                        if (gimbals != null) {
                            gimbal = gimbals.get(0);
                        } else {
                            gimbal = aircraft.getGimbal();
                        }
                        if (gimbal != null) {
                            gimbal.startCalibration(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {                        if (getActivity()!=null) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError == null) {
                                                ToastUtil.showToast("云台开始校准");
                                            } else {
                                                ToastUtil.showToast("云台校准失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                }
                                }
                            });
                        } else {
                            ToastUtil.showToast("未检测到云台固件");
                        }
                    } else {
                        ToastUtil.showToast("未检测到飞机连接");
                    }
                    break;
                case R.id.gimbal_reset:
                    Aircraft aircraft1 = ApronApp.getAircraftInstance();
                    if (aircraft1 != null) {
                        Gimbal gimbal = null;
                        List<Gimbal> gimbals = aircraft1.getGimbals();
                        if (gimbals != null) {
                            gimbal = gimbals.get(0);
                        } else {
                            gimbal = aircraft1.getGimbal();
                        }
                        if (gimbal != null) {
                            gimbal.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {                        if (getActivity()!=null) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError == null) {
                                                ToastUtil.showToast("已恢复出厂设置");
                                            } else {
                                                ToastUtil.showToast("恢复出厂设置失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                }
                                }
                            });
                        } else {
                            ToastUtil.showToast("未检测到云台固件");
                        }
                    } else {
                        ToastUtil.showToast("未检测到飞机连接");
                    }
                    break;
            }
        }
    };
    OnDisconnectActionSelectedListener listener = new OnDisconnectActionSelectedListener() {
        @Override
        public void select(int postion) {
            mBinding.tvGimbalMode.setText(times.get(postion));
            if (Helper.isFlightControllerAvailable()) {
                Aircraft aircraft = ApronApp.getAircraftInstance();
                Gimbal gimbal = null;
                List<Gimbal> gimbals = aircraft.getGimbals();
                if (gimbals != null) {
                    gimbal = gimbals.get(0);
                } else {
                    gimbal = aircraft.getGimbal();
                }
                if (gimbal != null) {
                    gimbal.setMode(GimbalMode.find(postion), new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {                        if (getActivity()!=null) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (djiError == null) {
                                        LocalSource.getInstance().setGimbalMode(postion);
                                        ToastUtil.showToast("云台模式已变更");
                                    } else {
                                        ToastUtil.showToast("云台模式变更失败");
                                    }
                                }
                            });
                        }
                        }
                    });
                } else {
                    ToastUtil.showToast("未检测到云台固件");
                }
            } else {
                ToastUtil.showToast("飞行器未连接");
            }
        }
    };

    SwitchButton.OnCheckedChangeListener onCheckedChangeListener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            if (Helper.isFlightControllerAvailable()) {
                Aircraft aircraft = ApronApp.getAircraftInstance();
                Gimbal gimbal = null;
                List<Gimbal> gimbals = aircraft.getGimbals();
                if (gimbals != null) {
                    gimbal = gimbals.get(0);
                } else {
                    gimbal = aircraft.getGimbal();
                }
                if (gimbal != null) {
                    switch (view.getId()) {
                        case R.id.sb_gimbal_mode:
                            gimbal.setPitchRangeExtensionEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {                        if (getActivity()!=null) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError == null) {
                                                ToastUtil.showToast("云台俯仰限位扩展已变更");
                                                LocalSource.getInstance().setPitchRangeExtension(isChecked);
                                            } else {
                                                ToastUtil.showToast("云台俯仰限位扩展变更失败：" + djiError.getDescription());
                                            }
                                        }
                                    });
                                }
                                }
                            });
                            break;
                    }
                } else {
                    ToastUtil.showToast("云台未连接");
                }
            } else {
                ToastUtil.showToast("飞行器未连接或固件不支持");
            }

        }
    };
}
