package com.example.surajama.tekhealthcare.services;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.example.surajama.tekhealthcare.Controller;
import com.example.surajama.tekhealthcare.R;
import com.example.surajama.tekhealthcare.models.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by sopani on 3/25/2018.
 */

public class CustomerService extends AppCompatActivity {

    private String basePath;
    public void getCustomers(final Context context,final String userId){
        basePath= context.getString(R.string.large_text);
        StringEntity jsonEntity = null;
        AsyncHttpClient client = new AsyncHttpClient();
        Header header;
        client.addHeader("Accept", "application/json");

        client.addHeader("Content-Type", "application/json");

        client.addHeader("api-key","xxTEKyy");
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            jsonEntity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        client.post(context, basePath+"TekShop/getCustomer", jsonEntity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                System.out.println("success");
                Intent intent = new Intent(context, LoginActivity.class);
                try{
                final Controller aController = (Controller) context.getApplicationContext();
                aController.setCustomer(new ObjectMapper().readValue(response.toString(),Customer.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                context.startActivity(intent);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                System.out.println("failure");
                Intent intent = new Intent(context, LoginActivity.class);

                context.startActivity(intent);
            }
        });



    }
}
