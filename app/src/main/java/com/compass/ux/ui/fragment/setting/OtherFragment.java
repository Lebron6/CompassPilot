package com.compass.ux.ui.fragment.setting;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.viewbinding.ViewBinding;

import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseFragment;
import com.compass.ux.callback.LiveShowStatusCallback;
import com.compass.ux.databinding.FragmentGraphicBinding;
import com.compass.ux.databinding.FragmentOtherBinding;
import com.compass.ux.entity.DataCache;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.manager.StreamManager;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.xclog.XcFileLog;
import com.suke.widget.SwitchButton;

import dji.common.airlink.OcuSyncBandwidth;
import dji.common.airlink.OcuSyncFrequencyBand;
import dji.common.airlink.OcuSyncMagneticInterferenceLevel;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.airlink.AirLink;
import dji.sdk.airlink.OcuSyncLink;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;


/**
 * 其它设置
 */
public class OtherFragment extends BaseFragment {

    FragmentOtherBinding mBinding;


    @Override
    public boolean useEventBus() {
        return false;
    }

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentOtherBinding.inflate(layoutInflater, container, false);
        return mBinding;
    }


    @Override
    protected void initDatas() {
        if (!TextUtils.isEmpty(DataCache.getInstance().getRtmp_address())) {
            mBinding.tvLiveShowUrl.setText("rtmpAddr: " + DataCache.getInstance().getRtmp_address());
        }
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (DJISDKManager.getInstance().getLiveStreamManager() != null) {
                    mBinding.tvFps.setText("fps: " + DJISDKManager.getInstance().getLiveStreamManager().getLiveVideoFps());
                    mBinding.tvChannelBandWidth.setText("bitRate: " + DJISDKManager.getInstance().getLiveStreamManager().getLiveVideoBitRate() + "kpbs");
                }
                try {
                    mHandler.postDelayed(this, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
mBinding.btnStartLive.setOnClickListener(onClickListener);
mBinding.btnStopLive.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_start_live:
                    StreamManager.getInstance().startLiveShow(null, null);
                    break;
                case R.id.btn_stop_live:
                    StreamManager.getInstance().stopLiveShow(null, null);
                    break;
            }
        }
    };


}
