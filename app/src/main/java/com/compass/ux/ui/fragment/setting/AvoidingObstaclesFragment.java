package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.databinding.FragmentAvoidingObstaclesBinding;
import com.compass.ux.databinding.FragmentFlightControllerBinding;
import com.compass.ux.ui.window.DisconnectActionWindow;

import java.util.ArrayList;
import java.util.List;


/**
 * 感知避障设置
 */
public class AvoidingObstaclesFragment extends BaseFragment {

    FragmentAvoidingObstaclesBinding mBinding;



    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentAvoidingObstaclesBinding.inflate(layoutInflater, container, false);
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
