package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.compass.ux.R;
import com.compass.ux.api.BaseUrl;
import com.compass.ux.api.HttpUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.databinding.FragmentFlightControllerBinding;
import com.compass.ux.databinding.FragmentPersonalBinding;
import com.compass.ux.entity.UserInfo;
import com.compass.ux.tools.Helper;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.activity.MessageActivity;
import com.compass.ux.ui.activity.UpdataPasswordActivity;
import com.compass.ux.ui.window.DisconnectActionWindow;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.List;

import dji.common.error.DJIError;
import dji.common.flightcontroller.ConnectionFailSafeBehavior;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 飞控
 */
public class FlightControllerFragment extends BaseFragment {

    FragmentFlightControllerBinding mBinding;
    private List<String> times = new ArrayList<>();
    private ArrayAdapter arrayAdapter;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentFlightControllerBinding.inflate(layoutInflater, container, false);
        return mBinding;

    }

    @Override
    protected void initDatas() {
        times.add("悬停");
        times.add("降落");
        times.add("返航");
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_question, R.id.tv_popqusetion, times);

        mBinding.layoutShowWindow.setOnClickListener(onClickListener);
        if (Helper.isFlightControllerAvailable()) {
            FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
            flightController.getGoHomeHeightInMeters(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mBinding.sbGoHomeAltitude.setProgress(integer);
                                mBinding.tvGohomeHeigth.setText(integer+"m");
                            }
                        });
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
            flightController.getConnectionFailSafeBehavior(new CommonCallbacks.CompletionCallbackWith<ConnectionFailSafeBehavior>() {
                @Override
                public void onSuccess(ConnectionFailSafeBehavior connectionFailSafeBehavior) {
                    switch (connectionFailSafeBehavior.value()) {
                        case 0:
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvAction.setText("悬停");
                                    }
                                });
                            }
                            break;
                        case 1:
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvAction.setText("降落");
                                    }
                                });
                            }
                            break;
                        case 2:
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvAction.setText("返航");
                                    }
                                });
                            }
                            break;
                    }
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });
        } else {
            ToastUtil.showToast("飞行器未连接");
        }
        mBinding.sbGoHomeAltitude.setOnSeekChangeListener(onSeekChangeListener);
    }
    OnSeekChangeListener onSeekChangeListener=new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            switch (seekParams.seekBar.getId()) {
                case R.id.sb_go_home_altitude:
                    mBinding.tvGohomeHeigth.setText(seekParams.progress + "m");
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            if (Helper.isFlightControllerAvailable()) {
                FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                flightController.setGoHomeHeightInMeters(seekBar.getProgress(), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (getActivity()!=null){
                            if (djiError!=null){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast("设置返航高度失败："+djiError.getDescription());

                                    }
                                });
                            }else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast("返航高度已更新");
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_show_window:
                    DisconnectActionWindow timeSelectWindow = new DisconnectActionWindow(getActivity());
                    timeSelectWindow.showView(mBinding.layoutShowWindow, arrayAdapter, listener);
                    break;
            }
        }
    };
    OnDisconnectActionSelectedListener listener = new OnDisconnectActionSelectedListener() {
        @Override
        public void select(int postion) {
            mBinding.tvAction.setText(times.get(postion));
            if (Helper.isFlightControllerAvailable()) {
                FlightController flightController = ApronApp.getAircraftInstance().getFlightController();
                flightController.setConnectionFailSafeBehavior(ConnectionFailSafeBehavior.find(postion), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (getActivity() != null) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (djiError == null) {
                                        ToastUtil.showToast("失控行为已修改");
                                    } else {
                                        ToastUtil.showToast("失控行修改失败:" + djiError.getDescription());
                                    }
                                }
                            });
                        }

                    }
                });
            } else {
                ToastUtil.showToast("飞行器未连接");
            }
        }

    };
}
