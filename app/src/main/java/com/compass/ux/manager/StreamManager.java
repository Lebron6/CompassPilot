package com.compass.ux.manager;

import static dji.sdk.sdkmanager.LiveVideoBitRateMode.AUTO;
import static dji.sdk.sdkmanager.LiveVideoResolution.VIDEO_RESOLUTION_1920_1080;

import android.os.Handler;
import android.text.TextUtils;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.base.BaseManager;
import com.compass.ux.callback.LiveShowStatusCallback;
import com.compass.ux.entity.DataCache;
import com.compass.ux.entity.Communication;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.w3c.dom.Text;

import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;

/**
 * 推流
 */
public class StreamManager extends BaseManager {

    private StreamManager() {
    }

    private static class LiveStreamHolder {
        private static final StreamManager INSTANCE = new StreamManager();
    }

    public static StreamManager getInstance() {
        return StreamManager.LiveStreamHolder.INSTANCE;
    }

    public void initStreamManager() {
        LiveStreamManager liveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();
        if (liveStreamManager != null) {
            liveStreamManager.registerListener(new LiveShowStatusCallback());
        } else {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "initStreamManager:liveStreamManager is null");
        }
    }

    private boolean isLiveStreamManagerOn() {
        if (DJISDKManager.getInstance().getLiveStreamManager() == null) {
            return false;
        }
        return true;
    }

    public void startLiveShow(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        new Handler().postDelayed(new Runnable() {//测试延时两秒重启推流
            @Override
            public void run() {
                if (TextUtils.isEmpty(DataCache.getInstance().getRtmp_address())) {
                    ToastUtil.showToast("未获取到推流地址");
                    if (mqttAndroidClient != null) {
                        sendErrorMsg2Server(mqttAndroidClient, message, "未获取到推流地址");
                    }
                } else {
                    if (isLiveStreamManagerOn()) {
                        LiveStreamManager liveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();
                        liveStreamManager.stopStream();
                        liveStreamManager.setLiveUrl(DataCache.getInstance().getRtmp_address());
                        liveStreamManager.setAudioStreamingEnabled(false);
                        liveStreamManager.setAudioMuted(false);
                        liveStreamManager.setVideoEncodingEnabled(true);
                        liveStreamManager.setLiveVideoBitRateMode(AUTO);
                        liveStreamManager.setLiveVideoResolution(VIDEO_RESOLUTION_1920_1080);//分辨率低，FPS越高
                        liveStreamManager.setVideoSource(LiveStreamManager.LiveStreamVideoSource.Primary);
                        int result = liveStreamManager.startStream();
                        liveStreamManager.setStartTime();
                        Logger.e("startLive:" + result + "-" + DataCache.getInstance().getRtmp_address());
                        if (result==0){
                            ToastUtil.showToast("推流成功,正在直播");

                        }else{
                            ToastUtil.showToast("推流失败:"+result);

                        }
                        if (mqttAndroidClient != null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "重启推流:" + String.valueOf(result));
                        }
                    } else {
                        if (mqttAndroidClient != null) {
                            sendErrorMsg2Server(mqttAndroidClient, message, "重启推流失败");

                        }
                    }
                }
            }
        }, 2000);
        Logger.e("startLive:" + "-" + DataCache.getInstance().getRtmp_address());


    }

    public void stopLiveShow(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (!isLiveStreamManagerOn()) {
            sendErrorMsg2Server(mqttAndroidClient, message, "推流暂未开始");
        } else {
            DJISDKManager.getInstance().getLiveStreamManager().stopStream();
            sendErrorMsg2Server(mqttAndroidClient, message, "推流已结束");
        }
    }

    public void restartLiveShow(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        int delayTime;
        if (message != null && message.getPara() != null) {
            String delay = message.getPara().get("delay");
            if (TextUtils.isEmpty(delay)) {
                delayTime = 2000;
            } else {
                delayTime = (Integer.valueOf(delay)) * 1000;
            }
        } else {
            delayTime = 1000;
        }
        if (!isLiveStreamManagerOn()) {
            return;
        }
        DJISDKManager.getInstance().getLiveStreamManager().stopStream();
        new Handler().postDelayed(new Runnable() {//测试延时两秒重启推流
            @Override
            public void run() {
                startLiveShow(mqttAndroidClient, message);
            }
        }, delayTime);

    }
}
