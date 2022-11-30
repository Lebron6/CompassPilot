package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.callback.OnTimeSelectedListener;
import com.compass.ux.databinding.ActivityTaskReportBinding;
import com.compass.ux.ui.adapter.TaskTypeAdapter;
import com.compass.ux.ui.view.TaskTypePop;

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
        mBinding.layoutFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.tvStartFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskTypePop pop=new TaskTypePop(TaskReportActivity.this);
                pop.showView(mBinding.tvStartFly, new TaskTypeAdapter(), new OnTimeSelectedListener() {
                    @Override
                    public void select(int postion) {

                    }
                });
            }
        });
    }
}
