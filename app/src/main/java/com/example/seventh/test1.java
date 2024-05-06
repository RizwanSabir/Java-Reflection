package com.example.seventh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.net.InetAddress;

public class test1 extends BroadcastReceiver {
    public static final String TAG = "===WifiBReceiver";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    public test1(WifiP2pManager manager, WifiP2pManager.Channel channel,
                 MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Toast.makeText(context, "wifi en/un check", Toast.LENGTH_SHORT).show();


        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d(test1.TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
               WifiP2pDeviceList peers = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
         //   Toast.makeText(context, "important "+peers.getDeviceList().size(), Toast.LENGTH_SHORT).show();
            handlePeerList( peers);
            if (mManager != null) {
                MyPeerListener myPeerListener = new MyPeerListener(mActivity);
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {


                    Toast.makeText(context, "permission check", Toast.LENGTH_SHORT).show();
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                mManager.requestPeers(mChannel, myPeerListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {


            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "connection is removed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
                    // Failed to remove existing connection.
                }
            });

            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            //WifiP2pInfo p2pInfo = intent
            //        .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);

            //if (p2pInfo != null && p2pInfo.groupOwnerAddress != null) {
            //    String goAddress = Utils.getDottedDecimalIP(p2pInfo.groupOwnerAddress
            //            .getAddress());
            //    boolean isGroupOwner = p2pInfo.isGroupOwner;
            //     Log.d(WifiBroadcastReceiver.TAG,"I am a group owner");
            // }
            if (networkInfo.isConnected()) {

                mActivity.binding.s2.setText("Connected");
                WifiP2pInfo wifiP2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
                WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();


                if (wifiP2pInfo.isGroupOwner) {
                    // Handle incoming connection from a client.
                    // Implement your custom logic to accept or reject the connection.
                    if (wifiP2pInfo.isGroupOwner) {
                        // Handle incoming connection from a client.
                        InetAddress clientAddress = wifiP2pInfo.groupOwnerAddress;

                        wifiP2pConfig.deviceAddress = String.valueOf(clientAddress);
                        // Implement your custom negotiation logic here.
                        if (shouldAcceptConnection()) {
                            mManager.connect(mChannel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Connected to "+wifiP2pInfo.groupOwnerAddress, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(context, "Connected failed "+wifiP2pInfo.groupOwnerAddress, Toast.LENGTH_SHORT).show();

                    }
                });
                        } else {
                            mManager.cancelConnect(mChannel, null);
                            Toast.makeText(context, "Connection cut kar dya", Toast.LENGTH_SHORT).show();
                        }
                        // we are connected with the other device, request connection
                        // info to find group owner IP
                        //mManager.requestConnectionInfo(mChannel, mActivity);
                    } else {
                        // It's a disconnect
                        Log.d(test1.TAG, "Its a disconnect");
                        mActivity.binding.s2.setText("Disonnected");

                        //activity.resetData();
                    }
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                    Log.d(test1.TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                    // Respond to this device's wifi state changing


                } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 10000);
                    if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                        mActivity.binding.s2.setText("Discovery started");
                    } else if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                        mActivity.binding.s2.setText("Discovery stopped");
                    }




                }


            }
        }


    }


    private void handlePeerList(WifiP2pDeviceList peers) {
        for (WifiP2pDevice device : peers.getDeviceList()) {
            if (device.status == WifiP2pDevice.INVITED) {
                // Incoming connection invitation detected.
                Toast.makeText(mActivity, "invited mela", Toast.LENGTH_SHORT).show();

                cancelConnection();

            }
        }
    }

    private void cancelConnection() {
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mActivity, "Connection is cannceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(mActivity, "no is cannceled", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private boolean shouldAcceptConnection() {
        return  false;
    }

}

