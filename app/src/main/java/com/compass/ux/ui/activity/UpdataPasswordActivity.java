package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.compass.ux.api.HttpUtil;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityFlightHistoryBinding;
import com.compass.ux.databinding.ActivityUpdataPasswordBinding;
import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.UpdataPasswordResult;
import com.compass.ux.tools.AppManager;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdataPasswordActivity extends BaseActivity {

    private ActivityUpdataPasswordBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UpdataPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUpdataPasswordBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    private void initView() {
        mBinding.layoutFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void commit() {
        if (TextUtils.isEmpty(mBinding.etOldPassword.getText())) {
            ToastUtil.showToast("请输入旧密码");
            return;
        }
        if (TextUtils.isEmpty(mBinding.etNewPassword.getText())) {
            ToastUtil.showToast("请输入新密码");
            return;
        }
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.createRequest2().updataPassword(PreferenceUtils.getInstance().getUserToken(), mBinding.etOldPassword.getText().toString(), mBinding.etNewPassword.getText().toString()).enqueue(new Callback<UpdataPasswordResult>() {
            @Override
            public void onResponse(Call<UpdataPasswordResult> call, Response<UpdataPasswordResult> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case "200":
                            PreferenceUtils.getInstance().loginOut();
                            AppManager.getAppManager().finishAllActivity();
                            startActivity(new Intent(UpdataPasswordActivity.this, LoginActivity.class));
                            break;
                        default:
                            ToastUtil.showToast("修改失败:" + response.body().getMsg());
                            break;
                    }
                } else {
                    Toast.makeText(UpdataPasswordActivity.this, "网络异常:修改失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdataPasswordResult> call, Throwable t) {
                Toast.makeText(UpdataPasswordActivity.this, "网络异常:修改失败", Toast.LENGTH_SHORT).show();
                Log.e("网络异常:修改失败", t.toString());
            }
        });
    }

}
