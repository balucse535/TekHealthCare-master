package com.example.surajama.tekhealthcare;

/**
 * Created by sopani on 3/21/2018.
 */

import android.app.Application;
import android.view.ViewConfiguration;

import com.example.surajama.tekhealthcare.models.Customer;
import com.example.surajama.tekhealthcare.models.Device;

public class Controller extends Application{

    private String userId;
    private Customer customer = new Customer();
    private Device device = new Device();

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Device getDevices() {
        return device;
    }
}
