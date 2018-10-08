package com.example.surajama.tekhealthcare.models;

import android.bluetooth.BluetoothDevice;

public class device {
    private BluetoothDevice bluetoothDevice;
    private String deviceId;
    private String deviceMacAddress;
    private String deviceName;
    private dataTypes readings;

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public dataTypes getReadings() {
        return readings;
    }

    public void setReadings(String reading,String datatype) {
        if (datatype.equals("BP"))
        this.readings.setBloodPreassure(reading);
        else if(datatype.equals("BPM"))
            this.readings.setHeartBeatRate(reading);
        else if(datatype.equals("footSteps"))
            this.readings.setFootStepsCount(reading);
    }




}
