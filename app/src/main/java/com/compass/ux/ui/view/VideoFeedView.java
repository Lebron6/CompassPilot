package com.compass.ux.ui.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.apron.mobilesdk.state.ProtoH264;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.xclog.XcFileLog;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.orhanobut.logger.Logger;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.util.concurrent.atomic.AtomicLong;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

/**
 * VideoView will show the live video for the given video feed.
 */
public class VideoFeedView extends TextureView implements SurfaceTextureListener {
    //region Properties
    private final static String TAG = "DULFpvWidget";
    private DJICodecManager codecManager = null;
    private VideoFeeder.VideoDataListener videoDataListener = null;
    private AtomicLong lastReceivedFrameTime = new AtomicLong(0);
    private Context context;

    public VideoFeedView(Context context) {
        this(context, null, 0);
    }

    public VideoFeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoFeedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public DJICodecManager getCodecManager() {
        return codecManager;
    }

    private void init() {
        // Avoid the rending exception in the Android Studio Preview view.
        if (isInEditMode()) {
            return;
        }
        setSurfaceTextureListener(this);
        videoDataListener = new VideoFeeder.VideoDataListener() {
            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (codecManager != null) {
                    codecManager.sendDataToDecoder(videoBuffer, size);
                }
//                Logger.e("H264:"+videoBuffer.toString());
//                if (client != null) {
//                    ProtoH264.H264.Builder builder=ProtoH264.H264.newBuilder();
//                    builder.setH264(ByteString.copyFrom(videoBuffer))
//                            .setSize(size);
//                    MqttMessage h264Message = new MqttMessage(builder.build().toByteArray());
//                    h264Message.setQos(1);
//                    publish(client, MqttConfig.MQTT_H264_TOPIC,h264Message);
//                }
            }
        };
    }

    public void publish(MqttAndroidClient client, String topic, MqttMessage message) {

        if (client.isConnected()) {
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
//                e.printStackTrace();
//                Logger.e(this.getClass().getSimpleName() + "推送H264失败:" + topic + e.toString());
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送H264失败:" + topic + e.toString());
            }
        } else {
//            Logger.e(this.getClass().getSimpleName()"推送H264失败:MQtt未连接");
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送H264失败:MQtt未连接");
        }

    }
    private MqttAndroidClient client;

    public VideoFeeder.VideoDataListener registerLiveVideo(VideoFeeder.VideoFeed videoFeed, MqttAndroidClient mqttAndroidClient) {
        this.client = mqttAndroidClient;
        if (videoDataListener != null && videoFeed != null && !videoFeed.getListeners().contains(videoDataListener)) {
            videoFeed.addVideoDataListener(videoDataListener);
            return videoDataListener;
        }
        return null;
    }

    //这四个 视频相关
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (codecManager == null) {
            Logger.e("onSurfaceTextureAvailable");
            codecManager = new DJICodecManager(context, surface, width, height);
            //For M300RTK, you need to actively request an I frame.
            codecManager.resetKeyFrame();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed");
        if (codecManager != null) {
            codecManager.cleanSurface();
            codecManager = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void changeSourceResetKeyFrame() {
        if (codecManager != null) {
            codecManager.resetKeyFrame();
        }
    }

}
