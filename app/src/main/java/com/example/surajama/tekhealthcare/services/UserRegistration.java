package com.example.surajama.tekhealthcare.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surajama.tekhealthcare.Controller;
import com.example.surajama.tekhealthcare.MainActivity;
import com.example.surajama.tekhealthcare.R;
import com.example.surajama.tekhealthcare.models.users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class UserRegistration extends AppCompatActivity {
    private DatabaseReference users;
    private Button registerUser;
    private EditText userName, emailID, firstName, lastName, password, rePassword, MRNumber, phoneNumber;
    private users user;
    private String basePath,patientId;
    private StringEntity jsonEntity = null;
    private AsyncHttpClient client ;
    private Header header;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        users= FirebaseDatabase.getInstance().getReference("Users");
        registerUser = findViewById(R.id.signup);
        userName = findViewById(R.id.userName);
        emailID = findViewById(R.id.emailID);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        password = findViewById(R.id.password);
        rePassword = findViewById(R.id.confirm_password);
        MRNumber = findViewById(R.id.medicalNum);
        phoneNumber = findViewById(R.id.phoneNum);
        basePath= UserRegistration.this.getString(R.string.base_url);
        client = new AsyncHttpClient();

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verifiaction = checkPassword(password.getText().toString(), rePassword.getText().toString());
                if (!verifiaction.equals("success"))
                    Toast.makeText(UserRegistration.this, verifiaction, Toast.LENGTH_LONG).show();
                else {
                    JSONObject jsonParams = new JSONObject();
                    try {
                        jsonParams.put("userId", userName.getText().toString());
                        jsonParams.put("firstName", firstName.getText().toString());
                        jsonParams.put("lastName", lastName.getText().toString());
                        jsonParams.put("emailId", emailID.getText().toString());
                        jsonParams.put("password", password.getText().toString());
                        jsonParams.put("medicalNumber", MRNumber.getText().toString());
                        jsonParams.put("phoneNumber", phoneNumber.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        jsonEntity = new StringEntity(jsonParams.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    client.setTimeout(180000);
                    client.post(UserRegistration.this, basePath + "/TEKHealthAPI-0.0.1-SNAPSHOT/users/create", jsonEntity, "application/json", new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                            try {
                                patientId = response.get("patientId").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String id = userName.getText().toString();
                            user = new users(userName.getText().toString(), firstName.getText().toString(), lastName.getText().toString(),emailID.getText().toString(),password.getText().toString(),MRNumber.getText().toString(),phoneNumber.getText().toString(),patientId);
                            try {
                                users.child(id).setValue(user);
                                Toast.makeText(UserRegistration.this, "Registration Successfully completed", Toast.LENGTH_LONG).show();
                                Controller aController = (Controller)UserRegistration.this.getApplicationContext();
                                aController.setUserId(userName.getText().toString());
                                aController.setUser(user);
                                Intent i = new Intent(UserRegistration.this, MainActivity.class);
                                startActivity(i);
                            } catch (Exception exp) {
                                String exper = exp.getMessage().toString();
                                Toast.makeText(UserRegistration.this, exper.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            Toast.makeText(UserRegistration.this, "User Registration failed .. Try Again!!", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }
    public String checkPassword(String password, String confirmPassword)
    {
        if (!password.equals(confirmPassword))
            return "password do not match!!";

        return "success";
    }
}
