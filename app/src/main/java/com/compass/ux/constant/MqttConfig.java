package com.compass.ux.constant;

public class MqttConfig {
    /**
     * 服务器IP地址
     */
    public static String SOCKET_HOST = "tcp://36.154.125.61:8098";

    /**
     * 设备号
     */
    public static String EQUIPMENT_ID = "msdk";

    /**
     * 账号
     */
    public static String USER_NAME = "pilot";

    /**
     * 密码
     */
    public static String USER_PASSWORD = "pilot123";

    /**
     * SN码
     */
    public static String SERIAL_NUMBER = "";

    /**
     * 注册
     */
    public static String MQTT_REGISTER_TOPIC = "uav/product/" + SERIAL_NUMBER + "/status";

    /**
     * 注册成功订阅推流地址
     */
    public static String MQTT_REGISTER_REPLY_TOPIC = "uav/product/" + SERIAL_NUMBER + "/status_reply";

    /**
     * 订阅飞控消息
     */
    public static String MQTT_FLIGHT_CONTROLLER_TOPIC = "uav/product/" + SERIAL_NUMBER + "/controller";

    /**
     * 飞控是否调用成功的返回结果
     */
    public static String MQTT_FLIGHT_CONTROLLER_REPLY_TOPIC = "uav/product/" + SERIAL_NUMBER + "/controller_reply";

    /**
     * 推送电池A
     */
    public static String MQTT_BATTERY_A_TOPIC = "uav/product/" + SERIAL_NUMBER + "/batteryA";

    /**
     * 推送电池B
     */
    public static String MQTT_BATTERY_B_TOPIC = "uav/product/" + SERIAL_NUMBER + "/batteryB";

    /**
     * 推送报警
     */
    public static String MQTT_DIAGNOSTICS_TOPIC = "uav/product/" + SERIAL_NUMBER + "/diagnostics";

    /**
     * 推送图传信号
     */
    public static String MQTT_DOWNlINK_TOPIC = "uav/product/" + SERIAL_NUMBER + "/downlink_signal_quality";

    /**
     * 推送遥控信号
     */
    public static String MQTT_UPLINK_TOPIC = "uav/product/" + SERIAL_NUMBER + "/uplink_signal_quality";

    /**
     * 飞行状态
     */
    public static String MQTT_FLIGHT_STATE_TOPIC = "uav/product/" + SERIAL_NUMBER + "/flight_state";

    /**
     * 云台A
     */
    public static String MQTT_GIMBAL_A_TOPIC = "uav/product/" + SERIAL_NUMBER + "/gimbalA";

    /**
     * 云台B
     */
    public static String MQTT_GIMBAL_B_TOPIC = "uav/product/" + SERIAL_NUMBER + "/gimbalB";

    /**
     * RTK基站状态
     */
    public static String MQTT_RTK_STATE_TOPIC = "uav/product/" + SERIAL_NUMBER + "/rtk_state";

    /**
     * RTK连接状态
     */
    public static String MQTT_RTK_CONNECTION_STATE_TOPIC = "uav/product/" + SERIAL_NUMBER + "/rtk_connection_state";

    /**
     * RTK列表
     */
    public static String MQTT_RTK_LIST_INFO_TOPIC = "uav/product/" + SERIAL_NUMBER + "/rtk_list_info";

    /**
     * 任务执行状态
     */
    public static String MQTT_MISSION_EXECUTE_STATE_TOPIC = "uav/product/" + SERIAL_NUMBER + "/mission_execute_state";

    /**
     * H264裸流
     */
    public static String MQTT_H264_TOPIC = "uav/product/" + SERIAL_NUMBER + "/h264";
}