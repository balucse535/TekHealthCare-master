package com.example.surajama.tekhealthcare;

import android.animation.ArgbEvaluator;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surajama.tekhealthcare.Helpers.CustomBluetoothProfile;
import com.example.surajama.tekhealthcare.models.device;
import com.example.surajama.tekhealthcare.services.HealthVisualizer;

import org.apache.commons.lang3.ArrayUtils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class DeviceActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    ImageButton mNextBtn;
    Button mSkipBtn, mFinishBtn;
    ImageView zero, one, two;
    ImageView[] indicators;
    Boolean isListeningHeartRate = false;
    Boolean isListeningFootsteps = false;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    static String metric = "0";
    static String BPmetric ="0";
    static String StepCount="0";
    private Controller aController;
    Button btnMiBand,btnFitristHeartRate,deviceReg,btnDataSync;
    TextView txtPhysicalAddress,txtState,txtByte,hearBeatRate,BP,deviceName,footStepsCount;
    static TextView heartBeatReading,textView,footStepsReading,BPreading;
    private device device;
    ImageButton btnDisconnect;
    private String mDeviceName;
    private String mDeviceAddress;
    private static int count=0;
    private Dialog progressDialog;

    int lastLeftValue = 0;

    CoordinatorLayout mCoordinator;

    SectionsPagerAdapter mSectionsPagerAdapter;

    static final String TAG = "PagerActivity";

    int page = 0;   //  to track page position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_profile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        }

        setContentView(R.layout.device_profile);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNextBtn = (ImageButton) findViewById(R.id.intro_btn_next);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            mNextBtn.setImageDrawable(
                    tintMyDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chevron_right_24dp), Color.WHITE)
            );

        mSkipBtn =  findViewById(R.id.intro_btn_skip);
        mFinishBtn = findViewById(R.id.intro_btn_finish);

        zero =  findViewById(R.id.intro_indicator_0);
        one = findViewById(R.id.intro_indicator_1);
        two =  findViewById(R.id.intro_indicator_2);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);


        indicators = new ImageView[]{zero, one, two};

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = ContextCompat.getColor(this, R.color.cyan);
        final int color2 = ContextCompat.getColor(this, R.color.orange);
        final int color3 = ContextCompat.getColor(this, R.color.green);

        final int[] colorList = new int[]{color1, color2, color3};

        final ArgbEvaluator evaluator = new ArgbEvaluator();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                /*
                color update
                 */
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 2 ? position : position + 1]);
                mViewPager.setBackgroundColor(colorUpdate);

            }

            @Override
            public void onPageSelected(int position) {

                page = position;

                updateIndicators(page);

                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        break;
                }


                mNextBtn.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 2 ? View.VISIBLE : View.GONE);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        initializeObjects();
        initilaizeComponents();
        initializeEvents();

        getBoundedDevice();
        startConnecting();
    }
    void getBoundedDevice() {

        mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        txtPhysicalAddress.setText(mDeviceAddress);
        deviceName.setText(mDeviceName);

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

        btnMiBand =  findViewById(R.id.miBand2);
        btnFitristHeartRate =  findViewById(R.id.btnFitRist);

        txtPhysicalAddress =  findViewById(R.id.txtPhysicalAddress);
        deviceName=findViewById(R.id.profile_device_name);
        txtState =  findViewById(R.id.txtState);
        txtByte =  findViewById(R.id.txtByte);
        deviceReg=findViewById(R.id.deviceReg);
        hearBeatRate = findViewById(R.id.HeartBeatRate);
        BP = findViewById(R.id.BP);
        footStepsCount = findViewById(R.id.FootSteps);
        btnDataSync = findViewById(R.id.dataSync);
        btnDisconnect=findViewById(R.id.btnDisconnect);
        aController = (Controller) DeviceActivity.this.getApplicationContext();


    }

    void initializeEvents() {

        btnMiBand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMiBand();
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });
        btnFitristHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitristHeartRate();
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

                StepCount = "0";
                String DeviceID=aController.getDevice(0).getDeviceId();
                Intent intent = new Intent(DeviceActivity.this,PushToIot.class);
                intent.putExtra(DeviceRegistration.EXTRAS_DEVICE_NAME,mDeviceName);
                if(DeviceID==null)
                    intent.putExtra("Device ID",aController.getDeviceId());
                else
                    intent.putExtra("Device ID",DeviceID);
                intent.putExtra("value",metric);
                intent.putExtra("StepCount",StepCount);
                intent.putExtra("BloodPressure",BPmetric);
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
     private void readFootStepsCount()
     {
         BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.FootSteps.service)
                 .getCharacteristic(CustomBluetoothProfile.FootSteps.descriptor);
         bluetoothGatt.readDescriptor(bchar.getDescriptor(CustomBluetoothProfile.FootSteps.descriptor));
     }

    void startConnecting() {
        progressDialog = new Dialog(DeviceActivity.this,R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("Connecting..");
        progressDialog.show();
        String address = txtPhysicalAddress.getText().toString();
        txtState.setText("Connecting...");
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
        progressDialog.cancel();
        bluetoothGatt.discoverServices();
        txtState.setText("Connected");
        fitristHeartRate();
        if(aController.getDevice(0).getDeviceName().contains("BP")||aController.getDevice(0).getDeviceName().contains("Pulzz"))
            fitristHeartRate();
        else if(aController.getDevice(0).getDeviceName().contains("MI Band2"))
            setMiBand();

        device = new device();
        footStepsCount.setText("3062");
        device.setDeviceMacAddress(mDeviceAddress);
        device.setDeviceName(mDeviceName);
        aController.getDevices().add(device);

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
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.FitRist.service)
                .getCharacteristic(CustomBluetoothProfile.FitRist.notifyCharcteristic);
        bluetoothGatt.readCharacteristic(bchar);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.FitRist.descriptor);
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

    void setMiBand() {
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
        listenFootStepsCount();
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
                heartBeatReading.setText(metric);
            }
            if(characteristic.getUuid().equals(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"))) {
                if (aController.getDevice(0).getDeviceName().contains("BP"))
                {
                    String tempMetricS = Byte.toString(data[2]);
                    int tempMetricSval= Integer.parseInt(tempMetricS);
                    String tempMetricD=Byte.toString(data[3]);
                    heartBeatReading.setText(Byte.toString(data[4]));
                    metric=Byte.toString(data[4]);
                    hearBeatRate.setText(metric);
                    if (tempMetricSval>0)
                        tempMetricS = Integer.toString(tempMetricSval);
                    else
                    {
                        tempMetricSval=128+(128+tempMetricSval);
                        tempMetricS = Integer.toString(tempMetricSval);
                    }
                    if(tempMetricS!=null&&!tempMetricS.equals("0")){
                        tempMetricD=tempMetricD.replaceAll("\\n", "");
                        BPmetric = tempMetricS+"."+tempMetricD;
                        BP.setText(BPmetric);
                        BPreading.setText(BPmetric);
                        aController.getDevice(0).setReadings(BPmetric,"BP");

                    }

                }
                else {
                    String tempMetric = Byte.toString(data[7]);
                    if(tempMetric!=null&&!tempMetric.equals("0")){
                        metric = tempMetric;
                        hearBeatRate.setText(metric);
                        heartBeatReading.setText(metric);
                    }
                }

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



    public static Drawable tintMyDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }


    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    public void viewDeviceData(View view) {
        Intent i = new Intent(DeviceActivity.this, HealthVisualizer.class);
        startActivity(i);

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] bgs = new int[]{R.drawable.heart_beat, R.drawable.original, R.drawable.foot_steps};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
            textView =  rootView.findViewById(R.id.section_label);
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                textView.setText("Heart Beat Rate");
                heartBeatReading  = rootView.findViewById(R.id.reading);
            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER)==2) {
                textView.setText("Blood Pressure");
                BPreading = rootView.findViewById(R.id.reading);
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3) {
                textView.setText("FootSteps Count");
                footStepsReading = rootView.findViewById(R.id.reading);
            }

            img = rootView.findViewById(R.id.section_img);
            img.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);

            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HeartBeatRate";
                case 1:
                    return "Foot Steps Count";
                case 2:
                    return "Blood Pressure reading";
            }
            return null;
        }

    }

}

