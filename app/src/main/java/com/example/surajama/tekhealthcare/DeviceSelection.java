package com.example.surajama.tekhealthcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class DeviceSelection extends AppCompatActivity {
    private Integer typeOfDevice;
    private String id;
    private CardView miband, reflex,fitrist,bpMachine;
    private Intent intent;
    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        typeOfDevice = getIntent().getIntExtra("typeOfDevice",0);
        if (typeOfDevice==R.id.activity_tracker) {
            setContentView(R.layout.activity_trackers);
            miband = findViewById(R.id.MiBand2);
            reflex = findViewById(R.id.Reflex);
            fitrist = findViewById(R.id.Fitrist);


            miband.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("model","MI Band 2");
                    startActivity(intent);
                }
            });
            reflex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("model","W307N_A3B01EB161");
                    startActivity(intent);
                }
            });
            fitrist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("model","Pulzz.2C12");
                    startActivity(intent);
                }
            });
        }
        else if(typeOfDevice==R.id.health_devices) {
            setContentView(R.layout.health_devices);
            bpMachine = findViewById(R.id.bp_machine);
            bpMachine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("model","BP2941");
                    startActivity(intent);
                }
            });
        }
        else if(typeOfDevice==R.id.phone) {
            setContentView(R.layout.phone_tracker);
        }
        else if(typeOfDevice==R.id.task_calendar) {
            setContentView(R.layout.task_calendar);
        }
        else
            setContentView(R.layout.default_activity);
        intent = new Intent(DeviceSelection.this,DeviceScanActivity.class);


    }

}
