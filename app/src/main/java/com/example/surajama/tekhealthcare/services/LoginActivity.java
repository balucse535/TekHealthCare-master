package com.example.surajama.tekhealthcare.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.example.surajama.tekhealthcare.MainActivity;
import com.example.surajama.tekhealthcare.R;
import com.example.surajama.tekhealthcare.Controller;
import com.example.surajama.tekhealthcare.models.users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    FrameLayout btnLogin;
    Button btnsignUp;
    EditText userName,password;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference users;
    String patientID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.userName);
        password=findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        btnsignUp = findViewById(R.id.signup);
        users = FirebaseDatabase.getInstance().getReference("Users");
        final Controller aController=(Controller)LoginActivity.this.getApplicationContext();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mchild = users.child(userName.getText().toString());
                mchild.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            users user = dataSnapshot.getValue(users.class);
                            aController.setUserId(userName.getText().toString());
                            aController.setUser(user);
                            aController.setDeviceId(user.getDeviceId());
                        }
                        catch (Exception exc)
                        {

                            System.out.println(exc.toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Log.i("this","childs retreieve");

                if(aController.getUserId() != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                }
            }
        });
        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserRegistration.class);
                startActivity(intent);
            }
        });
    }
    public void load(View view) {
    }

}
