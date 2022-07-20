package com.compass.ux.manager;

import android.text.TextUtils;

import com.apron.mobilesdk.state.ProtoMessage;
import com.compass.ux.app.ApronApp;
import com.compass.ux.base.BaseManager;
import com.compass.ux.tools.AudioDecodeUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;

import java.nio.charset.Charset;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.payload.Payload;

/**
 * 喊话器
 */
public class SpeakerManager extends BaseManager {

    private byte voiceByteData[][];


    private SpeakerManager() {
    }

    private static class SpeakerHolder {
        private static final SpeakerManager INSTANCE = new SpeakerManager();
    }

    public static SpeakerManager getInstance() {
        return SpeakerManager.SpeakerHolder.INSTANCE;
    }

    /**
     * 喊话文本指令
     *
     * @param mqttAndroidClient
     * @param message
     */
    public void sendTTS2Payload(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        BaseProduct product = ApronApp.getProductInstance();
        if (product != null) {
            Payload payload = product.getPayload();
            if (payload != null) {
                String tts = message.getPara().get("word");
                if (TextUtils.isEmpty(tts)) {
                    sendErrorMsg2Server(mqttAndroidClient, message, "未检测到语音文本");
                    return;
                } else {
                    String fre = message.getPara().get("model");
                    String volume = message.getPara().get("volume");//音量
                    String tone = message.getPara().get("tone");//性别
                    String speed = message.getPara().get("speed");//语速

                    if (payload.getPayloadProductName().equals("MP130S")) {
                        if (speed.equals("11")) {//适配CZZN新版协议
                            speed = " --speed 100";
                        } else {
                            speed = " --speed " + speed + "0";
                        }
                        if (volume.equals("11")) {
                            volume = "100";
                        } else {
                            volume = volume + "0";
                        }
                        String sex = " --voice_name xiaoyan";
                        if (tone.equals("0")) {
                            sex = " --voice_name xiaoyan";
                        } else {
                            sex = " --voice_name xiaofeng";
                        }
                        byte[] bytesNomal = getUTF8Bytes(tts + sex + speed);
                        byte[] bytesLength = getDataHexLength(bytesNomal.length + 1);//这里length+1是MP130S固件bug
                        byte[] byteData = AudioDecodeUtils.dataCopy130S(AudioDecodeUtils.TTSINS130S, bytesNomal);

                        byte[] volumeByte = new byte[]{0x24, 0x00, 0x07, 0x53, hexToByte(Integer.toHexString(Integer.valueOf(volume))), 0x00, 0x23};
                        payload.sendDataToPayload(volumeByte, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    payload.sendDataToPayload(fre.equals("0") ? AudioDecodeUtils.TTSONEINSMP130S : AudioDecodeUtils.TTSREPEATINSMP130S, new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError == null) {
                                                payload.sendDataToPayload(bytesLength, new CommonCallbacks.CompletionCallback() {
                                                    @Override
                                                    public void onResult(DJIError djiError) {
                                                        if (djiError == null) {
                                                            payload.sendDataToPayload(byteData, new CommonCallbacks.CompletionCallback() {
                                                                @Override
                                                                public void onResult(DJIError djiError) {
                                                                    if (djiError == null) {
                                                                        sendCorrectMsg2Server(mqttAndroidClient, message, "喊话器已喊话");
                                                                    } else {
                                                                        sendErrorMsg2Server(mqttAndroidClient, message, "喊话失败:" + djiError.getDescription());

                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            sendErrorMsg2Server(mqttAndroidClient, message, "喊话器发送数组长度失败:" + djiError.getDescription());
                                                        }
                                                    }
                                                });
                                            } else {
                                                sendErrorMsg2Server(mqttAndroidClient, message, "喊话器设置循环方式失败:" + djiError.getDescription());
                                            }
                                        }
                                    });
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient, message, "喊话器发送音量失败:" + djiError.getDescription());
                                }
                            }
                        });
                    } else {
                        String sign = "[" + "d" + "]";//标记
                        String sex = "53";
                        if (tone.equals("0")) {
                            sex = "53";//男声
                        } else {
                            sex = "52";//女声
                        }
                        sign = "[" + "v" + volume + "]" + "[" + "s" + speed + "]" + "[" + "m" + sex + "]";//科大讯飞标记使用
                        byte[] content = AudioDecodeUtils.hexString2Bytes(AudioDecodeUtils.toChineseHex(sign + tts));
                        byte[] data = AudioDecodeUtils.dataCopy(AudioDecodeUtils.TTSINS, content);
                        payload.sendDataToPayload(fre.equals("0") ? AudioDecodeUtils.TTSONEINS : AudioDecodeUtils.TTSREPEATINS, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    payload.sendDataToPayload(data, new CommonCallbacks.CompletionCallback() {
                                        @Override
                                        public void onResult(DJIError djiError) {
                                            if (djiError == null) {
                                                sendCorrectMsg2Server(mqttAndroidClient, message, "喊话指令已发送");
                                            } else {
                                                sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                                            }
                                        }
                                    });
                                } else {
                                    sendErrorMsg2Server(mqttAndroidClient, message, djiError.getDescription());
                                }
                            }
                        });
                    }
                }

            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "Payload is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "Product is null");
        }
    }

    /**
     * 发送即时语音指令
     * @param mqttAndroidClient
     * @param message
     */
    public void sendMP32Payload(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        BaseProduct product = ApronApp.getProductInstance();
        if (product != null) {
            Payload payload = product.getPayload();
            if (payload != null) {
                String mp3 = message.getPara().get("MP3");
                if (TextUtils.isEmpty(mp3)) {
                    sendErrorMsg2Server(mqttAndroidClient, message, "未检测到喊话内容");
                    return;
                }
                payload.sendDataToPayload(AudioDecodeUtils.MP3STARTSINS, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            sendErrorMsg2Server(mqttAndroidClient, message, "发送音频开始上传音频指令出错:" + djiError.getDescription());
                        } else {
                            byte[] bytes = AudioDecodeUtils.hexString2Bytes(mp3);
                            voiceByteData = AudioDecodeUtils.splitBytes(bytes, 128);
                            upLoadMP3(voiceByteData[0], message);
                        }
                    }
                });
            } else {
                sendErrorMsg2Server(mqttAndroidClient, message, "Payload is null");
            }
        } else {
            sendErrorMsg2Server(mqttAndroidClient, message, "Product is null");
        }
    }

    /**
     * 上传音频文件,将结果回传给服务器
     *
     * @param mp3
     * @param message
     */
    private void upLoadMP3(byte[] mp3, ProtoMessage.Message message) {
        BaseProduct product = ApronApp.getProductInstance();
        if (product != null) {
            Payload payload = product.getPayload();
            if (payload != null) {
                payload.sendDataToPayload(mp3, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
//                            sendErrorMsg2Server(message, djiError.getDescription());
                        } else {
                            voiceByteData = AudioDecodeUtils.deleteAt(voiceByteData, 0);
                            if (voiceByteData.length > 0) {
                                upLoadMP3(voiceByteData[0], message);
                            } else {
                                payload.sendDataToPayload(AudioDecodeUtils.MP3STOPSINS, new CommonCallbacks.CompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if (djiError != null) {
//                                            sendErrorMsg2Server(message,"发送音频结束指令时出错:"+djiError.getDescription());
                                            return;
                                        } else {
                                            payload.sendDataToPayload(AudioDecodeUtils.MP3OPENINS, new CommonCallbacks.CompletionCallback() {
                                                @Override
                                                public void onResult(DJIError djiError) {
                                                    if (djiError != null) {
//                                                        sendErrorMsg2Server(message,"发送播放指令时出错:"+djiError.getDescription());
                                                    } else {
//                                                        sendCorrectMsg2Server(mqttAndroidClient,message,"开始播放音频");

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            } else {
//                sendErrorMsg2Server(message, "Payload is null");
            }
        } else {
//            sendErrorMsg2Server(message, "Product is null");
        }
    }


    /**
     * 发送停止播放文本指令到Payload
     *
     * @param mqttAndroidClient
     * @param message
     */
    public void sendTTSStop2Payload(MqttAndroidClient mqttAndroidClient, ProtoMessage.Message message) {
        BaseProduct product = ApronApp.getProductInstance();
        if (product != null) {
            Payload payload = product.getPayload();
            if (payload != null) {
                payload.sendDataToPayload(payload.getPayloadProductName().equals("MP130S") ? AudioDecodeUtils.TTSSTOPINSMP130S : AudioDecodeUtils.TTSSTOPINS, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "停止喊话失败:" + djiError.getDescription());
                        } else {
                            sendCorrectMsg2Server(mqttAndroidClient, message, "喊话已停止");
                        }
                    }
                });
            } else {
                sendCorrectMsg2Server(mqttAndroidClient, message, "payload is null");
            }
        } else {
            sendCorrectMsg2Server(mqttAndroidClient, message, "product is null");
        }

    }

    public static byte[] getUTF8Bytes(String data) {
        return getBytes(data, "utf-8");
    }


    private static byte[] getBytes(String data, String charsetName) {
        Charset charset = Charset.forName(charsetName);
        return data.getBytes(charset);
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 获取文本总长度
     */
    private byte[] getDataHexLength(int length) {
        String size = Integer.toHexString(length);
        if (size.length() == 1) {
            size = "000" + size;
        } else if (size.length() == 2) {
            size = "00" + size;
        } else if (size.length() == 3) {
            size = "0" + size;
        } else if (size.length() > 4) {
            size = size.substring(size.length() - 4);
        }
        String size1 = size.substring(0, 2);
        byte a = hexToByte(size1);
        String size2 = size.substring(2, 4);
        byte b = hexToByte(size2);

        byte[] lengthHex = new byte[]{0x24, 0x00, 0x08, 0x13, a, b, 0x00, 0x23};
        return lengthHex;

    }
}
