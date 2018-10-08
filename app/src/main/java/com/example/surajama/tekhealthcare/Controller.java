package com.example.surajama.tekhealthcare;

/**
 * Created by sopani on 3/21/2018.
 */

import android.app.Application;

import com.example.surajama.tekhealthcare.models.device;
import com.example.surajama.tekhealthcare.models.users;

import java.util.ArrayList;
import java.util.List;

public class Controller extends Application{
    private String userId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceId;
    private users user = new users();
    private List<device> devices = new ArrayList<>();
    private String dateTimeofLastReading;
    public String getDateTimeofLastReading() {
        return dateTimeofLastReading;
    }

    public void setDateTimeofLastReading(String dateTimeofLastReading) {
        this.dateTimeofLastReading = dateTimeofLastReading;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<device> getDevices()
    {
        return devices;
    }
    public void addDevice(device dev)
    {
        devices.add(dev);
    }
    public void removeDevice(int poisition)
    {
        devices.remove(poisition);
    }
    public device getDevice(int poistion)
    {
        return devices.get(poistion);
    }
    public void setUser(users user)
    {
        this.user=user;
    }
    public users getUser()
    {
        return this.user;
    }
}
