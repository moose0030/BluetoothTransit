package com.example.matthewpreston.bluetoothtransit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by matthewpreston on 2016-01-07.
 */
public class BluetoothClient {

    BluetoothSocket mmSocket = null;
    BluetoothDevice mmDevice = null;
    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    public BluetoothClient(BluetoothAdapter mBluetoothAdapter){
        this.bluetoothAdapter = mBluetoothAdapter;
    }

    public boolean setup(BluetoothAdapter mBluetoothAdapter){
        if (mBluetoothAdapter == null)
                return false;
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    mmDevice = device;
                    break;
                }
            }
        }
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()) {
                try {
                    mmSocket.connect();
                }catch (Exception e) {
                    return false;
                }
            }
        }catch (IOException e) {
            return false;
        }
        return true;
    }

    public String query(String msg){
        try {
            if(!mmSocket.isConnected())
                setup(bluetoothAdapter);
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());
            final InputStream mmInputStream;
            mmInputStream = mmSocket.getInputStream();
            byte[] readBuffer = new byte[1024];
            if (mmInputStream.read(readBuffer) > 0) {
                final String data = new String(readBuffer);
                return data;
            }
            return "Error exchanging with server";
        } catch (IOException e) {
            return "Error exchanging with server";
        }
    }




    public boolean closeConnection() {
        if(!mmSocket.isConnected())
            return true;
        try {
            mmSocket.close();
        }catch (IOException e){
            return false;
        }
        return true;
    }

    public boolean testConnection(){
        try {
            if(!setup(bluetoothAdapter))
                return false;
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.flush();
            String msg = "TEST_BT";
            mmOutputStream.write(msg.getBytes());
            final InputStream mmInputStream;
            mmInputStream = mmSocket.getInputStream();
            byte[] readBuffer = new byte[1024];
            if(mmInputStream.read(readBuffer) > 0) {
                final String data = new String(readBuffer);
                if(data.trim().equals("SUCCESS!"))
                    return true;
            }
            return false;

        } catch (IOException e) {
            return false;
        }
    }
}