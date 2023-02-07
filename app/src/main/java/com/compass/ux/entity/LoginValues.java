package com.compass.ux.entity;

public class LoginValues {
    private String username;
    private String password;
    private String uavSn;
    private String uavType;

    public String getUavType() {
        return uavType;
    }

    public void setUavType(String uavType) {
        this.uavType = uavType;
    }

    public String getUavSn() {
        return uavSn;
    }

    public void setUavSn(String uavSn) {
        this.uavSn = uavSn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
