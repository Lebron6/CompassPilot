package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.icu.number.Precision;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.callback.OnTypeSelectedListener;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.databinding.ActivityTaskReportBinding;
import com.compass.ux.entity.TaskReportResult;
import com.compass.ux.entity.TaskReportValues;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.view.ButtomSelectWindow;
import com.compass.ux.ui.view.TaskTypePop;

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
        return false;
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
                ButtomSelectWindow pop=new ButtomSelectWindow(TaskReportActivity.this);
                pop.showView(mBinding.layoutChoseType,  new OnTypeSelectedListener() {
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
                commit();
            }
        });
    }

    private void commit() {
        if (TextUtils.isEmpty(mBinding.etTaskType.getText())){
            ToastUtil.showToast("请选择任务类型");
            return;
        }
        if (TextUtils.isEmpty(mBinding.etTaskName.getText())){
            ToastUtil.showToast("请填写任务名称");
            return;
        }
        HttpUtil httpUtil=new HttpUtil();
        TaskReportValues values=new TaskReportValues();
        values.setTaskName(mBinding.etTaskName.getText().toString());
        values.setTaskType(mBinding.etTaskType.getText().toString());
        httpUtil.createRequest2().taskReport(PreferenceUtils.getInstance().getUserToken(), values).enqueue(new Callback<TaskReportResult>() {
            @Override
            public void onResponse(Call<TaskReportResult> call, Response<TaskReportResult> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case "200":
                            if (!FlightActivity.isStarted()){
                                startActivity(new Intent(TaskReportActivity.this,FlightActivity.class));
                            }
                            break;
                    }
                } else {
                    Toast.makeText(TaskReportActivity.this, "网络异常1:报备失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskReportResult> call, Throwable t) {
                Toast.makeText(TaskReportActivity.this, "报备失败"+t.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
