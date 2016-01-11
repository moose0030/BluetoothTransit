package com.example.matthewpreston.bluetoothtransit;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by matthewpreston on 2016-01-07.
 */
public class BluetoothClient implements CommunicationClient{

    public BluetoothClient(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public boolean connect(){
        return true;
    }
    public boolean send(int routeNum){
        return true;
    }

    public boolean receive(){
        return true;
    }
}
