package com.compass.ux.tools;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.InputStream;
import java.util.Arrays;

public class AudioDecodeUtils {

    public static final byte[] HEADER = {0x24};//帧头
    public static final byte[] VERI = {0x00};//校验
    public static final byte[] TAIL = {0x23};//帧尾

    public static final byte[] TTSINS = {0x54};//文本指令
    public static final byte[] VOLUMECTRINS = {0x56};//音量调节
    public static final byte[] MP3STARTSINS = {0x24,0x00,0x07,0x75,0x03,0x00,0x23};//开始上传音频
    public static final byte[] MP3STOPSINS = {0x24,0x00,0x07,0x75,0x04,0x00,0x23};//停止上传音频
    public static final byte[] MP3OPENINS = {0x24,0x00,0x07,0x7B,0x04,0x00,0x23};//播放音频
    public static final byte[] TTSREPEATINS = {0x24, 0x00, 0x07, 0x73, 0x65, 0x00, 0x23};//循环播放文本指令
    public static final byte[] TTSONEINS = {0x24, 0x00, 0x07, 0x73, 0x66, 0x00, 0x23};//单次播放文本指令
    public static final byte[] TTSSTOPINS = {0x24, 0x00, 0x0a, 0x79, (byte) 0xFD, 0x00, 0x01, 0x02, 0x00, 0x23};//文本停止

    public static final byte[] VERI130S = {0x00,0x00};//校验
    public static final byte[] TTSINS130S = {0x14};//文本指令

    public  static final byte[] TTSREPEATINSMP130S = {0x24,0x00,0x07,0x11,(byte)0xC2,0x00,0x23};//循环播放文本指令
    public static final byte[] TTSONEINSMP130S = {0x24, 0x00, 0x07, 0x11, (byte)0xC1, 0x00, 0x23};//单次播放文本指令
    public static final byte[] TTSSTOPINSMP130S = {0x24, 0x00, 0x07, 0x11, (byte) 0xF2, 0x00, 0x23};//文本停止

    //开灯
    public static final  byte[] OPEN_LIGHT = {0x24,0x00,0x07,0x6A, (byte) 0xF1,0x00,0x23};
    //关灯
    public static final  byte[] CLOSE_LIGHT = {0x24,0x00,0x07,0x6A, (byte) 0xF2,0x00,0x23};
    //接收到开灯状态
    public static final  byte[] REC_OPEN_LIGHT = {0x24,0x00,0x07,0x6B, (byte) 0x01,0x00,0x23};
    //接收到关灯状态
    public static final  byte[] REC_CLOSE_LIGHT = {0x24,0x00,0x07,0x6B, (byte) 0x00,0x00,0x23};
    /**
     * 文本转16进制字符串
     *
     * @param s
     * @return
     */
    public static String toChineseHex(String s) {
        String ss = s;
        byte[] bt = new byte[0];
        try {
            bt = ss.getBytes("GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s1 = "";
        for (int i = 0; i < bt.length; i++) {
            String tempStr = Integer.toHexString(bt[i]);
            if (tempStr.length() > 2)
                tempStr = tempStr.substring(tempStr.length() - 2);
            s1 = s1 + tempStr + "";
        }
        return s1.toUpperCase();
    }

    /**
     * 拼接指令
     * @param ins
     * @param content
     * @return
     */
    public static byte[] dataCopy(byte[] ins, byte[] content) {
        byte[] size =intToBytes(6+ content.length);//包长转byte数组格式[0x00,0x0A]
        byte[] data = new byte[HEADER.length + size.length + ins.length + content.length+VERI.length + TAIL.length];
        System.arraycopy(HEADER, 0, data, 0, HEADER.length);//数据头
        System.arraycopy(size, 0, data, HEADER.length, size.length);//数据位长度
        System.arraycopy(ins, 0, data, HEADER.length + size.length, ins.length);//指令
        System.arraycopy(content, 0, data, HEADER.length + size.length + ins.length, content.length);//文本
        System.arraycopy(VERI, 0, data, HEADER.length + size.length + ins.length+content.length, VERI.length);//校验
        System.arraycopy(TAIL, 0, data, HEADER.length + size.length + ins.length + content.length+VERI.length, TAIL.length);//帧尾
        return data;
    }


    /**
     * 拼接指令
     * @param ins
     * @param content
     * @return
     */
    public static byte[] dataCopy130S(byte[] ins, byte[] content) {
        byte[] size =intToBytes(7+ content.length);//包长转byte数组格式[0x00,0x0A],这里的6表示除了数据长度外,加上帧头帧尾指令位效验,MP130S应该是7
        byte[] data = new byte[HEADER.length + size.length + ins.length + content.length+VERI130S.length + TAIL.length];
        System.arraycopy(HEADER, 0, data, 0, HEADER.length);//数据头
        System.arraycopy(size, 0, data, HEADER.length, size.length);//数据位长度
        System.arraycopy(ins, 0, data, HEADER.length + size.length, ins.length);//指令
        System.arraycopy(content, 0, data, HEADER.length + size.length + ins.length, content.length);//文本
        System.arraycopy(VERI130S, 0, data, HEADER.length + size.length + ins.length+content.length, VERI130S.length);//校验
        System.arraycopy(TAIL, 0, data, HEADER.length + size.length + ins.length + content.length+VERI130S.length, TAIL.length);//帧尾
        return data;
    }

    /**
     * 拆分byte数组
     * @param bytes
     * 要拆分的数组
     * @param size
     * 要按几个组成一份
     * @return
     */
    public static byte[][] splitBytes(byte[] bytes, int size) {
        double splitLength = Double.parseDouble(size + "");
        int arrayLength = (int) Math.ceil(bytes.length / splitLength);
        byte[][] result = new byte[arrayLength][];
        int from, to;
        for (int i = 0; i < arrayLength; i++) {
            from = (int) (i * splitLength);
            to = (int) (from + splitLength);
            if (to > bytes.length)
                to = bytes.length;
            result[i] = Arrays.copyOfRange(bytes, from, to);
        }
        return result;
    }

    /**
     * int到byte[] 由高位到低位
     *
     * @param value 需要转换为byte数组的整行值。
     * @return byte数组
     */

    public static  byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) (value & 0xFF);
        src[0] = (byte) ((value >> 8) & 0xFF);
//        src[0] =  (byte) ((value>>16) & 0xFF);
//        src[1] =  (byte) ((value>>24) & 0xFF);
        return src;
    }

    /**
     * 16进制字符串转byte数组
     *
     * @param src
     * @return
     */
    public static byte[] hexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    //移除数组第一位
    public static byte[][] deleteAt(byte[][] bs, int index)
    {
        int length = bs.length - 1;
        byte[][] ret = new byte[length][];

        System.arraycopy(bs, 0, ret, 0, index);
        System.arraycopy(bs, index + 1, ret, index, length - index);

        return ret;
    }

    /**
     * byte数组转字符串
     * @param bytes
     * @return
     */
    public static String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            //sb.append(' ');
        }
        return sb.toString();
    }

    public static byte[] readFileFromAssets(Context context, String groupPath, String filename){
        byte[] buffer = null;
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = null;
            if (groupPath != null) {
                inputStream = am.open(groupPath + "/" + filename);
            } else {
                inputStream = am.open(filename);
            }

            int length = inputStream.available();
            Log.e("testLength", "readFileFromAssets length:" + length);
            buffer = new byte[length];
            inputStream.read(buffer);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return buffer;
    }


}
