package com.example.surajama.tekhealthcare;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surajama.tekhealthcare.R;

import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PushToIot extends Activity {

    EditText did,hr,steps,bp;
    TextView daid;
    Button push;
    Date currentTime;
    String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_to_iot);


        // Toast about the action
        Toast toast = Toast.makeText(PushToIot.this, "Push the data to Cumulocity", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

        did = (EditText) findViewById(R.id.deviceID);
        hr = (EditText) findViewById(R.id.heartRate);
        steps = (EditText) findViewById(R.id.stepData);
        push = (Button) findViewById(R.id.deviceButton);
        bp = findViewById(R.id.bloodPressure);
        daid = (TextView) findViewById(R.id.devID);
        String ID = getIntent().getStringExtra("Device ID");
        String heartRate = getIntent().getStringExtra("value");
        String StepCount =getIntent().getStringExtra("StepCount");
        String BP = getIntent().getStringExtra("BloodPressure");
        did.setText(ID);
        hr.setText(heartRate);
        steps.setText(StepCount);
        bp.setText(BP);
        currentTime= Calendar.getInstance().getTime();

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateTime = df2.format(currentTime);
//        Log.i("PushToIot",dateTime);
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PushData().execute();

            }
        });
    }
    public class PushData extends AsyncTask<String,String,String>
    {

        @Override
        protected void onPreExecute() {
            System.out.println("START!");

        }

        @Override
        protected String doInBackground(String... params) {

            try
            {
                URL url = new URL("https://tek.cumulocity.com/measurement/measurements");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setRequestProperty("charset","UTF-8");
                conn.setRequestProperty("ver","0.9");
                conn.setRequestProperty("Accept","application/vnd.com.nsn.cumulocity.measurement+json");
                conn.setRequestProperty("Authorization","Basic cHBhdnVsdXJAdGVrc3lzdGVtcy5jb206QWJjZDEyMzQ=");
                String deviceJSON = "{\n" +
                        "    \"c8y_HealthMonitoring\": {\n" +
                        "        \"H\": {\n" +
                        "            \"value\": "+ hr.getText().toString() + ",\n" +
                        "            \"unit\":\"BPM\"\n" +
                        "        },\n" +
                        "         \"D\": {\n" +
                        "            \"value\": "+ steps.getText().toString() + ",\n" +
                        "            \"unit\":\"FootSteps\"\n" +
                        "        },\n" +
                        "         \"P\": {\n" +
                        "            \"value\": "+ bp.getText().toString() + ",\n" +
                        "            \"unit\":\"mmgh\"\n" +
                        "        }\n" +
                        "    },\n" +
                        "    \"time\": \"" + dateTime +"\",\n" +
                        "    \"source\": {\n" +
                        "        \"id\": \"" + did.getText().toString() + "\"\n" +
                        "    },\n" +
                        "    \"type\":\"c8y_PTCMeasurement\"\n" +
                        "}";

                //Frame Body of the request

                System.out.println("Frame Body of the request");
                System.out.println(deviceJSON);
                OutputStream os = conn.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(deviceJSON);
                osw.flush();
                osw.close();
                os.close();

                //connect to Cummulocity
                System.out.println("Connecting to Cummulocity");
                conn.connect();
                System.out.println("Response code is " + conn.getResponseCode());

                //read the inputstream and print it

                String result;
                System.out.println("Reading data1");
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                System.out.println("Reading data2");
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result2 = bis.read();
                System.out.println("Reading data");
                while(result2 != -1) {
                    buf.write((byte) result2);
                    result2 = bis.read();
                }
                result = buf.toString();
                System.out.println("Success");
                System.out.println(result);

                JSONObject jo = new JSONObject(result);
                // Toast about the action

                System.out.println("Data entry DONE and id is "+ jo.getString("id"));
                publishProgress(jo.getString("id"));
            }
            catch(Exception e)
            {
                System.out.println("--ERROR--");
                System.out.println(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            System.out.println("DONE!");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            Toast.makeText(PushToIot.this,"Data entry DONE and Data id is  "+ values[0],Toast.LENGTH_LONG).show();
        }
    }
}
