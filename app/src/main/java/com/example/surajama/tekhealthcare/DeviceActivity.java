package com.example.surajama.tekhealthcare;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surajama.tekhealthcare.Helpers.CustomBluetoothProfile;
import com.example.surajama.tekhealthcare.models.Device;

import org.apache.commons.lang3.ArrayUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

public class DeviceActivity extends Activity {
    Boolean isListeningHeartRate = false;
    Boolean isListeningFootsteps = false;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    String metric;
    private Controller aController;
    Button btnStartConnecting, btnGetBatteryInfo, btnGetHeartRate, btnWalkingInfo, btnStartVibrate, btnFitristHeartRate,deviceReg,btnDisconnect,btnDataSync;
    EditText txtPhysicalAddress;
    TextView txtState, txtByte,hearBeatRate;
    private Device device;
    private String mDeviceName;
    private String mDeviceAddress;
    private static int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_profile);

        initializeObjects();
        initilaizeComponents();
        initializeEvents();

        getBoundedDevice();

    }

    void getBoundedDevice() {

        mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        txtPhysicalAddress.setText(mDeviceAddress);

        Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bd : boundedDevice) {
            if (bd.getName().contains("MI Band 2")) {
                txtPhysicalAddress.setText(bd.getAddress());
            }
        }
    }

    void initializeObjects() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    void initilaizeComponents() {
        btnStartConnecting =  findViewById(R.id.btnStartConnecting);
        btnDisconnect =findViewById(R.id.btnDisconnect);
        btnGetBatteryInfo =  findViewById(R.id.btnGetBatteryInfo);
        btnWalkingInfo = findViewById(R.id.btnWalkingInfo);
        btnStartVibrate =  findViewById(R.id.btnStartVibrate);
        btnFitristHeartRate =  findViewById(R.id.btnStopVibrate);
        btnGetHeartRate = findViewById(R.id.btnGetHeartRate);
        txtPhysicalAddress =  findViewById(R.id.txtPhysicalAddress);
        txtState =  findViewById(R.id.txtState);
        txtByte =  findViewById(R.id.txtByte);
        deviceReg=findViewById(R.id.deviceReg);
        hearBeatRate = findViewById(R.id.HeartBeatRate);
        btnDataSync = findViewById(R.id.dataSync);
        aController = (Controller) DeviceActivity.this.getApplicationContext();

    }

    void initializeEvents() {
        btnStartConnecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnecting();
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
        btnGetBatteryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBatteryStatus();
            }
        });
        btnStartVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVibrate();
            }
        });
        btnFitristHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitristHeartRate();
            }
        });
        btnGetHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenHeartRate();
            }
        });
        btnWalkingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenFootStepsCount();
            }
        });
        deviceReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceActivity.this,DeviceRegistration.class);
                intent.putExtra(DeviceRegistration.EXTRAS_DEVICE_NAME,mDeviceName);
                intent.putExtra(DeviceRegistration.EXTRAS_DEVICE_ADDRESS,mDeviceAddress);
                startActivity(intent);
            }
        });
        btnDataSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String DeviceID=aController.getDevices().getDeviceId();
                Intent intent = new Intent(DeviceActivity.this,PushToIot.class);
                intent.putExtra(DeviceRegistration.EXTRAS_DEVICE_NAME,mDeviceName);
                intent.putExtra("Device ID",DeviceID);
                intent.putExtra("value",metric);
                intent.putExtra("StepCount",2045);
                startActivity(intent);
            }
        });
    }

    private void listenFootStepsCount() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.FootSteps.service)
                .getCharacteristic(CustomBluetoothProfile.FootSteps.notifyCharcteristic);
        bluetoothGatt.readCharacteristic(bchar);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.FootSteps.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningFootsteps = true;
    }

    void startConnecting() {

        String address = txtPhysicalAddress.getText().toString();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

        Log.v("test", "Connecting to " + address);
        Log.v("test", "Device name " + bluetoothDevice.getName());

        bluetoothGatt = bluetoothDevice.connectGatt(this, true, bluetoothGattCallback);

    }
    void disconnect()
    {
        bluetoothGatt.disconnect();
    }

    void stateConnected() {
        bluetoothGatt.discoverServices();
        txtState.setText("Connected");

        device = new Device();
        device.setDeviceMacAddress(mDeviceAddress);
        device.setDeviceName(mDeviceName);
        //aController.getDevices().add(device);
    }

    void stateDisconnected() {
        bluetoothGatt.disconnect();
        txtState.setText("Disconnected");
    }

    void startScanHeartRate() {
        txtByte.setText("...");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        bluetoothGatt.writeCharacteristic(bchar);
    }

    void listenHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        bluetoothGatt.readCharacteristic(bchar);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }
    void fitristHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.FitRist.service)
                .getCharacteristic(CustomBluetoothProfile.FitRist.notifyCharcteristic);
        bluetoothGatt.readCharacteristic(bchar);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.FitRist.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }

    void getBatteryStatus() {
        txtByte.setText("...");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.Basic.service)
                .getCharacteristic(CustomBluetoothProfile.Basic.batteryCharacteristic);
        if (!bluetoothGatt.readCharacteristic(bchar)) {
            Toast.makeText(this, "Failed get battery info", Toast.LENGTH_SHORT).show();
        }

    }

    void startVibrate() {
        String notification="";
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.Authentication.service)
                .getCharacteristic(CustomBluetoothProfile.Authentication.authCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar,true);
        for (BluetoothGattDescriptor descriptor : bchar.getDescriptors()){
            if (descriptor.getUuid().equals(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))) {
                Log.i("INFO", "Found NOTIFICATION BluetoothGattDescriptor: " + descriptor.getUuid().toString());
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                notification = new String(descriptor.getValue());
            }
        }

        Toast.makeText(this, notification, Toast.LENGTH_SHORT).show();
        bchar.setValue(new byte[]{0x01, 0x8, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45});
        bluetoothGatt.writeCharacteristic(bchar);
    }

    void stopVibrate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.AlertNotification.service)
                .getCharacteristic(CustomBluetoothProfile.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{0});
        if (!bluetoothGatt.writeCharacteristic(bchar)) {
            Toast.makeText(this, "Failed stop vibrate", Toast.LENGTH_SHORT).show();
        }
    }


    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v("test", "onConnectionStateChange");

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.v("test", "onServicesDiscovered");
            listenHeartRate();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.v("test", "onCharacteristicRead");
            byte[] data = characteristic.getValue();
            txtByte.setText(Arrays.toString(data));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.v("test", "onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            count++;
            super.onCharacteristicChanged(gatt, characteristic);
            Log.v("test", "onCharacteristicChanged");
            byte[] data = characteristic.getValue();

            txtByte.setText(Arrays.toString(data));
            if(characteristic.getUuid().equals(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"))) {
                metric = Arrays.toString(data);
                metric=metric.substring(4,metric.length()-1);
                hearBeatRate.setText(metric);
                final Controller aController = (Controller) DeviceActivity.this.getApplicationContext();
                aController.getDevices();
            }
            if(characteristic.getUuid().equals(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"))) {
                String tempMetric = Byte.toString(data[7]);
                if(tempMetric!=null&&!tempMetric.equals("0")){
                    metric = tempMetric;
                    hearBeatRate.setText(metric);
                }

                final Controller aController = (Controller) DeviceActivity.this.getApplicationContext();
                aController.getDevices();
            }
            if (count == 2)
            {
                byte[] value = characteristic.getValue();
                byte[] tmpValue = Arrays.copyOfRange(value, 3, 19);
                Cipher cipher = null;
                try {
                    cipher = Cipher.getInstance("AES/ECB/NoPadding");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }


                SecretKeySpec key = new SecretKeySpec(new byte[] {0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x40, 0x41, 0x42, 0x43, 0x44, 0x45}, "AES");

                try {
                    cipher.init(Cipher.ENCRYPT_MODE, key);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                byte[] bytes = new byte[0];
                try {
                    bytes = cipher.doFinal(tmpValue);
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }

                byte[] rq = ArrayUtils.addAll(new byte[]{0x03, 0x8}, bytes);
                characteristic.setValue(rq);
                gatt.writeCharacteristic(characteristic);
            }
            if (count == 1) {
                characteristic.setValue(new byte[]{0x02, 0x8});
                gatt.writeCharacteristic(characteristic);
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.v("test", "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.v("test", "onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.v("test", "onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.v("test", "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.v("test", "onMtuChanged");
        }

    };
}
