package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityEquipmentDetailsBinding;
import com.compass.ux.databinding.ActivityFlightHistoryBinding;

public class FlightHistoryActivity extends BaseActivity {

    private ActivityFlightHistoryBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, FlightHistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityFlightHistoryBinding.inflate(getLayoutInflater());
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
        mBinding.jzVideo.SAVE_PROGRESS=false;
        mBinding.jzVideo.setUp("http://36.154.125.57/p0189/2022/11/21/17.08.56.175new27.mp4", "回放");
    }

    @Override
    public void onBackPressed() {
        if (mBinding.jzVideo.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mBinding.jzVideo.releaseAllVideos();
    }
}
