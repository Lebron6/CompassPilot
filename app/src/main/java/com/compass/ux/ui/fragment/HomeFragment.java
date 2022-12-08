package com.compass.ux.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.constant.Constant;
import com.compass.ux.databinding.FragmentHomeBinding;
import com.compass.ux.tools.Helper;
import com.compass.ux.ui.activity.GalleryActivity;
import com.compass.ux.ui.activity.TaskReportActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    FragmentHomeBinding mBinding;

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(layoutInflater, container, false);
        initView();
        return mBinding;
    }

    private void initView() {
        mBinding.tvStartMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskReportActivity.actionStart(getActivity());
            }
        });
        mBinding.tvMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryActivity.actionStart(getActivity());
            }
        });
    }

    @Override
    protected void initDatas() {
        if (Helper.isFlightControllerAvailable()) {
            mBinding.layoutIsConnect.setVisibility(View.VISIBLE);
            mBinding.layoutDisconnect.setVisibility(View.GONE);
        }
        mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_air2s));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant.FLAG_CONNECT:
                mBinding.layoutIsConnect.setVisibility(View.VISIBLE);
                mBinding.layoutDisconnect.setVisibility(View.GONE);
                setIcon();
                break;
            case Constant.FLAG_DISCONNECT:
                mBinding.layoutIsConnect.setVisibility(View.GONE);
                mBinding.layoutDisconnect.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setIcon() {
        if (ApronApp.getProductInstance() != null) {
            mBinding.tvUavNum.setText(ApronApp.getProductInstance().getModel().getDisplayName());
            switch (ApronApp.getProductInstance().getModel()) {
                case PHANTOM_4_PRO:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case PHANTOM_4:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case PHANTOM_4_RTK:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_phantom4));
                    break;
                case MATRICE_300_RTK:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_m300rtk));
                    break;
                case MAVIC_2:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_mavic2));
                    break;
                case MAVIC_2_PRO:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_mavic2));
                    break;
                case MAVIC_AIR_2:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_mavic2));
                    break;
                case DJI_AIR_2S:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_air2s));
                    break;
                default:
                    mBinding.ivAir.setBackground(getActivity().getResources().getDrawable(R.mipmap.ic_air2s));

                    break;
            }
        }
    }
}
