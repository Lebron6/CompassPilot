package com.compass.ux.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.compass.ux.R;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.callback.DJISDKRegistrationCallback;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.LoginResult;
import com.compass.ux.entity.LoginValues;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.AndPermission;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.sdkmanager.DJISDKManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private EditText etAccount;
    private EditText etPassword;
    private EditText etSn;
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
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        checkAndRequestPermissions();
        registerDJISDK();
    }

    private void registerDJISDK() {
        //Check the permissions before registering the application for android system 6.0 above.
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (permissionCheck == 0 && permissionCheck2 == 0)) {
            DJISDKManager.getInstance().registerApp(getApplicationContext(), new DJISDKRegistrationCallback(this));
        } else {
            Toast.makeText(getApplicationContext(), "Please check if the permission is granted.", Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant.FLAG_CONNECT:
                if (Helper.isFlightControllerAvailable()) {

                    FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                    flightController.getSerialNumber(new CommonCallbacks.CompletionCallbackWith<String>() {
                        @Override
                        public void onSuccess(String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etSn.setText(s);
                                    ApronApp.SERIAL_NUMBER=s;
                                }
                            });
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etSn.setText("获取sn失败");
                                }
                            });
                        }
                    });//获取SN码
                }
                break;
        }
    }

    private void initView() {
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        etSn = findViewById(R.id.et_sn);
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
        if (TextUtils.isEmpty(etSn.getText().toString())) {
            ToastUtil.showToast("请接入飞行器获取SN码");
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
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            break;

                    }
                } else {
                    Toast.makeText(LoginActivity.this, "网络异常:登陆失败111", Toast.LENGTH_SHORT).show();
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
