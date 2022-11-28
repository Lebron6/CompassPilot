package com.compass.ux.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentFlightHistoryBinding;

/**
 * 飞行历史
 */
public class FlightHistoryFragment extends BaseFragment {

    FragmentFlightHistoryBinding mBinding;

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

    }
}
