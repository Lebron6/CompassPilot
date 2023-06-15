package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.BatteryAStateCallback;
import com.compass.ux.callback.BatteryBStateCallback;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.databinding.FragmentBatteryInfoBinding;
import com.compass.ux.databinding.FragmentGraphicBinding;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.fragment.setting.Battery.BatteryDetailsFragment;
import com.compass.ux.ui.fragment.setting.sensor.SensorStatusFragment;
import com.compass.ux.ui.window.DisconnectActionWindow;
import com.compass.ux.xclog.XcFileLog;
import com.suke.widget.SwitchButton;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.List;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.util.CommonCallbacks;
import dji.sdk.battery.Battery;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;


/**
 * 智能电池
 */
public class BatteryFragment extends BaseFragment {

    FragmentBatteryInfoBinding mBinding;
    private List<String> times = new ArrayList<>();
    private ArrayAdapter arrayAdapter;

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentBatteryInfoBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }

    @Override
    protected void initDatas() {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null && aircraft.isConnected()) {
            List<Battery> batteries = aircraft.getBatteries();
            if (batteries != null && batteries.size() > 0) {
                if (batteries.size() > 1) {
                    mBinding.layoutBatteryO.setVisibility(View.VISIBLE);
                    Battery batteryB = batteries.get(1);
                    batteryB.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvSnO.setText("序列号：" + s);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(DJIError djiError) {

                        }
                    });
                    batteryB.setStateCallback(new BatteryState.Callback() {
                        @Override
                        public void onUpdate(BatteryState batteryState) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvLowBatteryQuantityO.setText(batteryState.getChargeRemainingInPercent() + "%");
                                        mBinding.tvBatteryTemperatureO.setText(batteryState.getTemperature() + "℃");
                                        mBinding.tvBatteryVoltageO.setText(batteryState.getVoltage() + "mV");
                                        mBinding.tvBatteryCyclesO.setText(batteryState.getNumberOfDischarges() + "");
                                        switch (batteryState.getConnectionState()) {
                                            case NORMAL:
                                                mBinding.tvBatteryStatusO.setText("正常");
                                                break;
                                            case INVALID:
                                                mBinding.tvBatteryStatusO.setText("无效");
                                                break;
                                            case EXCEPTION:
                                                mBinding.tvBatteryStatusO.setText("异常");
                                                break;
                                        }
                                    }
                                });
                            }

                        }
                    });

                }
                Battery batteryA = batteries.get(0);
                batteryA.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.tvSn.setText("序列号：" + s);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                batteryA.getSelfDischargeInDays(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        if (getActivity() != null) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.tvAction.setText(integer + "天");
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                batteryA.setStateCallback(new BatteryState.Callback() {
                    @Override
                    public void onUpdate(BatteryState batteryState) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.tvLowBatteryQuantityOne.setText(batteryState.getChargeRemainingInPercent() + "%");
                                    mBinding.tvBatteryTemperature.setText(batteryState.getTemperature() + "℃");
                                    mBinding.tvBatteryVoltage.setText(batteryState.getVoltage() + "mV");
                                    mBinding.tvBatteryCycles.setText(batteryState.getNumberOfDischarges() + "");
//                                    switch (batteryState.getConnectionState()) {
//                                        case NORMAL:
                                            mBinding.tvBatteryStatus.setText("正常");
//                                            break;
//                                        case INVALID:
//                                            mBinding.tvBatteryStatus.setText("无效");
//                                            break;
//                                        case EXCEPTION:
//                                            mBinding.tvBatteryStatus.setText("异常");
//                                            break;
//                                    }
                                }
                            });
                        }

                    }
                });

            } else {
                ToastUtil.showToast("未检测到智能电池");
            }
        } else {
            ToastUtil.showToast("未检测到智能电池");
        }
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();

            flightController.getSmartReturnToHomeEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    if (getActivity() != null) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbSmartGoHome.setChecked(aBoolean);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            //低电量值，严重低电量
            flightController.getLowBatteryWarningThreshold(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    if (getActivity() != null) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbLow.setProgress(integer);

                            }
                        });
                    }
                    LocalSource.getInstance().setLowBatteryWarning(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getSeriousLowBatteryWarningThreshold(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    if (getActivity() != null) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbLowToo.setProgress(integer);
                            }
                        });
                    }
                    LocalSource.getInstance().setSeriousLowBatteryWarning(integer + "");
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

        } else {
            ToastUtil.showToast("未检测到飞控固件");
        }
        mBinding.sbSmartGoHome.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.sbLow.setOnSeekChangeListener(onSeekChangeListener);
        mBinding.sbLowToo.setOnSeekChangeListener(onSeekChangeListener);
        times.add("1天");
        times.add("5天");
        times.add("10天");
        times.add("15天");
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_question, R.id.tv_popqusetion, times);

        mBinding.layoutShowWindow.setOnClickListener(onClickListener);
        mBinding.layoutBatteryO.setOnClickListener(onClickListener);
        mBinding.layoutBatteryT.setOnClickListener(onClickListener);
    }

    OnSeekChangeListener onSeekChangeListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            switch (seekParams.seekBar.getId()) {
                case R.id.sb_low:
                    mBinding.tvLowBattery.setText(seekParams.progress + "%");
                    break;
                case R.id.sb_low_too:
                    mBinding.tvLowBatteryT.setText(seekParams.progress + "%");
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            switch (seekBar.getId()) {
                case R.id.sb_low:
                    if (Helper.isFlightControllerAvailable()) {
                        FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                        //低电量值，严重低电量
                        flightController.setLowBatteryWarningThreshold(seekBar.getProgress(), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (getActivity() != null) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置低电量阈值失败:" + djiError.getDescription());
                                            } else {
                                                ToastUtil.showToast("设置低电量阈值成功");

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        ToastUtil.showToast("未检测到设备连接");
                    }

                    break;
                case R.id.sb_low_too:
                    if (Helper.isFlightControllerAvailable()) {
                        FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                        //低电量值，严重低电量
                        flightController.setSeriousLowBatteryWarningThreshold(seekBar.getProgress(), new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (getActivity() != null) {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError != null) {
                                                ToastUtil.showToast("设置严重低电量阈值失败:" + djiError.getDescription());
                                            } else {
                                                ToastUtil.showToast("设置严重低电量阈值成功");

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        ToastUtil.showToast("未检测到设备连接");
                    }
                    break;
            }
        }
    };


    SwitchButton.OnCheckedChangeListener onCheckedChangeListener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            if (Helper.isFlightControllerAvailable()) {
                FlightController flightController = ApronApp.getAircraftInstance().getFlightController();

                flightController.setSmartReturnToHomeEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            if (getActivity() != null) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast("更改智能返航状态失败：" + djiError.getDescription());
                                    }
                                });
                            }
                        } else {
                            if (getActivity() != null) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast("更改智能返航状态成功");
                                    }
                                });
                            }
                        }
                    }
                });
            } else {
                ToastUtil.showToast("未检测到飞控固件");
            }

        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_show_window:
                    DisconnectActionWindow timeSelectWindow = new DisconnectActionWindow(getActivity());
                    timeSelectWindow.showView(mBinding.layoutShowWindow, arrayAdapter, listener);
                    break;
                case R.id.layout_battery_o:
                    Aircraft aircraft = ApronApp.getAircraftInstance();
                    if (aircraft != null && aircraft.isConnected()) {
                        List<Battery> batteries = aircraft.getBatteries();
                        if (batteries != null && batteries.size() > 0) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BatteryDetailsFragment(0)).commit();
                        }
                    }else{
                        ToastUtil.showToast("未检测到设备");
                    }
                    break;
                case R.id.layout_battery_t:
                    Aircraft aircraft1 = ApronApp.getAircraftInstance();
                    if (aircraft1 != null && aircraft1.isConnected()) {
                        List<Battery> batteries = aircraft1.getBatteries();
                        if (batteries != null && batteries.size() > 1) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new BatteryDetailsFragment(1)).commit();
                        }
                    }else{
                        ToastUtil.showToast("未检测到设备");
                    }
                    break;
            }
        }
    };

    OnDisconnectActionSelectedListener listener = new OnDisconnectActionSelectedListener() {
        @Override
        public void select(int postion) {
            mBinding.tvAction.setText(times.get(postion));
            Aircraft aircraft = ApronApp.getAircraftInstance();
            if (aircraft != null && aircraft.isConnected()) {
                List<Battery> batteries = aircraft.getBatteries();
                if (batteries != null && batteries.size() > 0) {
                    Battery batteryA = batteries.get(0);
                    int num = 1;
                    switch (postion) {
                        case 0:
                            num = 1;
                            break;
                        case 1:
                            num = 5;
                            break;
                        case 2:
                            num = 10;

                            break;
                        case 3:
                            num = 15;
                            break;
                    }
                    batteryA.setSelfDischargeInDays(num, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (getActivity() != null) {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (djiError != null) {
                                            ToastUtil.showToast("设置自放电失败：" + djiError.getDescription());
                                        } else {
                                            ToastUtil.showToast("设置自放电成功");
                                        }
                                    }
                                });
                            }
                        }
                    });

                } else {
                    ToastUtil.showToast("未检测到智能电池");
                }
            } else {
                ToastUtil.showToast("未检测到智能电池");
            }
        }

    };
}
