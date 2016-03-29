package com.example.matthewpreston.bluetoothtransit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by matthewpreston on 2016-01-07.
 */
class BluetoothClient {

    private BluetoothSocket mmSocket;
    private boolean inUse = false;

    public BluetoothClient(BluetoothAdapter bt) {
        setup(bt);
    }

    public void setup(BluetoothAdapter bt) {
        Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();
        BluetoothDevice mmDevice = null;
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) {
                    Log.e("raspberrypi", device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID

        try {
            if (mmDevice != null) {
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);

                if (!mmSocket.isConnected()) {
                    mmSocket.connect();
                    Log.e("socket", mmSocket.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String query(String msg) {
        if (inUse) {
            return "Bluetooth in use";
        } else {
            inUse = true;
        }

        try {
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

            final InputStream mmInputStream;
            mmInputStream = mmSocket.getInputStream();
            byte[] readBuffer = new byte[1024];
            if (mmInputStream.read(readBuffer) > 0) {
                final String data = new String(readBuffer);
                inUse = false;
                return data;
            }

        } catch (IOException e) {
            e.printStackTrace();
            inUse = false;
            return "Error exchanging with server";
        }
        return "Empty read";
    }


    public boolean closeConnection() {
        if (!mmSocket.isConnected())
            return true;
        try {
            mmSocket.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public boolean testConnection() {
        if (inUse) {
            return false;
        } else {
            inUse = true;
        }

        try {
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            String msg = "TEST_BT";
            mmOutputStream.write(msg.getBytes());

            final InputStream mmInputStream;
            mmInputStream = mmSocket.getInputStream();
            byte[] readBuffer = new byte[1024];
            if (mmInputStream.read(readBuffer) > 0) {
                final String data = new String(readBuffer);
                if (data.trim().equals("SUCCESS!")) {
                    inUse = false;
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inUse = false;
        return false;
    }
}