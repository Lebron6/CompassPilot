package com.compass.ux.manager;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.apron.mobilesdk.state.ProtoMediaDownLoadProgress;
import com.apron.mobilesdk.state.ProtoMessage;
import com.autonavi.base.amap.mapcore.FileUtil;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.constant.MqttConfig;
import com.compass.ux.entity.DeleteFileList;
import com.compass.ux.entity.DownLoadFileList;
import com.compass.ux.entity.PicUpload;
import com.compass.ux.entity.Thum;
import com.compass.ux.tools.ImageUtils;
import com.compass.ux.xclog.XcFileLog;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJICameraError;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
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

/**
 * 媒体文件管理
 */
public class PlayBackManager extends BaseManager {

    private List<MediaFile> mediaFileList = new ArrayList<>();
    private List<Thum> thums = new ArrayList<>();
    private MediaManager mediaManager;
    private MediaManager.FileListState currentFileListState = MediaManager.FileListState.UNKNOWN;
    private FetchMediaTaskScheduler scheduler;
    private int downLoadIndex = 0;
    DownLoadFileList downLoadFileList;
    File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/ApronPic/");
    private int currentProgress = -1;

    private PlayBackManager() {
    }

    private static class PlayBackManagerHolder {
        private static final PlayBackManager INSTANCE = new PlayBackManager();
    }

    public static PlayBackManager getInstance() {
        return PlayBackManager.PlayBackManagerHolder.INSTANCE;
    }

    public void photoAlbum(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        mediaFileList.clear();
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            FlightController flightController = aircraft.getFlightController();
            if (flightController != null) {
                if (flightController.getState().isFlying()) {
                    sendErrorMsg2Server(mqttAndroidClient, message, "飞行中无法查看相册");
                } else {
                    Camera camera = aircraft.getCamera();
                    if (camera != null) {
                        if (camera.isMediaDownloadModeSupported()) {
                            camera.enterPlayback(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError == null) {
                                        mediaManager = camera.getMediaManager();
                                        if (mediaManager != null) {
                                            mediaManager.addUpdateFileListStateListener(updateFileListStateListener);
                                            scheduler = mediaManager.getScheduler();
                                            try {
                                                TimeUnit.SECONDS.sleep(1);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if ((currentFileListState == MediaManager.FileListState.SYNCING) || (currentFileListState == MediaManager.FileListState.DELETING)) {
                                                sendErrorMsg2Server(mqttAndroidClient, message, "打开相册失败:" + "Media Manager is busy");
                                            } else {
                                                mediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.SDCARD, new CommonCallbacks.CompletionCallback() {
                                                    @Override
                                                    public void onResult(DJIError djiError) {
                                                        if (djiError == null) {
                                                            List<MediaFile> fileList = mediaManager.getSDCardFileListSnapshot();
                                                            if (fileList != null && fileList.size() > 0) {
                                                                Collections.sort(fileList, (lhs, rhs) -> {
                                                                    if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                                                                        return 1;
                                                                    } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                                                                        return -1;
                                                                    }
                                                                    return 0;
                                                                });

                                                                for (int i = 0; i < fileList.size(); i++) {
//                                                                    if (fileList.get(i).getMediaType() == MediaFile.MediaType.JPEG) {
                                                                    mediaFileList.add(fileList.get(i));
//                                                                    }
                                                                }
                                                                scheduler.resume(new CommonCallbacks.CompletionCallback() {
                                                                    @Override
                                                                    public void onResult(DJIError error) {
                                                                        if (error == null) {

                                                                            int page = Integer.parseInt(message.getPara().get("pageNum"));
                                                                            int size = Integer.parseInt(message.getPara().get("pageSize"));
                                                                            if (thums != null) {
                                                                                thums.clear();
                                                                            }
                                                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                                                List<MediaFile> collect = mediaFileList.stream().skip(page * size).limit(size).collect(Collectors.toList());
                                                                                Iterator iterator = collect.iterator();
                                                                                while (iterator.hasNext()) {
                                                                                    FetchMediaTask task = new FetchMediaTask((MediaFile) iterator.next(), FetchMediaTaskContent.THUMBNAIL, new FetchMediaTask.Callback() {
                                                                                        @Override
                                                                                        public void onUpdate(MediaFile file, FetchMediaTaskContent fetchMediaTaskContent, DJIError error) {
                                                                                            if (null == error) {
                                                                                                Thum thum = new Thum();
                                                                                                thum.setName(file.getFileName());
                                                                                                thum.setSize(file.getFileSize());
                                                                                                thum.setThum(ImageUtils.bitmapToBase64(file.getThumbnail()));
                                                                                                thums.add(thum);
                                                                                                if (file.getFileName().equals(collect.get(collect.size() - 1).getFileName())) {
                                                                                                    PicUpload picUpload = new PicUpload();
                                                                                                    picUpload.setPicCount(mediaFileList.size());
                                                                                                    picUpload.setThums(thums);
                                                                                                    String msg = new Gson().toJson(picUpload);
                                                                                                    sendCorrectMsg2Server(mqttAndroidClient, message, msg);
                                                                                                }
                                                                                            } else {
                                                                                                sendErrorMsg2Server(mqttAndroidClient, message, "打开相册失败:" + "Fetch Media Task Failed" + error.getDescription());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    scheduler.moveTaskToEnd(task);
                                                                                }
                                                                            } else {
                                                                                sendErrorMsg2Server(mqttAndroidClient, message, "打开相册失败:" + "collect error");
                                                                            }
                                                                        } else {
                                                                            sendErrorMsg2Server(mqttAndroidClient, message, "打开相册调度器失败:" + error.getDescription());
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                sendErrorMsg2Server(mqttAndroidClient, message, "暂无媒体文件");
                                                            }
                                                        } else {
                                                            sendErrorMsg2Server(mqttAndroidClient, message, "refreshFileListOfStorageLocation:" + djiError.getDescription());
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            sendErrorMsg2Server(mqttAndroidClient, message, "打开相册失败:" + "mediaManager is null");
                                        }
                                    } else {
                                        sendErrorMsg2Server(mqttAndroidClient, message, "打开相册失败:" + djiError.getDescription());
                                    }
                                }
                            });
                        } else {
                            sendErrorMsg2Server(mqttAndroidClient, message, "固件不支持访问媒体文件");
                        }
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient, message, "飞行器未挂载相机");
                    }
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    //Listeners
    private MediaManager.FileListStateListener updateFileListStateListener = new MediaManager.FileListStateListener() {
        @Override
        public void onFileListStateChange(MediaManager.FileListState state) {
            currentFileListState = state;
        }
    };

    public void exitPlayback(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            FlightController flightController = aircraft.getFlightController();
            if (flightController != null) {
                if (flightController.getState().isFlying()) {
                    sendErrorMsg2Server(mqttAndroidClient, message, "飞行中无法关闭相册");
                } else {
                    Camera camera = aircraft.getCamera();
                    if (camera != null) {
                        camera.exitPlayback(new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError != null) {
                                    sendErrorMsg2Server(mqttAndroidClient, message, "退出媒体模式失败:" + djiError.getDescription());
                                } else {
                                    sendCorrectMsg2Server(mqttAndroidClient, message, "已退出媒体模式");
                                }
                            }
                        });
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient, message, "飞行器未挂载相机");
                    }
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }


    public void downLoadPhoto(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        Aircraft aircraft = ApronApp.getAircraftInstance();
        if (aircraft != null) {
            FlightController flightController = aircraft.getFlightController();
            if (flightController != null) {
                if (flightController.getState().isFlying()) {
                    sendErrorMsg2Server(mqttAndroidClient, message, "飞行中无法下载");
                } else {
                    Camera camera = aircraft.getCamera();
                    if (camera != null) {
                        String photoName = message.getPara().get("photoName");
                        if (TextUtils.isEmpty(photoName)) {
                            sendErrorMsg2Server(mqttAndroidClient, message, "文件名参数有误");
                            return;
                        }
                        sendCorrectMsg2Server(mqttAndroidClient,message,"正在下载");
                        downLoadFileList = new Gson().fromJson(photoName, DownLoadFileList.class);
                        downLoadIndex = 0;
                        downloadFileByIndex(downLoadFileList.getName().get(downLoadIndex), mqttAndroidClient);
                    } else {
                        sendErrorMsg2Server(mqttAndroidClient, message, "飞行器未挂载相机");
                    }
                }
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "flightController is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "aircraft disconnect");
        }
    }

    public void downloadFileByIndex(final String fileName, MqttAndroidClient mqttAndroidClient) {
        if (mediaFileList != null && mediaFileList.size() > 0) {
            for (int i = 0; i < mediaFileList.size(); i++) {
                if (fileName.equals(mediaFileList.get(i).getFileName())) {
                    mediaFileList.get(i).fetchFileData(destDir, null, new DownloadListener<String>() {
                        @Override
                        public void onFailure(DJIError error) {
                            XcFileLog.getInstace().i("下载失败:" + error.getDescription(), fileName);
                            ProtoMediaDownLoadProgress.MediaDownLoadProgress mediaDownLoadProgress =
                                    ProtoMediaDownLoadProgress.MediaDownLoadProgress.newBuilder().setMsg("下载失败:" + error.getDescription() + ",请尝试重新下载").build();
                            MqttMessage mediaDownLoadProgressMessage = new MqttMessage(mediaDownLoadProgress.toByteArray());
                            mediaDownLoadProgressMessage.setQos(1);
                            publish(mqttAndroidClient, MqttConfig.MQTT_MEDIA_DOWNLOAD_PROGRESS, mediaDownLoadProgressMessage);

                        }

                        @Override
                        public void onProgress(long total, long current) {
                        }

                        @Override
                        public void onRateUpdate(long total, long current, long persize) {
                            int tmpProgress = (int) (1.0 * current / total * 100);
                            if (tmpProgress != currentProgress) {
                                currentProgress = tmpProgress;
                                Log.e("下载进度", fileName + "--" + currentProgress + "");
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
                                    .url( MqttConfig.PHOTO_UPLOAD_ADDR+"/oauth/photo/upload")
                                    .method("POST", body)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("上传失败", e.toString());
                                    XcFileLog.getInstace().i("上传文件失败:", file.getName() + "---");
                                    ProtoMediaDownLoadProgress.MediaDownLoadProgress mediaDownLoadProgress =
                                            ProtoMediaDownLoadProgress.MediaDownLoadProgress.newBuilder().setMsg("上传图片失败").build();
                                    MqttMessage mediaDownLoadProgressMessage = new MqttMessage(mediaDownLoadProgress.toByteArray());
                                    mediaDownLoadProgressMessage.setQos(1);
                                    publish(mqttAndroidClient, MqttConfig.MQTT_MEDIA_DOWNLOAD_PROGRESS, mediaDownLoadProgressMessage);
//
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    FileUtil.deleteFile(file);
                                    downLoadIndex++;
//
                                    NumberFormat numberFormat = NumberFormat.getInstance();
                                    numberFormat.setMaximumFractionDigits(0);
                                    String result = numberFormat.format((float) downLoadIndex / (float) downLoadFileList.getName().size() * 100);
//
                                    if (downLoadIndex == downLoadFileList.getName().size()) {
                                        ProtoMediaDownLoadProgress.MediaDownLoadProgress mediaDownLoadProgress =
                                                ProtoMediaDownLoadProgress.MediaDownLoadProgress.newBuilder().setMsg(result).build();
                                        MqttMessage mediaDownLoadProgressMessage = new MqttMessage(mediaDownLoadProgress.toByteArray());
                                        mediaDownLoadProgressMessage.setQos(1);
                                        publish(mqttAndroidClient, MqttConfig.MQTT_MEDIA_DOWNLOAD_PROGRESS, mediaDownLoadProgressMessage);
                                        Log.e("已下载完成", "success");
                                        downLoadIndex = 0;
                                        return;
                                    }
                                    ProtoMediaDownLoadProgress.MediaDownLoadProgress mediaDownLoadProgress =
                                            ProtoMediaDownLoadProgress.MediaDownLoadProgress.newBuilder().setMsg(result).build();
                                    MqttMessage mediaDownLoadProgressMessage = new MqttMessage(mediaDownLoadProgress.toByteArray());
                                    mediaDownLoadProgressMessage.setQos(1);
                                    publish(mqttAndroidClient, MqttConfig.MQTT_MEDIA_DOWNLOAD_PROGRESS, mediaDownLoadProgressMessage);
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
    DeleteFileList deleteFileList;

    public void deletePhoto(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String photoName = message.getPara().get("photoName");
        if (TextUtils.isEmpty(photoName)) {
            sendErrorMsg2Server(mqttAndroidClient,message, "文件名参数有误");
            return;
        }
        deleteFileList = new Gson().fromJson(photoName, DeleteFileList.class);
        ArrayList<MediaFile> fileToDelete = new ArrayList<MediaFile>();
        if (mediaFileList != null && mediaFileList.size() > 0) {
            for (int i = 0; i < mediaFileList.size(); i++) {
                for (int j = 0; j < deleteFileList.getName().size(); j++) {
                    if (mediaFileList.get(i).getFileName().equals(deleteFileList.getName().get(j))) {
                        fileToDelete.add(mediaFileList.get(i));
                    }
                }
            }
        }
        deleteFileByIndex(fileToDelete, mqttAndroidClient,message);
    }
    public void deleteFileByIndex(ArrayList<MediaFile> files, MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        if (files != null && files.size() > 0) {
            mediaManager.deleteFiles(files, new CommonCallbacks.CompletionCallbackWithTwoParam<List<MediaFile>, DJICameraError>() {
                @Override
                public void onSuccess(List<MediaFile> x, DJICameraError y) {
                    mediaFileList.removeAll(files);
                    sendCorrectMsg2Server(mqttAndroidClient,message,"删除成功");
                }

                @Override
                public void onFailure(DJIError error) {
                    sendErrorMsg2Server(mqttAndroidClient,message,"Delete file failed:" + error.getDescription());
                }
            });
        } else {
            sendErrorMsg2Server(mqttAndroidClient,message,"Delete file failed:" + "待删除列表为空");

        }
    }

    public void downLoadPreview(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        String photoName = message.getPara().get("photoName");
        if (TextUtils.isEmpty(photoName)) {
            sendErrorMsg2Server(mqttAndroidClient,message, "文件名参数有误");
            return;
        }
        if (mediaFileList != null && mediaFileList.size() > 0) {
            for (int i = 0; i < mediaFileList.size(); i++) {
                if (mediaFileList.get(i).getFileName().equals(photoName)) {
                    MediaFile mediaFile = mediaFileList.get(i);
                    FetchMediaTask task = new FetchMediaTask(mediaFile, FetchMediaTaskContent.PREVIEW, new FetchMediaTask.Callback() {
                        @Override
                        public void onUpdate(MediaFile file, FetchMediaTaskContent fetchMediaTaskContent, DJIError error) {
                            if (null == error) {
                                Thum thum = new Thum();
                                thum.setName(file.getFileName());
                                thum.setSize(file.getFileSize());
                                thum.setPreview(ImageUtils.bitmapToBase64(file.getPreview()));
                                Log.e("上传测试预览图1", new Gson().toJson(thum));
                                sendCorrectMsg2Server(mqttAndroidClient,message,  new Gson().toJson(thum));
                            } else {
                                sendErrorMsg2Server(mqttAndroidClient,message,   "Fetch Media Task Failed :" + error.getDescription());
                            }
                        }
                    });
                    scheduler.moveTaskToEnd(task);
                    break;
                }
            }
        }
    }


    public void publish(MqttAndroidClient client, String topic, MqttMessage message) {

        if (client.isConnected()) {
            try {
                client.publish(topic, message);
            } catch (MqttException e) {
                e.printStackTrace();
                XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:" + topic + e.toString());
                Logger.e("推送失败：" + topic);
            }
        } else {
            XcFileLog.getInstace().i(this.getClass().getSimpleName(), "推送失败:MQtt未连接");
        }

    }

}
