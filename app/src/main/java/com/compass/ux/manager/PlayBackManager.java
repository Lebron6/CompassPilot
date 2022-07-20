package com.compass.ux.manager;

import android.text.TextUtils;
import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.entity.DownLoadFileList;
import com.compass.ux.entity.PicUpload;
import com.compass.ux.entity.Thum;
import com.compass.ux.tools.ImageUtils;
import com.google.gson.Gson;
import org.eclipse.paho.android.service.MqttAndroidClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.media.FetchMediaTask;
import dji.sdk.media.FetchMediaTaskContent;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;
import dji.sdk.products.Aircraft;

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
                               if (djiError!=null){
                                   sendErrorMsg2Server(mqttAndroidClient,message,"推出媒体模式失败:"+djiError.getDescription());
                               }else{
                                  sendCorrectMsg2Server(mqttAndroidClient,message,"已退出媒体模式");
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
                        downLoadFileList = new Gson().fromJson(photoName, DownLoadFileList.class);
                        downLoadIndex = 0;
//                        downloadFileByIndex(downLoadFileList.getName().get(downLoadIndex));
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

}
