package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.databinding.FragmentRemoteControlBinding;
import com.compass.ux.tools.ToastUtil;

import dji.common.error.DJIError;
import dji.common.remotecontroller.AircraftMappingStyle;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.remotecontroller.RemoteController;
import dji.sdk.sdkmanager.DJISDKManager;


/**
 * 遥控器
 */
public class RemoteControlFragment extends BaseFragment {

    FragmentRemoteControlBinding mBinding;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRemoteControlBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }


    @Override
    protected void initDatas() {
        mBinding.rgHandleType.setOnCheckedChangeListener(onCheckedChangeListener);
        if (ApronApp.getAircraftInstance() != null) {
            RemoteController remoteController = ApronApp.getAircraftInstance().getRemoteController();
            if (remoteController != null) {
                remoteController.getAircraftMappingStyle(new CommonCallbacks.CompletionCallbackWith<AircraftMappingStyle>() {
                    @Override
                    public void onSuccess(AircraftMappingStyle aircraftMappingStyle) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (aircraftMappingStyle) {
                                        case STYLE_1:
                                            mBinding.rbJ.setChecked(true);
                                            break;
                                        case STYLE_2:
                                            mBinding.rbA.setChecked(true);
                                            break;
                                        case STYLE_3:
                                            mBinding.rbC.setChecked(true);
                                            break;
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(DJIError djiError) {

                    }
                });
            }
        }
        mBinding.frequencyAlignment.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frequency_alignment:
                    Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
                    if (aircraft != null) {
                        RemoteController remoteController = aircraft.getRemoteController();
                        if (remoteController != null) {
                            remoteController.startPairing(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToast("开始对频失败：" + djiError.getDescription());
                                                }
                                            });
                                        }
                                    }else{
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToast("开始对频成功");
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        ToastUtil.showToast("未检测到固件");
                    }
                    break;
            }
        }
    };
    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (ApronApp.getAircraftInstance() != null) {
                RemoteController remoteController = ApronApp.getAircraftInstance().getRemoteController();
                if (remoteController != null) {
                    switch (checkedId) {
                        case R.id.rb_j:
                            remoteController.setAircraftMappingStyle(AircraftMappingStyle.STYLE_1, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToast("设置日本手失败：" + djiError.getDescription());
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                            break;
                        case R.id.rb_a:
                            remoteController.setAircraftMappingStyle(AircraftMappingStyle.STYLE_2, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToast("设置美国手失败：" + djiError.getDescription());
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                            break;
                        case R.id.rb_c:
                            remoteController.setAircraftMappingStyle(AircraftMappingStyle.STYLE_3, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        if (getActivity() != null) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ToastUtil.showToast("设置中国手失败：" + djiError.getDescription());
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                            break;
                    }
                }

            }
        }

    };
}
