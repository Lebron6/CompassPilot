package com.compass.ux.ui.fragment.setting.Battery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentBatteryDetailsBinding;
import com.compass.ux.databinding.FragmentBatteryInfoBinding;
import com.compass.ux.databinding.FragmentSensorStatusBinding;

import java.util.List;

import dji.common.battery.BatteryState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;


/**
 * 电池详情
 */
public class BatteryDetailsFragment extends BaseFragment {

    FragmentBatteryDetailsBinding mBinding;
    private int num;

    public BatteryDetailsFragment(int num) {
        this.num = num;
    }

    public BatteryDetailsFragment() {
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentBatteryDetailsBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }

    @Override
    protected void initDatas() {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null && aircraft.isConnected()) {
            List<Battery> batteries = aircraft.getBatteries();
            Battery battery = batteries.get(num);
            battery.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
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
            battery.getCellVoltages(new CommonCallbacks.CompletionCallbackWith<Integer[]>() {
                @Override
                public void onSuccess(Integer[] integers) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.tvVO.setText(integers[0]+"mv");
                                mBinding.tvVT.setText(integers[1]+"mv");
                                mBinding.tvVTh.setText(integers[2]+"mv");
                                mBinding.tvVF.setText(integers[3]+"mv");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            battery.setStateCallback(new BatteryState.Callback() {
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
//                                switch (batteryState.getConnectionState()) {
//                                    case NORMAL:
                                        mBinding.tvBatteryStatus.setText("正常");
//                                        break;
//                                    case INVALID:
//                                        mBinding.tvBatteryStatus.setText("无效");
//                                        break;
//                                    case EXCEPTION:
//                                        mBinding.tvBatteryStatus.setText("异常");
//                                        break;
//                                }
                            }
                        });
                    }

                }
            });

        }
    }


}
