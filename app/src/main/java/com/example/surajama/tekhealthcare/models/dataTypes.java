package com.example.surajama.tekhealthcare.models;

public class dataTypes {
    public String getHeartBeatRate() {
        return HeartBeatRate;
    }

    public void setHeartBeatRate(String heartBeatRate) {
        HeartBeatRate = heartBeatRate;
    }

    public String getFootStepsCount() {
        return FootStepsCount;
    }

    public void setFootStepsCount(String footStepsCount) {
        FootStepsCount = footStepsCount;
    }

    public String getBP() {
        return BP;
    }

    public void setBP(String BP) {
        this.BP = BP;
    }

    private String HeartBeatRate;
    private String FootStepsCount;
    private String BP;
}
