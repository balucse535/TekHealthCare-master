package com.example.surajama.tekhealthcare.models;

public class users {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMedicalNumber() {
        return medicalNumber;
    }

    public void setMedicalNumber(String medicalNumber) {
        this.medicalNumber = medicalNumber;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public users(String userId, String firstName, String lastName, String emailId, String password, String medicalNumber,String mobileNumber, String patientId)
    {
        this.userId=userId;
        this.firstName = firstName;
        this.lastName =lastName;
        this.password=password;
        this.mobileNumber = mobileNumber;
        this.medicalNumber = medicalNumber;
        this.patientId=patientId;
    }
    public users()
    {

    }
    private String userId;
    private String firstName;
    private String lastName;
    private String emailId;
    private String password;
    private String mobileNumber;
    private String medicalNumber;
    private String patientId;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    private String deviceId;


}
