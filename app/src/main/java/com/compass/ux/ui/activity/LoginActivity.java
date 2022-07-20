package com.compass.ux.ui.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.compass.ux.R;
import com.compass.ux.base.BaseActivity;
public class LoginActivity extends BaseActivity {
private EditText etAccount;
private EditText etPassword;
private TextView tvLogin;

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        etAccount=findViewById(R.id.et_account);
        etPassword=findViewById(R.id.et_password);
        tvLogin=findViewById(R.id.tv_login);
    }
}
