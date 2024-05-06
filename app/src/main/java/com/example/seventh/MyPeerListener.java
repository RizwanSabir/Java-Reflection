package com.example.seventh;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ash on 14/2/18.
 */

public class MyPeerListener implements WifiP2pManager.PeerListListener {
    public static final String TAG = "===MyPeerListener";
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    public MainActivity mainActivity;

    public MyPeerListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {

        ArrayList<WifiP2pDevice> deviceDetails = new ArrayList<>();


        if(wifiP2pDeviceList != null ) {
            if(wifiP2pDeviceList.getDeviceList().size() == 0) {
                return;
            }

            for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                deviceDetails.add(device);
            }
            if(mainActivity != null) {
                mainActivity.setDeviceList(deviceDetails);
            }

        }
        else {
            Toast.makeText(mainActivity, "Devices list is null", Toast.LENGTH_SHORT).show();

        }
    }
}
