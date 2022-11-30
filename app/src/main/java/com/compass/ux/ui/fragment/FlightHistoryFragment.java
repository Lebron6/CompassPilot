package com.compass.ux.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;

import com.compass.ux.api.HttpUtil;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentFlightHistoryBinding;
import com.compass.ux.entity.UserInfo;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.ui.adapter.EquipmentTypeAdapter;
import com.compass.ux.ui.adapter.FlightHistoryAdapter;
import com.compass.ux.ui.view.SimpleFooter;
import com.compass.ux.ui.view.SimpleHeader;
import com.compass.ux.ui.view.SpringView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 飞行历史
 */
public class FlightHistoryFragment extends BaseFragment {

    FragmentFlightHistoryBinding mBinding;
    private int page=1;
    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentFlightHistoryBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }

    @Override
    protected void initDatas() {
        FlightHistoryAdapter adapter = new FlightHistoryAdapter(getActivity());
        RecyclerViewHelper.initRecyclerViewV(getActivity(), mBinding.rvFlightHistory, false, adapter);
        initSpringViewStyle();
    }

    private void initSpringViewStyle() {
        mBinding.svFlightHistory.setType(SpringView.Type.FOLLOW);
        mBinding.svFlightHistory.setListener(onFreshListener);
        mBinding.svFlightHistory.setHeader(new SimpleHeader(getActivity()));
        mBinding.svFlightHistory.setFooter(new SimpleFooter(getActivity()));
    }
    SpringView.OnFreshListener onFreshListener = new SpringView.OnFreshListener() {
        @Override
        public void onRefresh() {
            getHistoryList();
        }

        @Override
        public void onLoadmore() {
            onLoadmoreList();
        }
    };

    public void getHistoryList() {
        HttpUtil httpUtil=new HttpUtil();
        httpUtil.createRequest2().flightHistory(PreferenceUtils.getInstance().getUserToken(),
                "","","5",page+"").enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                mBinding.svFlightHistory.onFinishFreshAndLoad();
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {

            }
        });
    }

    private void onLoadmoreList() {
        page = ++page;

    }
}
