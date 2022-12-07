package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.compass.ux.api.HttpUtil;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.databinding.ActivityMessageBinding;
import com.compass.ux.entity.MessageResult;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.adapter.EquipmentTypeAdapter;
import com.compass.ux.ui.adapter.MessageAdapter;
import com.compass.ux.ui.view.SimpleFooter;
import com.compass.ux.ui.view.SimpleHeader;
import com.compass.ux.ui.view.SpringView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends BaseActivity {

    private ActivityMessageBinding mBinding;
    private int page = 1;
    MessageAdapter adapter;
    List<MessageResult.ResultsDTO> datas=new ArrayList<>();

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
        adapter = new MessageAdapter(this);
        RecyclerViewHelper.initRecyclerViewV(this, mBinding.rvMessage, false, adapter);
        initSpringViewStyle();
        getEquipmentList();
    }
    private void initSpringViewStyle() {
        mBinding.svMessage.setType(SpringView.Type.FOLLOW);
        mBinding.svMessage.setListener(onFreshListener);
        mBinding.svMessage.setHeader(new SimpleHeader(this));
        mBinding.svMessage.setFooter(new SimpleFooter(this));
    }
    SpringView.OnFreshListener onFreshListener = new SpringView.OnFreshListener() {
        @Override
        public void onRefresh() {
            page = 1;
            getEquipmentList();
        }

        @Override
        public void onLoadmore() {
            onLoadmoreList();
        }
    };
    public void getEquipmentList() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.createRequest2().messageList(PreferenceUtils.getInstance().getUserToken(),
                page, 10).enqueue(new Callback<MessageResult>() {
            @Override
            public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                if (response.body()!=null&&response.body().getCode().equals("200")) {
                    datas=response.body().getResults();
                    adapter.setData(datas);
                }
                mBinding.svMessage.onFinishFreshAndLoad();
            }

            @Override
            public void onFailure(Call<MessageResult> call, Throwable t) {
                ToastUtil.showToast("网络异常:获取设备列表失败");
                mBinding.svMessage.onFinishFreshAndLoad();

            }
        });
    }

    private void onLoadmoreList() {
        page = ++page;
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.createRequest2().messageList(PreferenceUtils.getInstance().getUserToken(),
                page, 10).enqueue(new Callback<MessageResult>() {
            @Override
            public void onResponse(Call<MessageResult> call, Response<MessageResult> response) {
                if (response.body()!=null&&response.body().getCode().equals("200")) {
                    datas.addAll(response.body().getResults());
                    adapter.setData(datas);
                }
                mBinding.svMessage.onFinishFreshAndLoad();
            }

            @Override
            public void onFailure(Call<MessageResult> call, Throwable t) {
                ToastUtil.showToast("网络异常:获取设备列表失败");
                mBinding.svMessage.onFinishFreshAndLoad();

            }
        });
    }
}
