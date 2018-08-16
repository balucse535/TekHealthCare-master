package com.example.surajama.tekhealthcare.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.surajama.tekhealthcare.MainActivity;
import com.example.surajama.tekhealthcare.R;
import com.example.surajama.tekhealthcare.Controller;
import com.example.surajama.tekhealthcare.models.Customer;

public class LoginActivity extends AppCompatActivity {
    FrameLayout btnLogin;
    EditText userName,password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.userName);
        password=findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Controller aController = (Controller)LoginActivity.this.getApplicationContext();
                aController.setUserId(userName.getText().toString());
                if(aController.getUserId() != null && aController.getCustomer().getUserId() == null) {
                    new CustomerService().getCustomers(LoginActivity.this, aController.getUserId());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                }
            }
        });
    }
    public void load(View view) {
    }

}
