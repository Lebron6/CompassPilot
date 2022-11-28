package com.compass.ux.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentPersonalBinding;
import com.compass.ux.ui.activity.MessageActivity;


/**
 * 个人中心
 */
public class PersonalFragment extends BaseFragment {

    FragmentPersonalBinding mBinding;

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPersonalBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }



    @Override
    protected void initDatas() {
        mBinding.layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageActivity.actionStart(getActivity());
            }
        });
    }
}
