package com.compass.ux.app;


import android.app.Application;
import android.content.Context;
import com.compass.ux.xclog.XcFileLog;
import com.compass.ux.xclog.XcLogConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.secneo.sdk.Helper;
import dji.common.product.Model;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointV2MissionOperator;
import dji.sdk.products.Aircraft;
import dji.sdk.products.HandHeld;
import dji.sdk.sdkmanager.DJISDKManager;


public class ApronApp extends Application{
    /**
     * SN码
     */
    public static String SERIAL_NUMBER ;//测试
    private static BaseProduct mProduct;
    private static Context context;
    public static Context getApplication() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        Helper.install(ApronApp.this);
        Logger.e("mapping");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        initConfig();
    }

    /**
     * Logger 初始化配置
     */
    private void initConfig() {
       PrettyFormatStrategy  formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // 隐藏线程信息 默认：显示
                .methodCount(0)         // 决定打印多少行（每一行代表一个方法）默认：2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//              .tag("JASON_LOGGER")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
//                return super.isLoggable(priority, tag);
                return true;
            }
        });
        XcFileLog.init(new XcLogConfig());
//        CrashHandler.getInstance().init();
    }

    public static synchronized BaseProduct getProductInstance() {
        if (null == mProduct) {
            mProduct = DJISDKManager.getInstance().getProduct();
        }
        return mProduct;
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected()) return null;
        return (Aircraft) getProductInstance();
    }

    public static boolean isM300Product() {
        if (DJISDKManager.getInstance().getProduct() == null) {
            return false;
        }
        Model model = DJISDKManager.getInstance().getProduct().getModel();
        return model == Model.MATRICE_300_RTK;
    }

    // 获取航点任务操作器
    public static WaypointV2MissionOperator getWaypointMissionOperator() {
        return MissionControl.getInstance().getWaypointMissionV2Operator();
    }

    public static synchronized Camera getCameraInstance() {

        if (getProductInstance() == null) return null;

        Camera camera = null;

        if (getProductInstance() instanceof Aircraft){
            camera = ((Aircraft) getProductInstance()).getCamera();

        } else if (getProductInstance() instanceof HandHeld) {
            camera = ((HandHeld) getProductInstance()).getCamera();
        }

        return camera;
    }
}
