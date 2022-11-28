package com.compass.ux.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentEquipmentTypeBinding;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.ui.adapter.EquipmentTypeAdapter;


/**
 * 设备状态
 */
public class EquipmentType extends BaseFragment {

    private int type;

    public EquipmentType(int type) {
        this.type=type;
    }

    FragmentEquipmentTypeBinding mBinding;

    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentEquipmentTypeBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }

    @Override
    protected void initDatas() {
        EquipmentTypeAdapter adapter = new EquipmentTypeAdapter(getActivity());
        RecyclerViewHelper.initRecyclerViewV(getActivity(), mBinding.rvEquipmentType, false, adapter);
    }
}
