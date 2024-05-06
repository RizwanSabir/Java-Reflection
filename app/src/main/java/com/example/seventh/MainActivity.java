package com.example.seventh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.seventh.databinding.ActivityMainBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    private static final String PERMISSION_TAG = "permissiondebug";
    ActivityMainBinding binding;
    IntentFilter mIntentFilter;
    WifiP2pDevice[] deviceListItems;
    int requestCode = 101;

    ArrayAdapter mAdapter;

    private static final int ACCESS_WIFI_STATE_REQUEST_CODE = 102;
    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 100;
    private static final int CHANGE_WIFI_STATE_REQUEST_CODE = 103;
    private static final int CHANGE_NETWORK_STATE_CODE = 104;
    private static final int INTERNET_REQUEST_CODE = 105;
    test1 mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new test1(mManager, mChannel, this);
        askPermission();


        binding.Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });

        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice wifiP2pDevice = deviceListItems[i];
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
                wifiP2pConfig.deviceAddress = wifiP2pDevice.deviceAddress;

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Item clicked", Toast.LENGTH_SHORT).show();
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
//                mManager.connect(mChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(MainActivity.this, "Connected to "+wifiP2pDevice.deviceName, Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int i) {
//                        Toast.makeText(MainActivity.this, "Connected failed "+wifiP2pDevice.deviceName, Toast.LENGTH_SHORT).show();
//
//                    }
//                });
            }
        });

        binding.Ds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopPeerDiscovery();
            }
        });


    }


    public void StopPeerDiscovery(){
        mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Toast.makeText(MainActivity.this, "Stopped Peer discovery ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i) {

                Toast.makeText(MainActivity.this, "Not Stopped Peer discovery", Toast.LENGTH_SHORT).show();
            }
        });


        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                binding.disconnect.setText("ho rah aha");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    private void setUpIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void askPermission() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            //request user permission
            Log.d(PERMISSION_TAG, "askPermissions: request FINE LOCATION permissions");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);

        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
            //request user permission
            Log.d(PERMISSION_TAG, "askPermissions: request ACCESS WIFI STATE permissions");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_WIFI_STATE}, ACCESS_WIFI_STATE_REQUEST_CODE);

        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
            //request user permission
            Log.d(PERMISSION_TAG, "askPermissions: request CHANGE WIFI STATE permissions");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CHANGE_WIFI_STATE}, CHANGE_WIFI_STATE_REQUEST_CODE);

        }

        // Check if the ACCESS_COARSE_LOCATION permission is granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            Toast.makeText(this, "Location permission must be granted", Toast.LENGTH_SHORT).show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        } else {
            // Permission is already granted, proceed with your location-related tasks
            // Your code here...
        }


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
            //request user permission
            Log.d(PERMISSION_TAG, "askPermissions: request CHANGE NETWORK STATE permissions");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CHANGE_NETWORK_STATE}, CHANGE_NETWORK_STATE_CODE);

        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            //request user permission
            Log.d(PERMISSION_TAG, "askPermissions: request INTERNET permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, INTERNET_REQUEST_CODE);

        }
    }


    WifiP2pManager.ConnectionInfoListener connectionInfoListener=new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            InetAddress inetAddresses=wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
              binding.status.setText("HOst server");
            }else{
                binding.status.setText("Client");
            }

        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        setUpIntentFilter();

        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
    public void setDeviceName(String name) {

        if(name.length() > 32) { // Name size limit is 32 chars.
            name = name.substring(0, 32);
        }

        Class[] paramTypes = new Class[3];
        paramTypes[0] = WifiP2pManager.Channel.class;
        paramTypes[1] = String.class;
        paramTypes[2] = WifiP2pManager.ActionListener.class;
        Object[] argList = new Object[3];
        argList[0] = mChannel; // Your current channel
        argList[1] = name;
        argList[2] = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Log.d(TAG, "Wifi name successfully set to " + name);
                Toast.makeText(MainActivity.this, "Wifi name successfully changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Wifi name no", Toast.LENGTH_SHORT).show();

            }
        };

        try {
            Method setDeviceNameMethod = WifiP2pManager.class.getMethod("setDeviceName", paramTypes);
            setDeviceNameMethod.setAccessible(true);
            setDeviceNameMethod.invoke(mManager, argList);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 IllegalArgumentException e) {

        }
    }
    private void discoverPeers() {

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "please enable location services", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED)
            {

            }  // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "discor succes ", Toast.LENGTH_SHORT).show();
                setDeviceName("123r");
                     binding.status.setText("discoveing ");
            }

            @Override
            public void onFailure(int i) {
                binding.status.setText("failed");
            }
        });



    }

    public void setDeviceList(ArrayList<WifiP2pDevice> deviceDetails) {

        deviceListItems = new WifiP2pDevice[deviceDetails.size()];
        String[] deviceNames = new String[deviceDetails.size()];
        for(int i=0 ;i< deviceDetails.size(); i++){
            deviceNames[i] = deviceDetails.get(i).deviceName;
            deviceListItems[i] = deviceDetails.get(i);
        }
        mAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,deviceNames);
        binding.list.setAdapter(mAdapter);
    }
}