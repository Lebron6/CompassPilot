package com.compass.ux.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.databinding.FragmentHomeBinding;
import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginValues;
import com.compass.ux.entity.AirName;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.activity.EquipmentDetailsActivity;
import com.compass.ux.ui.activity.GalleryActivity;
import com.compass.ux.ui.activity.TaskReportActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.log.third.Logger;
import dji.sdk.flightcontroller.FlightController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    FragmentHomeBinding mBinding;

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(layoutInflater, container, false);
        initView();
        return mBinding;
    }

    private void initView() {
        mBinding.tvStartMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskReportActivity.actionStart(getActivity());
            }
        });
        mBinding.tvMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity.actionStart(getActivity());
            }
        });
        mBinding.tvAirInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(airName)){
                    ToastUtil.showToast("请接入无人机获取SN");
                }else{
                    EquipmentDetailsActivity.actionStart(getActivity(), airName);
                }
            }
        });
    }

    @Override
    protected void initDatas() {
        if (Helper.isFlightControllerAvailable()) {
            mBinding.layoutIsConnect.setVisibility(View.VISIBLE);
            mBinding.layoutDisconnect.setVisibility(View.GONE);
            setIcon();
            getNameBySN();
        }else{
            mBinding.layoutIsConnect.setVisibility(View.GONE);
            mBinding.layoutDisconnect.setVisibility(View.VISIBLE);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant.FLAG_CONNECT:
                mBinding.layoutIsConnect.setVisibility(View.VISIBLE);
                mBinding.layoutDisconnect.setVisibility(View.GONE);
                setIcon();
                getNameBySN();
                break;
            case Constant.FLAG_DISCONNECT:
                mBinding.layoutIsConnect.setVisibility(View.GONE);
                mBinding.layoutDisconnect.setVisibility(View.VISIBLE);
                break;
        }
    }

    String airName;

    private void getNameBySN() {
        if (Helper.isFlightControllerAvailable()) {

            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                @Override
                public void onSuccess(String s) {
                    HttpUtil httpUtil = new HttpUtil();
                    httpUtil.createRequest2().getName(PreferenceUtils.getInstance().getUserToken(), s).enqueue(new Callback<AirName>() {
                        @Override
                        public void onResponse(Call<AirName> call, Response<AirName> response) {
                            if (response.body() != null) {
                                if (response.body().getCode().equals("200")) {
                                    airName = response.body().getResults();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AirName> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        }
    }


    private void setIcon() {
        if (ApronApp.getProductInstance() != null) {
            mBinding.tvUavNum.setText(ApronApp.getProductInstance().getModel().getDisplayName());
            switch (ApronApp.getProductInstance().getModel()) {
                case PHANTOM_4_PRO:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case PHANTOM_4:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case PHANTOM_4_RTK:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case MATRICE_300_RTK:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_m300rtk));
                    break;
                case MAVIC_2:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_mavic2));
                    break;
                case MAVIC_2_PRO:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_mavic2));
                    break;
                case MAVIC_AIR_2:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_mavic2));
                    break;
                case DJI_AIR_2S:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_air2s));
                    break;
                default:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_air2s));

                    break;
            }
        }
    }
}
