package com.example.surajama.tekhealthcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.surajama.tekhealthcare.models.device;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class DeviceRegistration extends Activity {

    EditText did;
    Button deviceButton;
    TextView devid;
    String mDeviceName;
    String mDeviceAddress;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private Intent intent;
    private AsyncHttpClient client;
    private StringEntity jsonEntity = null;
    private String base_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_registration);
        mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        base_path = DeviceRegistration.this.getString(R.string.base_url);
        // Toast about the action
        Toast toast = Toast.makeText(DeviceRegistration.this, "Register your Device here", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        //Get the device Name and button name
        did = (EditText) findViewById(R.id.deviceID);
        deviceButton = findViewById(R.id.deviceButton);
        devid = (TextView) findViewById(R.id.devID);
        did.setText(mDeviceName.toString()+"_"+(int )(Math.random() * 10 ));
        Controller aController = (Controller)DeviceRegistration.this.getApplicationContext();

        //Listener for Button
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new RegisterDevice().execute();
            }
        });

         client = new AsyncHttpClient();
        Header header;
        client.addHeader("Content-Type", "application/json");


    }

       public class RegisterDevice extends AsyncTask<String,String,String>
                  {
                      @Override
                      protected void onPreExecute() {

                          System.out.println("START!");
                      }

                      @Override
                      protected String doInBackground(String... params) {

                          try
                          {
                            URL url = new URL("https://tek.cumulocity.com/inventory/managedObjects");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type","application/json");
                            conn.setRequestProperty("charset","UTF-8");
                            conn.setRequestProperty("ver","0.9");
                            conn.setRequestProperty("Accept","application/vnd.com.nsn.cumulocity.managedObject+json");
                            conn.setRequestProperty("Authorization","Basic cHBhdnVsdXJAdGVrc3lzdGVtcy5jb206QWJjZDEyMzQ=");
                            String deviceJSON = "{\n" +
                                    "    \"c8y_IsDevice\" : {},\n" +
                                    "    \"c8y_Position\" : {},\n" +
                                    "    \"name\" : \"" + did.getText().toString() +"\",\n" +
                                    "    \"c8y_LocationUpdate\" : {},\n" +
                                    "    \"com_cumulocity_model_Agent\": {},\n" +
                                    "    \"c8y_SupportedOperations\": [ \"c8y_Restart\", \"c8y_Configuration\" ]\n" +
                                    "}";

                            //Frame Body of the request

                            System.out.println("Frame Body of the request");
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

                             System.out.println("Device created and the Device id is "+ jo.getString("id"));
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
                          devid.setText("Device created and the Device id is "+ values[0]);
                          final Controller aController = (Controller) DeviceRegistration.this.getApplicationContext();
                          device device=aController.getDevice(0);
                          device.setDeviceId(values[0]);
                          JSONObject jsonParams = new JSONObject();
                          try {

                              jsonParams.put("deviceId",device.getDeviceId());
                              jsonParams.put("patientId",aController.getUser().getPatientId());
                              jsonParams.put("emailId",aController.getUser().getEmailId());
                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                          try {
                              jsonEntity = new StringEntity(jsonParams.toString());
                          } catch (UnsupportedEncodingException e) {
                              e.printStackTrace();
                          }
                          final Context context = getApplicationContext();
                          client.post(context,base_path+"/TEKHealthAPI-0.0.1-SNAPSHOT/devices/create",jsonEntity,"application/json",new JsonHttpResponseHandler(){
                              @Override
                              public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                  super.onSuccess(statusCode, headers, response);

                                  System.out.println("success");
                                  Toast.makeText(DeviceRegistration.this,"Device ID set",Toast.LENGTH_LONG);
                                  intent = new Intent(DeviceRegistration.this,DeviceScanActivity.class);
                              }

                              @Override
                              public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                  Toast.makeText(DeviceRegistration.this,"Device ID set failed to service layer",Toast.LENGTH_LONG);
                              }

                          });
                          Toast.makeText(DeviceRegistration.this,"Device ID set",Toast.LENGTH_LONG);
                          finish();
                      }
                  }
    }

