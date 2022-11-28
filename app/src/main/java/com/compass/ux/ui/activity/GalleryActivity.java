package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityGalleryBinding;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.ui.adapter.GalleryAdapter;
import com.compass.ux.ui.view.datescroller.DateScrollerDialog;
import com.compass.ux.ui.view.datescroller.data.Type;
import com.compass.ux.ui.view.datescroller.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GalleryActivity extends BaseActivity {

    private ActivityGalleryBinding mBinding;
    private static final long HUNDRED_YEARS = 100L * 365 * 1000 * 60 * 60 * 24L;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private long mLastTime = System.currentTimeMillis();
    private long mLastFinishTime = System.currentTimeMillis();


    @Override
    public boolean useEventBus() {
        return false;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    private void initView() {
        mBinding.layoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateScroller();
            }
        });
        GalleryAdapter adapter = new GalleryAdapter();
        RecyclerViewHelper.initRecyclerViewG(this, mBinding.rvPic, adapter, 2);
    }

    private void showDateScroller() {

        DateScrollerDialog dialog = new DateScrollerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
                .setTitleStringId("选择日期")
                .setMinMilliseconds(System.currentTimeMillis() - HUNDRED_YEARS)
                .setMaxMilliseconds(System.currentTimeMillis() + HUNDRED_YEARS)
                .setCurMilliseconds(mLastTime, mLastFinishTime)
                .setCallback(mOnDateSetListener)
                .build();
        if (dialog != null) {
            if (!dialog.isAdded()) {
                dialog.show(getSupportFragmentManager(), "year_month_day");
            }
        }

    }

    private OnDateSetListener mOnDateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DateScrollerDialog timePickerView, long milliseconds, long milliFinishseconds) {
            mLastTime = milliseconds;
            mLastFinishTime = milliFinishseconds;
            String text = getDateToString(milliseconds);
            String text2 = getDateToString(milliFinishseconds);
            mBinding.tvTime.setText(text + "-" + text2);
        }
    };

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

}
