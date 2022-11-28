package com.compass.ux.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.constant.Constant;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;

public class ConnectionActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConnectionActivity.class.getName();

    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mTextModelAvailable;
    private TextView mVersionTv;

    private Button mBtnOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        initUI();
    }



    @Override
    public boolean useEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant
                    .FLAG_CONNECT:
                BaseProduct mProduct = ApronApp.getProductInstance();
                if (null != mProduct && mProduct.isConnected()) {
                    Log.v(TAG, "refreshSDK: True");
                    mBtnOpen.setEnabled(true);
                    String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
                    mTextConnectionStatus.setText("Status: " + str + " connected");
                    if (null != mProduct.getModel()) {
                        mTextProduct.setText("" + mProduct.getModel().getDisplayName());
                    } else {
                        mTextProduct.setText(R.string.product_information);
                    }
                                    if (!FlightActivity.isStarted()){
                    startActivity(new Intent(this, FlightActivity.class));
                }
                } else {
                    Log.v(TAG, "refreshSDK: False");
                    mBtnOpen.setEnabled(false);
                    mTextProduct.setText(R.string.product_information);
                    mTextConnectionStatus.setText(R.string.connection_loose);
                }

                break;
        }
    }




    private void initUI() {

        mTextConnectionStatus = (TextView) findViewById(R.id.text_connection_status);
        mTextModelAvailable = (TextView) findViewById(R.id.text_model_available);
        mTextProduct = (TextView) findViewById(R.id.text_product_info);

        mVersionTv = (TextView) findViewById(R.id.textView2);
//        mVersionTv.setText(getResources().getString(R.string.sdk_version, DJISDKManager.getInstance().getSDKVersion()));
        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnOpen.setOnClickListener(this);
//        mBtnOpen.setEnabled(false);

    }


    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ConnectionActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open: {
                startActivity(new Intent(this, FlightActivity.class));

                break;
            }
            default:
                break;
        }
    }


}
