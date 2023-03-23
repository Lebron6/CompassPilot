package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentBatteryInfoBinding;
import com.compass.ux.databinding.FragmentGimbalSettingBinding;


/**
 * 云台设置
 */
public class GimbalSettingFragment extends BaseFragment {

    FragmentGimbalSettingBinding mBinding;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentGimbalSettingBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }


    @Override
    protected void initDatas() {

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }
    };

}
