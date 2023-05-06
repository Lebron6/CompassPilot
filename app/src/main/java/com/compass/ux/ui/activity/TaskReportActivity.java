package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.icu.number.Precision;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.callback.OnTypeSelectedListener;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.databinding.ActivityTaskReportBinding;
import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginValues;
import com.compass.ux.entity.TaskReportResult;
import com.compass.ux.entity.TaskReportValues;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.view.ButtomSelectWindow;
import com.compass.ux.ui.view.TaskTypePop;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.log.third.Logger;
import dji.sdk.flightcontroller.FlightController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskReportActivity extends BaseActivity {

    private ActivityTaskReportBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TaskReportActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityTaskReportBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    private void initView() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mBinding.layoutFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.layoutChoseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtomSelectWindow pop = new ButtomSelectWindow(TaskReportActivity.this);
                pop.showView(mBinding.layoutChoseType, new OnTypeSelectedListener() {
                    @Override
                    public void select(String value) {
                        mBinding.etTaskType.setText(value);
                        pop.dismiss();
                    }
                });
            }
        });
        mBinding.tvStartFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ApronApp.SERIAL_NUMBER)){
                    ToastUtil.showToast("SN获取失败,请接入飞机");
                    return;
                }
                commit();
//                startActivity(new Intent(TaskReportActivity.this, FlightActivity.class));

            }
        });

        getSNCode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant.FLAG_CONNECT:
                getSNCode();
                break;
        }
    }

    private void getSNCode() {
        if (Helper.isFlightControllerAvailable()) {

            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                @Override
                public void onSuccess(String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApronApp.SERIAL_NUMBER = s;
                            LoginValues loginValues = new LoginValues();
                            loginValues.setUsername(PreferenceUtils.getInstance().getUserName());
                            loginValues.setPassword(PreferenceUtils.getInstance().getUserPassword());
                            loginValues.setUavType(ApronApp.getProductInstance().getModel().getDisplayName());
                            loginValues.setUavSn(s);
                            com.orhanobut.logger.Logger.e("登录参数:"+new Gson().toJson(loginValues));
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
                                        Logger.e("网络异常:登陆失败");
                                    }
                                }

                                @Override
                                public void onFailure(Call<LoginResult> call, Throwable t) {
                                    Logger.e("网络异常:登陆失败" + t.toString());
                                }
                            });
                        }
                    });
                }

                @Override
                public void onFailure(DJIError djiError) {
                    runOnUiThread(new Runnable() {
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

    private void commit() {
        if (TextUtils.isEmpty(mBinding.etTaskType.getText())) {
            ToastUtil.showToast("请选择任务类型");
            return;
        }
        if (TextUtils.isEmpty(mBinding.etTaskName.getText())) {
            ToastUtil.showToast("请填写任务名称");
            return;
        }
        HttpUtil httpUtil = new HttpUtil();
        TaskReportValues values = new TaskReportValues();
        values.setTaskName(mBinding.etTaskName.getText().toString());
        values.setTaskType(mBinding.etTaskType.getText().toString());
        httpUtil.createRequest2().taskReport(PreferenceUtils.getInstance().getUserToken(), values).enqueue(new Callback<TaskReportResult>() {
            @Override
            public void onResponse(Call<TaskReportResult> call, Response<TaskReportResult> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case "200":
                            if (!FlightActivity.isStarted()) {
                                startActivity(new Intent(TaskReportActivity.this, FlightActivity.class));
                            }
                            break;
                    }
                } else {
                    Toast.makeText(TaskReportActivity.this, "网络异常1:报备失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskReportResult> call, Throwable t) {
                Toast.makeText(TaskReportActivity.this, "报备失败" + t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
