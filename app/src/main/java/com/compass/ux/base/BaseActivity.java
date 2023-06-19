package com.compass.ux.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.callback.MqttActionCallBack;
import com.compass.ux.callback.MqttCallBack;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.tools.AppManager;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class BaseActivity extends FragmentActivity {
    /**
     * activity堆栈管理
     */
    protected AppManager appManager = AppManager.getAppManager();

    public MqttAndroidClient mqttAndroidClient; //ltz change
    public MqttConnectOptions mMqttConnectOptions;


    protected String TAG;
    protected boolean useEventBus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loggerSimpleName();
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        appManager.addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    public void needConnect() {
//        if (nettyListener!=null) {
//            NettyClient.getInstance().setListener(nettyListener);
//            connect();
//        }
        initMqttClientParams();
    }

    public MqttAndroidClient getMqttInstance(){
        if (mqttAndroidClient==null){
            return new MqttAndroidClient(getApplicationContext(), MqttConfig.SOCKET_HOST, getRandomCode());
        }else{
            return mqttAndroidClient;
        }
    }

    public void initMqttClientParams() {
        getMqttInstance().setCallback(new MqttCallBack(getMqttInstance())); //设置监听订阅消息的回调
        mMqttConnectOptions = new MqttConnectOptions();
        mMqttConnectOptions.setAutomaticReconnect(true); //ltz add
        mMqttConnectOptions.setCleanSession(true); //设置是否清除缓存
        mMqttConnectOptions.setConnectionTimeout(10); //设置超时时间，单位：秒 ltz denote
        mMqttConnectOptions.setKeepAliveInterval(5); //设置心跳包发送间隔，单位：秒 ltz denote
        mMqttConnectOptions.setUserName(MqttConfig.USER_NAME); //设置用户名
        mMqttConnectOptions.setPassword(MqttConfig.USER_PASSWORD.toCharArray()); //设置密码
        doClientConnection();
    }

    public String getRandomCode() {
        String randomcode = "";
        // 用字符数组的方式随机
        String model = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        char[] m = model.toCharArray();
        for (int j = 0; j < 6; j++) {
            char c = m[(int) (Math.random() * 36)];
            // 保证六位随机数之间没有重复的
            if (randomcode.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            randomcode = randomcode + c;
        }
        return randomcode;
    }

    /**
     * 连接MQTT服务器
     */
    public void doClientConnection() {
        if (!getMqttInstance().isConnected() && isConnectIsNomarl()) {
            try {
                getMqttInstance().connect(mMqttConnectOptions, null, new MqttActionCallBack(getMqttInstance()));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连接
     */
    public boolean isConnectIsNomarl() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用Mqtt网络");
            /*没有可用网络的时候，延迟5秒再尝试重连*/
//            doConnectionDelay();
            return false;
        }
    }
    /*    */

    /**
     * 没有可用网络的时候，延迟5秒再尝试重连
     *//*
    private void doConnectionDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doClientConnection();
            }
        }, DELAY_MILLIS);
    }*/

//    private void connect() {
//        if (!NettyClient.getInstance().getConnectStatus()) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    NettyClient.getInstance().connect();//连接服务器
//                }
//            }).start();
//        }
//    }
    public abstract boolean useEventBus();

    public void loggerSimpleName() {
        TAG = getClass().getSimpleName();
//        Logger.e("当前界面 ："+ TAG);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 从栈中移除activity
        appManager.finishActivity(this);
        if (useEventBus == true) {
            EventBus.getDefault().unregister(this);
        }
        try {
            disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("注销异常"+e.toString()+"-----");
        }
//        NettyClient.getInstance().setReconnectNum(0);
//        NettyClient.getInstance().disconnect();
    }

    public  void disconnect() {
        try {
            if (getMqttInstance() != null) {
                if(getMqttInstance().isConnected())
                    publish(MqttConfig.MQTT_REGISTER_TOPIC);
                //getMqttInstance().unsubscribe(MqttConfig.MQTT_AIRLINK_STATUS);
                getMqttInstance().close();
                getMqttInstance().unregisterResources();
                mqttAndroidClient = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic) throws MqttException {
            ProtoMessage.Message.Builder builder=ProtoMessage.Message.newBuilder();
            builder.setMethod("offline").setRequestTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            MqttMessage registerMessage = new MqttMessage(builder.build().toByteArray());
            registerMessage.setQos(1);
            getMqttInstance().publish(topic, registerMessage);

    }
}
