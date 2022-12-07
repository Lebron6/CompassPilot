package com.compass.ux.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.apron.mobilesdk.state.ProtoMediaDownLoadProgress;
import com.apron.mobilesdk.state.ProtoMessage;
import com.autonavi.base.amap.mapcore.FileUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseActivity;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.databinding.ActivityGalleryBinding;
import com.compass.ux.entity.DownLoadFileList;
import com.compass.ux.entity.MyGallyData;
import com.compass.ux.tools.RecyclerViewHelper;
import com.compass.ux.tools.ToastUtil;
import com.compass.ux.ui.adapter.GalleryAdapter;
import com.compass.ux.ui.view.datescroller.DateScrollerDialog;
import com.compass.ux.ui.view.datescroller.data.Type;
import com.compass.ux.ui.view.datescroller.listener.OnDateSetListener;
import com.compass.ux.xclog.XcFileLog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.FetchMediaTask;
import dji.sdk.media.FetchMediaTaskContent;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.products.Aircraft;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GalleryActivity extends BaseActivity {

    private ActivityGalleryBinding mBinding;
    private static final long HUNDRED_YEARS = 100L * 365 * 1000 * 60 * 60 * 24L;
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private long mLastTime = System.currentTimeMillis();
    private long mLastFinishTime = System.currentTimeMillis();

    private List<MediaFile> mediaFileList = new ArrayList<MediaFile>();
    private MediaManager mMediaManager;
    private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    private FetchMediaTaskScheduler scheduler;
    GalleryAdapter adapter;
    File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/ApronPic/");
    private int downLoadIndex = 0;
    DownLoadFileList downLoadFileList = new DownLoadFileList();
    private int currentProgress = -1;

    @Override
    public boolean useEventBus() {
        return false;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, GalleryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initView();
    }

    private void initView() {
        mBinding.layoutFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new GalleryAdapter();
        RecyclerViewHelper.initRecyclerViewG(GalleryActivity.this, mBinding.rvPic, adapter, 2);
        mBinding.layoutTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateScroller();
            }
        });

        initMediaManager();
        mBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadPhoto();
            }
        });
    }

    private void initMediaManager() {
        if (ApronApp.getProductInstance() == null || ApronApp.getProductInstance().getCameras() == null) {
            ToastUtil.showToast("飞行器未连接");
            finish();
            return;
        }
        List<Camera> cameras = ((Aircraft) ApronApp.getProductInstance()).getCameras();
        if (null != cameras && cameras.get(0).isMediaDownloadModeSupported()) {
            mMediaManager = cameras.get(0).getMediaManager();
            scheduler = mMediaManager.getScheduler();
            if (null != mMediaManager) {
                if (isMavicAir2() || isM300()) {
                    ApronApp.getCameraInstance().enterPlayback(djiError -> {
                        if (djiError == null) {
                            DJILog.e(TAG, "Set cameraMode success");
                            getFileList();
                        } else {
                            setResultToToast("Set cameraMode failed");
                        }
                    });
                } else {
                    ApronApp.getCameraInstance().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, error -> {
                        if (error == null) {
                            DJILog.e(TAG, "Set cameraMode success");
                            getFileList();
                        } else {
                            setResultToToast("Set cameraMode failed");
                        }
                    });
                }

                if (mMediaManager.isVideoPlaybackSupported()) {
                    DJILog.e(TAG, "Camera support video playback!");
                } else {
                    setResultToToast("Camera does not support video playback!");
                }
            } else {

            }

        } else {
            setResultToToast("Media Download Mode not Supported");
        }

    }

    private void getFileList() {
        mMediaManager = ApronApp.getCameraInstance().getMediaManager();
        if (mMediaManager != null) {

            if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)) {
                setResultToToast("相机正忙");

            } else {
                mMediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, djiError -> {
                    if (null == djiError) {
                        mediaFileList = mMediaManager.getSDCardFileListSnapshot();
                        if (mediaFileList != null) {
                            Collections.sort(mediaFileList, (lhs, rhs) -> {
                                if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                    return 1;
                                } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                    return -1;
                                }
                                return 0;
                            });
                            scheduler.resume(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError error) {
                                    if (error == null) {
                                        getThumbnails();
                                    }
                                }
                            });
                        }
                    } else {
                        setResultToToast("Get Media File List Failed:" + djiError.getDescription());
                    }
                });
            }
        }
    }

    List<MyGallyData> myGallyDataList = new ArrayList<>();

    private FetchMediaTask.Callback taskCallback = new FetchMediaTask.Callback() {
        @Override
        public void onUpdate(MediaFile file, FetchMediaTaskContent option, DJIError error) {
            if (null == error) {
                if (option == FetchMediaTaskContent.PREVIEW) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MyGallyData myGallyData = new MyGallyData();
                            myGallyData.setMediaFile(file);
                            myGallyDataList.add(myGallyData);

                            adapter.setData(myGallyDataList);

                        }
                    });
                }
                if (option == FetchMediaTaskContent.THUMBNAIL) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            MyGallyData myGallyData = new MyGallyData();
                            myGallyData.setMediaFile(file);
                            myGallyDataList.add(myGallyData);
                            adapter.setData(myGallyDataList);
                        }
                    });
                }
            } else {
                DJILog.e(TAG, "Fetch Media Task Failed" + error.getDescription());
            }
        }
    };

    private void getThumbnails() {
        if (mediaFileList.size() <= 0) {
            setResultToToast("No File info for downloading thumbnails");
            return;
        }
        for (int i = 0; i < mediaFileList.size(); i++) {
            getThumbnailByIndex(i);
        }
    }

    private void getThumbnailByIndex(final int index) {
        FetchMediaTask task = new FetchMediaTask(mediaFileList.get(index), FetchMediaTaskContent.THUMBNAIL, taskCallback);
        scheduler.moveTaskToEnd(task);
    }

    public void downLoadPhoto() {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            FlightController flightController = aircraft.getFlightController();
            if (flightController != null) {
                if (flightController.getState().isFlying()) {
                    setResultToToast("飞行中无法下载");
                } else {
                    Camera camera = aircraft.getCamera();
                    if (camera != null) {
                        if (myGallyDataList != null && myGallyDataList.size() > 0) {
                            List<String> fileNames = new ArrayList<>();
                            for (int i = 0; i < myGallyDataList.size(); i++) {
                                if (myGallyDataList.get(i).isChecked()) {
                                    String fileName = myGallyDataList.get(i).getMediaFile().getFileName();
                                    fileNames.add(fileName);
                                }
                            }
                            downLoadFileList.setName(fileNames);
                        }
                        setResultToToast("开始下载,请不要退出当前页面");
                        downLoadIndex = 0;
                        downloadFileByIndex(downLoadFileList.getName().get(downLoadIndex), mqttAndroidClient);
                    } else {
                        setResultToToast("飞行器未挂载相机");
                    }
                }
            } else {
                setResultToToast("flightController is null");
            }
        } else {
            setResultToToast("aircraft disconnect");
        }
    }

    public void downloadFileByIndex(final String fileName, MqttAndroidClient mqttAndroidClient) {
        if (mediaFileList != null && mediaFileList.size() > 0) {
            for (int i = 0; i < mediaFileList.size(); i++) {
                if (fileName.equals(mediaFileList.get(i).getFileName())) {
                    mediaFileList.get(i).fetchFileData(destDir, null, new DownloadListener<String>() {
                        @Override
                        public void onFailure(DJIError error) {
                            setResultToToast("下载文件失败:" + fileName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBinding.tvFileDownloadProgress.setText("下载文件失败:" + error.getDescription());
                                }
                            });
                        }

                        @Override
                        public void onProgress(long total, long current) {
                        }

                        @Override
                        public void onRateUpdate(long total, long current, long persize) {
                            int tmpProgress = (int) (1.0 * current / total * 100);
                            if (tmpProgress != currentProgress) {
                                currentProgress = tmpProgress;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBinding.tvFileName.setText(fileName);
                                        mBinding.tvFileDownloadProgress.setText("下载进度:" + currentProgress + "%");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onRealtimeDataUpdate(byte[] bytes, long l, boolean b) {

                        }

                        @Override
                        public void onStart() {
                        }

                        @Override
                        public void onSuccess(String filePath) {

                            File file = new File(filePath + "/" + fileName);
                            Log.e("下载测试", "success" + filePath);
                            String type;
                            if (fileName.substring(fileName.length() - 3).equals("mp4")) {
                                type = ".mp4";
                            } else {
                                type = ".jpg";
                            }
                            OkHttpClient client = new OkHttpClient().newBuilder()
                                    .build();
                            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("file", file.getName(),
                                            RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"),
                                                    file))
                                    .addFormDataPart("type", type)
                                    .addFormDataPart("uavName", ApronApp.SERIAL_NUMBER)
                                    .build();
                            Request request = new Request.Builder()
                                    .url(MqttConfig.PHOTO_UPLOAD_ADDR + "/oauth/photo/upload")
                                    .method("POST", body)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    setResultToToast("上传文件失败:" + file.getName());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    FileUtil.deleteFile(file);
                                    downLoadIndex++;
                                    NumberFormat numberFormat = NumberFormat.getInstance();
                                    numberFormat.setMaximumFractionDigits(0);
                                    String result = numberFormat.format((float) downLoadIndex / (float) downLoadFileList.getName().size() * 100);
                                    if (downLoadIndex == downLoadFileList.getName().size()) {
                                        setResultToToast("媒体文件上传完毕");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mBinding.tvUploadProgress.setText("上传进度:" + result + "%");
                                                mBinding.tvFileName.setText("");
                                                mBinding.tvFileDownloadProgress.setText("");
                                                mBinding.tvUploadProgress.setText("");
                                                for (int j = 0; j < myGallyDataList.size(); j++) {
                                                    myGallyDataList.get(j).setChecked(false);
                                                }
                                                adapter.setData(myGallyDataList);

                                            }
                                        });
                                        return;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBinding.tvUploadProgress.setText("上传进度:" + result + "%");
                                        }
                                    });

                                    downloadFileByIndex(downLoadFileList.getName().get(downLoadIndex), mqttAndroidClient);
                                }
                            });
                        }
                    });
                    break;
                }
            }
        }
    }


    private void showDateScroller() {

        DateScrollerDialog dialog = new DateScrollerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
                .setTitleStringId("选择日期")
                .setMinMilliseconds(System.currentTimeMillis() - HUNDRED_YEARS)
                .setMaxMilliseconds(System.currentTimeMillis() + HUNDRED_YEARS)
                .setCurMilliseconds(mLastTime, mLastFinishTime)
                .setCallback(mOnDateSetListener)
                .build();
        if (dialog != null) {
            if (!dialog.isAdded()) {
                dialog.show(getSupportFragmentManager(), "year_month_day");
            }
        }

    }

    private OnDateSetListener mOnDateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DateScrollerDialog timePickerView, long milliseconds, long milliFinishseconds) {
            mLastTime = milliseconds;
            mLastFinishTime = milliFinishseconds;
            String text = getDateToString(milliseconds);
            String text2 = getDateToString(milliFinishseconds);
            mBinding.tvTime.setText(text + "-" + text2);
        }
    };

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void setResultToToast(final String result) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(GalleryActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isMavicAir2() {
        BaseProduct baseProduct = ApronApp.getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MAVIC_AIR_2;
        }
        return false;
    }

    private boolean isM300() {
        BaseProduct baseProduct = ApronApp.getProductInstance();
        if (baseProduct != null) {
            return baseProduct.getModel() == Model.MATRICE_300_RTK;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mMediaManager != null) {
            mMediaManager.stop(null);
            mMediaManager.exitMediaDownloading();
            if (scheduler != null) {
                scheduler.removeAllTasks();
            }
        }
        if (ApronApp.getCameraInstance() != null) {
            ApronApp.getCameraInstance().exitPlayback(djiError -> {
                if (djiError == null) {
                    DJILog.e(TAG, "exitPlayback success");
//                    getFileList();
                } else {
                    setResultToToast("exitPlayback failed");
                }
            });
        }


        if (mediaFileList != null) {
            mediaFileList.clear();
        }
        super.onDestroy();
    }

}
