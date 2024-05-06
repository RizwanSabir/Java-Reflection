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

public class BoradCast extends BroadcastReceiver {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    MainActivity mActivity;

    public BoradCast(WifiP2pManager mManager, WifiP2pManager.Channel mChannel,
                     MainActivity mainActivity) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            Toast.makeText(context, "wifi en/un check", Toast.LENGTH_SHORT).show();


        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Peers list changed, handle it if needed.
             // For example, you can retrieve the peers list and check for the INVITED status.
            //   WifiP2pDeviceList peers = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
            // handlePeerList(peers);
            MyPeerListener myPeerListener = new MyPeerListener(mActivity);
            if (mManager != null) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, android.Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                mManager.requestPeers(mChannel, myPeerListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection state changed, handle it if needed.
            WifiP2pInfo info = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            handleConnectionState(info);
        }

    }

    private void handleConnectionState(WifiP2pInfo info) {
        Toast.makeText(mActivity, "Connection ho gya", Toast.LENGTH_SHORT).show();
    }

    private void handlePeerList(WifiP2pDeviceList peers) {
        for (WifiP2pDevice device : peers.getDeviceList()) {
        if (device.status == WifiP2pDevice.CONNECTED) {
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
}
