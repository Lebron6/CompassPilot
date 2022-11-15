package com.compass.ux.ui.activity;

import static dji.sdk.codec.DJICodecManager.VideoSource.CAMERA;
import static dji.sdk.codec.DJICodecManager.VideoSource.FPV;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.compass.ux.BuildConfig;
import com.compass.ux.R;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.constant.Constant;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.DataCache;
import com.compass.ux.entity.LocalSource;
import com.compass.ux.manager.AccountManager;
import com.compass.ux.manager.AirLinkManager;
import com.compass.ux.manager.AssistantManager;
import com.compass.ux.manager.BatteryManager;
import com.compass.ux.manager.CameraManager;
import com.compass.ux.manager.DiagnosticsManager;
import com.compass.ux.manager.FlightManager;
import com.compass.ux.manager.GimbalManager;
import com.compass.ux.manager.MissionManager;
import com.compass.ux.manager.MissionV1Manager;
import com.compass.ux.manager.RTKManager;
import com.compass.ux.manager.StreamManager;
import com.compass.ux.manager.SystemManager;
import com.compass.ux.tools.AppManager;
import com.compass.ux.tools.DroneHelper;
import com.compass.ux.tools.OpenCVHelper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.view.LongTouchBtn;
import com.compass.ux.xclog.XcFileLog;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.aruco.Aruco;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.mission.waypointv2.WaypointV2MissionState;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.basestation.BaseStation;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;
import dji.sdk.useraccount.UserAccountManager;

public class MainActivity extends BaseActivity implements TextureView.SurfaceTextureListener {

    private static boolean isAppStarted = false;

    public static boolean isStarted() {
        return isAppStarted;
    }

    VideoFeeder videoFeeder;
    DJICodecManager codecManager;
    Aircraft aircraft;
    TextureView mTextureView;
    TextureView modifiedVideoStreamPreview;

    private LongTouchBtn btnUp, btnDown, btnForward, btnBackward, btnLeft, btnRight;

    private TextView tvFPS, tvBitRate, tvStreamUrl, tvFirmwareVersion;

    private Canvas canvas;
    private Bitmap edgeBitmap;
    private Dictionary dictionary;
    private CascadeClassifier faceDetector;
    private DroneHelper droneHelper;
    private OpenCVHelper openCVHelper;
    private Button btn_login;
    private Button btn_startlive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAppStarted = true;
        initViews();
        needConnect();
        openCVHelper = new OpenCVHelper(this);
        droneHelper = new DroneHelper();
        initDJIManager();
        intiVirtualStick();
        refreshSDKRelativeUI();
    }


    private void initViews() {
        tvFirmwareVersion = findViewById(R.id.tv_firmware_version);
        tvBitRate = findViewById(R.id.tv_bitRate);
        tvFPS = findViewById(R.id.tv_Fps);
        tvStreamUrl = findViewById(R.id.tv_stream_url);

        btn_login = findViewById(R.id.btn_login);
        btn_startlive = findViewById(R.id.btn_start_live);
        btn_startlive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StreamManager.getInstance().initStreamManager();
                StreamManager.getInstance().startLiveShow(null,null);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountManager.getInstance().loginAccount(MainActivity.this);
            }
        });
        mTextureView = findViewById(R.id.video_previewer_surface);
        modifiedVideoStreamPreview = findViewById(R.id.modified_videostream_preview);
        btnUp = findViewById(R.id.btn_up);
        btnDown = findViewById(R.id.btn_down);
        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnForward = findViewById(R.id.btn_forward);
        btnBackward = findViewById(R.id.btn_backward);
        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    int time = 0;
    boolean connectStatus;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String message) {
        switch (message) {
            case Constant.FLAG_CONNECT:
                initDJIManager();
                connectStatus = true;
                break;
            case Constant.FLAG_DISCONNECT:
                Logger.e("断开连接---");
//                connectStatus = false;
//                Handler mHandler = new Handler();
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (connectStatus == false) {
//                            if (time == 10) {
//                                SystemManager.getInstance().restartApp();
//                            } else {
//                                time = time + 1;
//                                try {
//                                    mHandler.postDelayed(this, 1000);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//
//                }, 1000);

                break;
            case Constant.VISUAL_ANGLE_TYPE:
                changeFPVOrGimbalView();
                break;
            case Constant.FLAG_START_DETECT_ARUCO:
                droneHelper.cancelLand(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            ToastUtil.showToast("取消返航失败");
                        } else {
                            Toast.makeText(MainActivity.this, "触发降落", Toast.LENGTH_SHORT).show();
                            Logger.e("触发降落" + "触发降落");
                            openCVHelper.startDetectAruco(droneHelper);
                            needDetectAruco = true;
                        }
                    }
                });

                break;
            case Constant.FLAG_DOWN_LAND:
                Toast.makeText(MainActivity.this, "直接降落", Toast.LENGTH_SHORT).show();
//                droneHelper.exitVirtualStickMode();
                droneHelper.land(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            Toast.makeText(MainActivity.this, "直接降落失败", Toast.LENGTH_SHORT).show();
                            Log.e("DroneHelper", "land failed: " + djiError.getDescription());
                        } else {
                            Toast.makeText(MainActivity.this, "直接降落成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                needDetectAruco = false;
                break;
            case Constant.FLAG_STREAM_URL:
                tvStreamUrl.setText("rtmpAddr: " + DataCache.getInstance().getRtmp_address());
                break;
        }
    }

    //更新当前连接信息
    private void refreshSDKRelativeUI() {
        aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null && aircraft.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(aircraft.getFirmwarePackageVersion())) {
                        tvFirmwareVersion.setText("固件版本: N/A");
                    } else {
                        tvFirmwareVersion.setText("固件版本: " + aircraft.getFirmwarePackageVersion());
                    }
                }
            });
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (DJISDKManager.getInstance().getLiveStreamManager() != null) {
                        tvFPS.setText("fps: " + DJISDKManager.getInstance().getLiveStreamManager().getLiveVideoFps());
                        tvBitRate.setText("bitRate: " + DJISDKManager.getInstance().getLiveStreamManager().getLiveVideoBitRate() + "kpbs");
                    }
                    try {
                        mHandler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        }
    }

    private void initDJIManager() {
        aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null && aircraft.isConnected()) {
            //图传
            initPreviewer();
            //飞控参数、RTK开关、避障等
            initFlightController();
            //遥控器、图传等
            initAirLink();
            //图传链路
            initOcuSyncLink();
            //避障、感知
            initAssistant();
            //电池信息
            initBattery();
            //云台
            initGimbal();
            //相机
            initCamera();
            //RTK
            initRTK();
            //警告信息
            initDiagnosticsInfomation();
            //推流
            initLiveStreamManager();
            //航线任务
            initMission();
            Button viewById = findViewById(R.id.btn_sl);
            viewById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseStation baseStation = ApronApp.getAircraftInstance().getBaseStation();
                    if (baseStation == null) {
                        showToast("无法获取实例");
                    } else {
                        showToast("已获取实例");
                    }
                }
            });

        } else {
            showToast("aircraft disconnect");
        }
    }

    private void initMission() {
        MissionManager.getInstance().initMissionInfo(mqttAndroidClient);
    }

    private void initCamera() {
        CameraManager.getInstance().initCameraInfo();
    }

    private void initAssistant() {
        AssistantManager.getInstance().initAssistant();
    }

    private void initRTK() {
        RTKManager.getInstance().initRTKInfo(mqttAndroidClient);
    }

    private void initGimbal() {
        GimbalManager.getInstance().initGimbalInfo(mqttAndroidClient);
    }

    private void initDiagnosticsInfomation() {
        DiagnosticsManager.getInstance().initDiagnosticsInfo(mqttAndroidClient);
    }

    private void initBattery() {
        BatteryManager.getInstance().initBatteryInfo(mqttAndroidClient);
    }

    private void initAirLink() {
        AirLinkManager.getInstance().initLinkInfo(mqttAndroidClient);
    }

    private void initOcuSyncLink() {
        AirLinkManager.getInstance().initOcuSyncLink();
    }

    private void initFlightController() {
        FlightManager.getInstance().initFlightInfo(this, mqttAndroidClient);
    }

    private void initLiveStreamManager() {
        StreamManager.getInstance().initStreamManager();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppStarted = false;
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initPreviewer() {
        if (mTextureView != null) {
            mTextureView.setSurfaceTextureListener(this);
        }
        videoFeeder = VideoFeeder.getInstance();
        if (videoFeeder != null) {
            videoFeeder.getPrimaryVideoFeed().addVideoDataListener(mReceivedVideoDataListener);
            videoFeeder.setTranscodingDataRate(8f);
        }
    }

    VideoFeeder.VideoDataListener mReceivedVideoDataListener = new VideoFeeder.VideoDataListener() {
        @Override
        public void onReceive(byte[] videoBuffer, int size) {
            if (codecManager != null) {
                codecManager.sendDataToDecoder(videoBuffer, size);
            }
        }
    };

    //改变云台视角
    public void changeFPVOrGimbalView() {
        if (codecManager != null) {
            DJICodecManager.VideoSource source = CAMERA;
            switch (LocalSource.getInstance().getCurrentVideoSource()) {
                case "1": //FPV视角
                    source = FPV;
                    break;
                case "0": //云台视角
                    source = CAMERA;
                    break;
            }
            codecManager.switchSource(source);
        } else {
            Toast.makeText(this, "切换视角失败,解码器初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_6X6_250);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                try {
                    // load cascade file from application resources
                    InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                    FileOutputStream os = new FileOutputStream(cascadeFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();

                    faceDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
                    if (faceDetector.empty()) {
                        showToast("Failed to load cascade classifier fo face detection");
                        faceDetector = null;
                    } else {
                        Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
                    }
                    cascadeDir.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                }
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (codecManager == null) {
            codecManager = new DJICodecManager(this, surface, width, height);
            //For M300RTK, you need to actively request an I frame.
//            codecManager.resetKeyFrame();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //Do nothing
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
        if (needDetectAruco == true) {
            codecManager.getBitmap(new DJICodecManager.OnGetBitmapListener() {
                @Override
                public void onGetBitmap(Bitmap bitmap) {
                    drawProcessedVideo(bitmap);
                }
            });
        }
    }

    public void drawProcessedVideo(Bitmap bitmap) {
        if (bitmap != null) {
            Mat source = new Mat();
            Utils.bitmapToMat(bitmap, source);
//            Mat processed = processImage(source);
            Mat processed = openCVHelper.detectArucoTags(source, dictionary, droneHelper);
            edgeBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

            Utils.matToBitmap(processed, edgeBitmap);
            canvas = modifiedVideoStreamPreview.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                if (BuildConfig.DEBUG) {
                    canvas.drawBitmap(edgeBitmap,
                            new Rect(0, 0, edgeBitmap.getWidth(), edgeBitmap.getHeight()),
                            new Rect((canvas.getWidth() - edgeBitmap.getWidth()) / 2,
                                    (canvas.getHeight() - edgeBitmap.getHeight()) / 2,
                                    (canvas.getWidth() - edgeBitmap.getWidth()) / 2 + edgeBitmap.getWidth(),
                                    (canvas.getHeight() - edgeBitmap.getHeight()) / 2
                                            + edgeBitmap.getHeight()),
                            null);
                }

                modifiedVideoStreamPreview.unlockCanvasAndPost(canvas);
                canvas.setBitmap(null);
                canvas = null;
                edgeBitmap.recycle();
                edgeBitmap = null;
            }
        }
    }

    boolean needDetectAruco = false;

    public Mat processImage(Mat input) {
        Mat output;
        if (needDetectAruco) {
            output = openCVHelper.detectArucoTags(input, dictionary, droneHelper);
        } else {
            output = openCVHelper.defaultImageProcessing(input);
        }
        return output;
    }


    private void intiVirtualStick() {
        FlightController flightController = droneHelper.fetchFlightController();
        if (flightController != null) {
            droneHelper.setVerticalModeToVelocity();
            btnUp.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, 0f, 0f, 1f);
                }
            }, 200);
            btnDown.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, 0f, 0f, -1f);
                }
            }, 200);
            btnForward.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, 0.5f, 0f, 0f);
                }
            }, 200);
            btnBackward.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0f, -0.5f, 0f, 0f);
                }
            }, 200);

            btnLeft.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(-0.8f, 0f, 0f, 0f);
                }
            }, 200);

            btnRight.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
                @Override
                public void onLongTouch() {
                    droneHelper.moveVxVyYawrateHeight(0.8f, 0f, 0f, 0f);
                }
            }, 200);
        }

    }

}
