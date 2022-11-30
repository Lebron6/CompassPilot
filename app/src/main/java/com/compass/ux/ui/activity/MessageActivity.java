package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityEquipmentDetailsBinding;
import com.compass.ux.databinding.ActivityMessageBinding;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.ui.adapter.CallBackAdapter;
import com.compass.ux.ui.adapter.MessageAdapter;

public class MessageActivity extends BaseActivity {
    private ActivityMessageBinding mBinding;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMessageBinding.inflate(getLayoutInflater());
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
        MessageAdapter adapter = new MessageAdapter(this);
        RecyclerViewHelper.initRecyclerViewV(this, mBinding.rvMessage, false, adapter);
    }
}
