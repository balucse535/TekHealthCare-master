package com.example.surajama.tekhealthcare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class UserActions extends Activity {

    Button device, iot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_actions);
        //get user name from previous activity
        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        // Toast the User
        Toast toast = Toast.makeText(UserActions.this, "Welcome " + user, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        //Get the button ID's
        device = findViewById(R.id.Device);
        iot = findViewById(R.id.IoTHUB);
        //Create Listeners

        //Device Registration Listener
        device.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          Intent deviceIntent = new Intent(UserActions.this, DeviceRegistration.class);
                                          startActivity(deviceIntent);
                                      }
                                  }

        );
        //IoTHub button Listener
        iot.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent iotIntent = new Intent(UserActions.this, PushToIot.class);
                                       startActivity(iotIntent);
                                   }
                               }

        );
    }
}
