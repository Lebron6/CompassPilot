package com.compass.ux.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.compass.ux.entity.TaskReportResult;
import com.compass.ux.entity.TaskReportValues;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.activity.EquipmentDetailsActivity;
import com.compass.ux.ui.activity.FlightActivity;
import com.compass.ux.ui.activity.GalleryActivity;
import com.compass.ux.ui.activity.TaskReportActivity;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
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

    private void commit() {
//        if (TextUtils.isEmpty(mBinding.etTaskType.getText())) {
//            ToastUtil.showToast("请选择任务类型");
//            return;
//        }
//        if (TextUtils.isEmpty(mBinding.etTaskName.getText())) {
//            ToastUtil.showToast("请填写任务名称");
//            return;
//        }
        HttpUtil httpUtil = new HttpUtil();
        TaskReportValues values = new TaskReportValues();
        values.setTaskName("飞行任务");
        values.setTaskType("临时");
        httpUtil.createRequest2().taskReport(PreferenceUtils.getInstance().getUserToken(), values).enqueue(new Callback<TaskReportResult>() {
            @Override
            public void onResponse(Call<TaskReportResult> call, Response<TaskReportResult> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case "200":
                            if (!FlightActivity.isStarted()) {
                                getActivity().startActivity(new Intent(getActivity(), FlightActivity.class));
                            }
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), "网络异常:报备失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskReportResult> call, Throwable t) {
                Toast.makeText(getActivity(), "报备失败" + t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initView() {
        mBinding.tvStartMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
//                if (TextUtils.isEmpty(sn)) {
//                    ToastUtil.showToast("请接入无人机");
//                } else {
//                    TaskReportActivity.actionStart(getActivity());
//                }
            }
        });
        mBinding.tvMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ApronApp.SERIAL_NUMBER)) {
                    ToastUtil.showToast("请接入无人机");
                } else {
                    GalleryActivity.actionStart(getActivity());
                }
            }
        });
        mBinding.tvAirInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(airName)) {
                    ToastUtil.showToast("请检查设备是否已录入");
                } else {
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
            getSNCode();
        } else {
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
                getSNCode();
                break;
            case Constant.FLAG_DISCONNECT:
                mBinding.layoutIsConnect.setVisibility(View.GONE);
                mBinding.layoutDisconnect.setVisibility(View.VISIBLE);
                break;
        }
    }

    String airName;
    String sn;

    //    private void getNameBySN() {
//        if (Helper.isFlightControllerAvailable()) {
//
//            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
//            flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
//                @Override
//                public void onSuccess(String s) {
//                    sn=s;
//                    Logger.e("SN" + s);
//                    HttpUtil httpUtil = new HttpUtil();
//                    httpUtil.createRequest2().getName(PreferenceUtils.getInstance().getUserToken(), s).enqueue(new Callback<AirName>() {
//                        @Override
//                        public void onResponse(Call<AirName> call, Response<AirName> response) {
//                            if (response.body() != null) {
//                                if (response.body().getCode().equals("200")) {
//                                    airName = response.body().getResults();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<AirName> call, Throwable t) {
//                            Logger.e("通过SN获取设备名称失败:" + t.toString());
//                        }
//                    });
//                }
//
//                @Override
//                public void onFailure(DJIError djiError) {
//
//                }
//            });
//        }
//    }
    private void getSNCode() {
        if (Helper.isFlightControllerAvailable()) {

            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                @Override
                public void onSuccess(String s) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApronApp.SERIAL_NUMBER = s;
                            PreferenceUtils.getInstance().setFlyNumber(s);
//                            ApronApp.SERIAL_NUMBER = "4GCCJ9DR0A0Q6T";
                            LoginValues loginValues = new LoginValues();
                            loginValues.setUsername(PreferenceUtils.getInstance().getUserName());
                            loginValues.setPassword(PreferenceUtils.getInstance().getUserPassword());
                            loginValues.setUavType(ApronApp.getProductInstance().getModel().getDisplayName());
                            loginValues.setUavSn(s);
                            com.orhanobut.logger.Logger.e("登录参数:" + new Gson().toJson(loginValues));
                            HttpUtil httpUtil = new HttpUtil();
                            httpUtil.createRequest().userLogin(loginValues).enqueue(new Callback<LoginResult>() {
                                @Override
                                public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                                    if (response.body() != null) {
                                        switch (response.body().getCode()) {
                                            case 0:
                                                MqttConfig.SOCKET_HOST = response.body().getData().getMqtt_addr();
                                                MqttConfig.USER_PASSWORD = response.body().getData().getMqtt_password();
                                                MqttConfig.USER_NAME = response.body().getData().getMqtt_username();

                                                break;
                                        }
                                    } else {
                                        dji.log.third.Logger.e("网络异常:登陆失败");
                                    }
                                }

                                @Override
                                public void onFailure(Call<LoginResult> call, Throwable t) {
                                    dji.log.third.Logger.e("网络异常:登陆失败" + t.toString());
                                }
                            });
                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast("获取SN失败");

                        }
                    });
                }
            });//获取SN码
        } else {
            ToastUtil.showToast("请连接飞机");
        }
    }


    private void setIcon() {
        if (ApronApp.getProductInstance() != null&&ApronApp.getProductInstance().getModel()!=null&&ApronApp.getProductInstance().getModel().getDisplayName()!=null) {
                mBinding.tvUavNum.setText(ApronApp.getProductInstance().getModel().getDisplayName());
            mBinding.tvSn.setText(ApronApp.SERIAL_NUMBER + "");
            Log.e("无人机类型",ApronApp.getProductInstance().getModel().toString()+"");
            switch (ApronApp.getProductInstance().getModel()) {
                case PHANTOM_4_PRO:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case PHANTOM_4_PRO_V2:
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
