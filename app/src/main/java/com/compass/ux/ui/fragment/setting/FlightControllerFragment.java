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
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.OnDisconnectActionSelectedListener;
import com.compass.ux.databinding.FragmentFlightControllerBinding;
import com.compass.ux.databinding.FragmentPersonalBinding;
import com.compass.ux.entity.UserInfo;
import com.compass.ux.tools.PreferenceUtils;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.activity.MessageActivity;
import com.compass.ux.ui.activity.UpdataPasswordActivity;
import com.compass.ux.ui.window.DisconnectActionWindow;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 飞控
 */
public class FlightControllerFragment extends BaseFragment {

    FragmentFlightControllerBinding mBinding;
    private List<String> times=new ArrayList<>();
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
        times.add("返航");
        times.add("悬停");
        times.add("降落");
        arrayAdapter = new ArrayAdapter(getActivity(), R.layout.item_question, R.id.tv_popqusetion, times);

        mBinding.layoutShowWindow.setOnClickListener(onClickListener);
    }

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

        }
    };
}
