package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityEquipmentDetailsBinding;

public class EquipmentDetailsActivity extends BaseActivity {
    private ActivityEquipmentDetailsBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, EquipmentDetailsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityEquipmentDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
    }
}
