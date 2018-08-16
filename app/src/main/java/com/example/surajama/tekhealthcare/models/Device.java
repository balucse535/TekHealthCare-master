package com.example.surajama.tekhealthcare.models;

public class Device {
    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }



    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }



    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public dataTypes getData() {
        return data;
    }

    public void setData(dataTypes data) {
        this.data = data;
    }

    private String deviceId;
    private String deviceName;
    private String deviceMacAddress;
    private dataTypes data;
}
