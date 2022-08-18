package com.compass.ux.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.compass.ux.R;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginValues;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.yanzhenjie.permission.AndPermission;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText etAccount;
    private EditText etPassword;
    private TextView tvLogin;
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        checkAndRequestPermissions();
    }

    private void initView() {
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLogin();
            }
        });
        if (!TextUtils.isEmpty(PreferenceUtils.getInstance().getUserName())) {
            etAccount.setText(PreferenceUtils.getInstance().getUserName());
            etPassword.setText(PreferenceUtils.getInstance().getUserPassword());
        }
    }

    private void toLogin() {
        if (TextUtils.isEmpty(etAccount.getText().toString())) {
            ToastUtil.showToast("请输入账号");
            return;
        }
        if (TextUtils.isEmpty(etPassword.getText().toString())) {
            ToastUtil.showToast("请输入密码");
            return;
        }
        LoginValues loginValues = new LoginValues();
        loginValues.setUsername(etAccount.getText().toString());
        loginValues.setPassword(etPassword.getText().toString());
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.createRequest().userLogin(loginValues).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.body() != null) {
                    switch (response.body().getCode()) {
                        case 0:
                            PreferenceUtils.getInstance().setUserToken(response.body().getData().getAccess_token());
                            PreferenceUtils.getInstance().setUserName(loginValues.getUsername());
                            PreferenceUtils.getInstance().setUserPassword(loginValues.getPassword());
                            MqttConfig.SOCKET_HOST = response.body().getData().getMqtt_addr();
                            MqttConfig.USER_PASSWORD = response.body().getData().getMqtt_password();
                            MqttConfig.USER_NAME = response.body().getData().getUsername();
                            startActivity(new Intent(LoginActivity.this, ConnectionActivity.class));
                            finish();
                            break;

                    }
                } else {
                    Toast.makeText(LoginActivity.this, "网络异常:登陆失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络异常:登陆失败", Toast.LENGTH_SHORT).show();
                Log.e("网络异常:登陆失败", t.toString());
            }
        });
    }

    /**
     * Checks if there is any missing permissions, and requests runtime permission if needed.
     */
    private void checkAndRequestPermissions() {
        AndPermission.with(this)
                .runtime()
                .permission(REQUIRED_PERMISSION_LIST)
                .onGranted(permissions -> {
                    // If there is enough permission, we will start the registration
                })
                .onDenied(permissions -> {
                    // Storage permission are not allowed.
                    ToastUtil.showToast("Missing permissions!!!");
                    finish();
                })
                .start();

    }
}
