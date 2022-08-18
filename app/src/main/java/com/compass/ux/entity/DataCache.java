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
    private String rtmp_address;

    private int missionWaypointSize;

    private int targetWaypointIndex;

    private int missionExecuteState;

    //图传信号
    private int downlinkQuality;

    public int getDownlinkQuality() {
        return downlinkQuality;
    }

    public void setDownlinkQuality(int downlinkQuality) {
        this.downlinkQuality = downlinkQuality;
    }

    public int getUplinkQuality() {
        return uplinkQuality;
    }

    public void setUplinkQuality(int uplinkQuality) {
        this.uplinkQuality = uplinkQuality;
    }

    //遥控信号
    private int uplinkQuality;

    public int getTargetWaypointIndex() {
        return targetWaypointIndex;
    }

    public void setTargetWaypointIndex(int targetWaypointIndex) {
        this.targetWaypointIndex = targetWaypointIndex;
    }

    public int getMissionExecuteState() {
        return missionExecuteState;
    }

    public void setMissionExecuteState(int missionExecuteState) {
        this.missionExecuteState = missionExecuteState;
    }

    public int getMissionWaypointSize() {
        return missionWaypointSize;
    }

    public void setMissionWaypointSize(int missionWaypointSize) {
        this.missionWaypointSize = missionWaypointSize;
    }

    public String getRtmp_address() {
        return rtmp_address;
    }

    public void setRtmp_address(String rtmp_address) {
        this.rtmp_address = rtmp_address;
    }

}
