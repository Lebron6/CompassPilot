package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.databinding.FragmentFlightControllerBinding;
import com.compass.ux.databinding.FragmentGraphicBinding;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.window.DisconnectActionWindow;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.airlink.OcuSyncMagneticInterferenceLevel;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.airlink.AirLink;
import dji.sdk.airlink.OcuSyncLink;
import dji.sdk.products.Aircraft;


/**
 * 图传
 */
public class GraphicFragment extends BaseFragment {

    FragmentGraphicBinding mBinding;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentGraphicBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }


    @Override
    protected void initDatas() {

        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            AirLink airLink = aircraft.getAirLink();
            if (airLink != null) {
                if (airLink.isOcuSyncLinkSupported()) {
                    OcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
                    if (ocuSyncLink != null) {
                        ocuSyncLink.setOcuSyncMagneticInterferenceLevelCallback(new OcuSyncMagneticInterferenceLevel.Callback() {
                            @Override
                            public void onUpdate(OcuSyncMagneticInterferenceLevel ocuSyncMagneticInterferenceLevel) {
                                if (getActivity()!=null){
                                    getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvMagneticInterferenceLevel.setText(ocuSyncMagneticInterferenceLevel.name());
                                        switch (ocuSyncMagneticInterferenceLevel.value()){
                                            case 0:
                                                mBinding.tvOcuStatus.setText("良好");
                                                break;
                                            case 1:
                                                mBinding.tvOcuStatus.setText("轻微干扰");
                                                break;
                                            case 2:
                                                mBinding.tvOcuStatus.setText("中度干扰");
                                                break;
                                            case 3:
                                                mBinding.tvOcuStatus.setText("重度干扰");
                                                break;
                                        }
                                    }
                                });}
                            }
                        });
                        ocuSyncLink.setVideoDataRateCallback(new OcuSyncLink.VideoDataRateCallback() {
                            @Override
                            public void onUpdate(float v) {
                                if (getActivity()!=null){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBinding.tvMbps.setText(v + "Mbps");
                                        }
                                    });
                                }

                            }
                        });
                        ocuSyncLink.getChannelBandwidth(new CommonCallbacks.CompletionCallbackWith<OcuSyncBandwidth>() {
                            @Override
                            public void onSuccess(OcuSyncBandwidth ocuSyncBandwidth) {
                                switch (ocuSyncBandwidth.value()) {
                                    case 0:
                                        LocalSource.getInstance().setChannelBandwidth("20MHz");
                                        if (getActivity()!=null){
                                            getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.tvChannelBandWidth.setText("20MHz");
                                            }
                                        });}
                                        break;
                                    case 1:
                                        LocalSource.getInstance().setChannelBandwidth("10MHz");
                                        if (getActivity()!=null){
                                            getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.tvChannelBandWidth.setText("10MHz");
                                            }
                                        });}
                                        break;
                                    case 2:
                                        LocalSource.getInstance().setChannelBandwidth("40MHz");
                                        if (getActivity()!=null){
                                            getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.tvChannelBandWidth.setText("40MHz");
                                            }
                                        });}
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(DJIError djiError) {

                            }
                        });
                        //工作频段 2.4g 5.8g 双频
                        ocuSyncLink.getFrequencyBand(new CommonCallbacks.CompletionCallbackWith<OcuSyncFrequencyBand>() {
                            @Override
                            public void onSuccess(OcuSyncFrequencyBand ocuSyncFrequencyBand) {
                                switch (ocuSyncFrequencyBand.getValue()) {
                                    case 0:
                                        if (getActivity()!=null){
                                            getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.rbTwo.setChecked(true);
                                            }
                                        });}
                                        LocalSource.getInstance().setFrequencyBand("双频");
                                        break;
                                    case 1:
                                        if (getActivity()!=null){
                                            getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.rbTf.setChecked(true);
                                            }
                                        });}
                                        LocalSource.getInstance().setFrequencyBand("2.4G");
                                        break;
                                    case 2:
                                        if (getActivity()!=null){
                                            getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.rbFe.setChecked(true);
                                            }
                                        });}
                                        LocalSource.getInstance().setFrequencyBand("5.8G");
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(DJIError djiError) {
                            }
                        });
                    }
                } else {
                    ToastUtil.showToast("固件不支持或飞行器未连接");
                }
            } else {
                ToastUtil.showToast("固件不支持或飞行器未连接");
            }
        } else {
            ToastUtil.showToast("固件不支持或飞行器未连接");
        }

        mBinding.rgChanel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Aircraft aircraft = ApronApp.getAircraftInstance();
                if (aircraft != null) {
                    AirLink airLink = aircraft.getAirLink();
                    if (airLink != null) {
                        if (airLink.isOcuSyncLinkSupported()) {
                            OcuSyncLink ocuSyncLink = airLink.getOcuSyncLink();
                            if (ocuSyncLink != null) {
                                switch (checkedId) {
                                    case R.id.rb_tf:
                                        ocuSyncLink.setFrequencyBand(OcuSyncFrequencyBand.FREQUENCY_BAND_2_DOT_4_GHZ, new CommonCallbacks.CompletionCallback() {
                                            @Override
                                            public void onResult(DJIError djiError) {
                                                if (getActivity()!=null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                            public void run() {
                                                            if (djiError == null) {
                                                                ToastUtil.showToast("已切换频段");
                                                            } else {
                                                                ToastUtil.showToast("切换频段失败：" + djiError.getDescription());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        break;
                                    case R.id.rb_fe:
                                        ocuSyncLink.setFrequencyBand(OcuSyncFrequencyBand.FREQUENCY_BAND_5_DOT_8_GHZ, new CommonCallbacks.CompletionCallback() {
                                            @Override
                                            public void onResult(DJIError djiError) {
                                                if (getActivity()!=null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (djiError == null) {

                                                                ToastUtil.showToast("已切换频段");
                                                            } else {
                                                                ToastUtil.showToast("切换频段失败：" + djiError.getDescription());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        break;
                                    case R.id.rb_two:
                                        ocuSyncLink.setFrequencyBand(OcuSyncFrequencyBand.FREQUENCY_BAND_DUAL, new CommonCallbacks.CompletionCallback() {
                                            @Override
                                            public void onResult(DJIError djiError) {
                                                if (getActivity()!=null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (djiError == null) {
                                                                ToastUtil.showToast("已切换频段");
                                                            } else {
                                                                ToastUtil.showToast("切换频段失败：" + djiError.getDescription());
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        break;
                                }
                            }
                        } else {
                            ToastUtil.showToast("固件不支持或飞行器未连接");
                        }
                    } else {
                        ToastUtil.showToast("固件不支持或飞行器未连接");
                    }
                } else {
                    ToastUtil.showToast("固件不支持或飞行器未连接");
                }

            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    };

}
