package com.example.surajama.tekhealthcare.services;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.example.surajama.tekhealthcare.Controller;
import com.example.surajama.tekhealthcare.R;
import com.example.surajama.tekhealthcare.models.Customer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by sopani on 3/25/2018.
 */

public class RegisterService extends AppCompatActivity {
    private String basePath;
    public void addCustomers(final Context context, final Customer customer, final String pass) {
        basePath= context.getString(R.string.large_text);
        StringEntity jsonEntity = null;
        AsyncHttpClient client = new AsyncHttpClient();
        Header header;
        client.addHeader("Accept", "application/json");

        client.addHeader("Content-Type", "application/json");

        client.addHeader("api-key", "xxTEKyy");
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("username", customer.getCustomerName());
            jsonParams.put("pwd", pass);
            jsonParams.put("customerName", customer.getCustomerName());
            jsonParams.put("customerAddress", customer.getCustomerAddress());
            jsonParams.put("contactNo", customer.getCustomerContact());
            jsonParams.put("cityId", customer.getCityId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonEntity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        client.post(context, basePath+"TekShop/signup", jsonEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println("success");
                Intent intent = new Intent(context, LoginActivity.class);
                try {
                    final Controller aController = (Controller) context.getApplicationContext();
                    aController.setUserId(response.get("User ID").toString());
                    if(aController.getUserId() != null && aController.getCustomer().getUserId() == null) {
                        new CustomerService().getCustomers(context, aController.getUserId());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println("failure");
                Intent intent = new Intent(context, LoginActivity.class);
                CharSequence text = "Register failed ! Please try again or continue as guest";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.TOP, -0, 230);
                toast.show();
                context.startActivity(intent);
            }
        });


    }
}
