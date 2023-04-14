package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.constant.Constant;
import com.compass.ux.databinding.FragmentAvoidingObstaclesBinding;
import com.compass.ux.databinding.FragmentFlightControllerBinding;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.window.DisconnectActionWindow;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightAssistant;


/**
 * 感知避障设置
 */
public class AvoidingObstaclesFragment extends BaseFragment {

    FragmentAvoidingObstaclesBinding mBinding;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAvoidingObstaclesBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }


    @Override
    protected void initDatas() {
        mBinding.sbRthEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.sbAoEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.sbLpEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        mBinding.sbRadar.setOnCheckedChangeListener(onCheckedChangeListener);
        if (Helper.isFlightControllerAvailable()) {
            FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
            if (assistant != null) {
                //返航障碍物检测
                assistant.getRTHRemoteObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {                        if (getActivity()!=null){

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbRthEnable.setChecked(aBoolean);
                            }
                        });}
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //下避障
                assistant.getLandingProtectionEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {                        if (getActivity()!=null){

                        LocalSource.getInstance().setLandingProtection(aBoolean);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbLpEnable.setChecked(aBoolean);
                            }
                        });}
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
                //水平避障
                assistant.getHorizontalVisionObstacleAvoidanceEnabled(new CommonCallbacks.CompletionCallbackWith<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {                        if (getActivity()!=null){

                        LocalSource.getInstance().setActiveObstacleAvoidance(aBoolean);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbAoEnable.setChecked(aBoolean);
                            }
                        });}
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });

            } else {
                ToastUtil.showToast("飞行器未连接或固件不支持");
            }
        } else {
            ToastUtil.showToast("飞行器未连接或固件不支持");
        }

    }

    SwitchButton.OnCheckedChangeListener onCheckedChangeListener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            if (Helper.isFlightControllerAvailable()) {
                FlightAssistant assistant = ApronApp.getAircraftInstance().getFlightController().getFlightAssistant();
                if (assistant != null) {
                    switch (view.getId()) {
                        case R.id.sb_radar:
                            if (getActivity().findViewById(R.id.rw).getVisibility()==View.VISIBLE){
                                if (getActivity()!=null){
                                    getActivity().findViewById(R.id.rw).setVisibility(View.GONE);
                                }
                            }else{
                                if (getActivity()!=null){
                                    getActivity().findViewById(R.id.rw).setVisibility(View.VISIBLE);
                                }                            }
                            break;
                        case R.id.sb_ao_enable:

                            assistant.setHorizontalVisionObstacleAvoidanceEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {                        if (getActivity()!=null){

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError == null) {
                                                ToastUtil.showToast("避障状态已修改");
                                                LocalSource.getInstance().setActiveObstacleAvoidance(isChecked);
                                            } else {
                                                ToastUtil.showToast("避障状态修改失败：" + djiError.getDescription());
                                            }
                                        }
                                    });}

                                }
                            });


                            break;
                        case R.id.sb_lp_enable:
                            assistant.setLandingProtectionEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {                        if (getActivity()!=null){

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (djiError == null) {
                                                ToastUtil.showToast("下避障状态已修改");
                                                LocalSource.getInstance().setLandingProtection(isChecked);
                                            } else {
                                                ToastUtil.showToast("下避障状态修改失败：" + djiError.getDescription());
                                            }
                                        }
                                    });}
                                }
                            });
                            break;
                        case R.id.sb_rth_enable:
                            //下避障

                                    assistant.setRTHRemoteObstacleAvoidanceEnabled(isChecked, new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {                        if (getActivity()!=null){

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (djiError == null) {
                                                        ToastUtil.showToast("返航避障状态已修改");
                                                    } else {
                                                        ToastUtil.showToast("返航避障状态修改失败：" + djiError.getDescription());
                                                    }
                                                }
                                            });}

                                        }
                                    });

                            break;
                    }
                } else {
                    ToastUtil.showToast("飞行器未连接或固件不支持");
                }
            } else {
                ToastUtil.showToast("飞行器未连接或固件不支持");
            }

        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    };

}
