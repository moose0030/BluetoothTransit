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
public class BluetoothClient implements CommunicationClient {

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;
    Handler handler;
    BluetoothAdapter bluetoothAdapter;
    public BluetoothClient(BluetoothAdapter mBluetoothAdapter){
        this.bluetoothAdapter = mBluetoothAdapter;
        setup(bluetoothAdapter);
    }

    public boolean setup(BluetoothAdapter mBluetoothAdapter){
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
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    @Override
    public boolean connect(String i, String a) {
        return false;
    }

    @Override
    public boolean send(String data) {
        return false;
    }

    @Override
    public boolean closeConnection() {
        return false;
    }

    public boolean testConnection(){
        try {
            Log.i("BT:testConnection 1",String.valueOf(mmSocket.isConnected()));
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
            e.printStackTrace();
        }
        return false;
    }

}
