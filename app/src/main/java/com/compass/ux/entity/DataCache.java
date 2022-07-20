package com.compass.ux.entity;

public class DataCache {

    private static class DataCacheHolder {
        private static final DataCache INSTANCE = new DataCache();
    }

    private DataCache(){}

    public static final DataCache getInstance() {
        return DataCache.DataCacheHolder.INSTANCE;
    }

    //rtmp地址
    private  String rtmp_address;

    public String getRtmp_address() {
        return rtmp_address;
    }

    public void setRtmp_address(String rtmp_address) {
        this.rtmp_address = rtmp_address;
    }

}
