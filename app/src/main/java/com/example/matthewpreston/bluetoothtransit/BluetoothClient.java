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

    public BluetoothClient(BluetoothAdapter mBluetoothAdapter){


        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()) {
                mmSocket.connect();
            }
        }catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    public String query(String msg){

        try {
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

    public void sendBtMsg(String message) {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()) {
                mmSocket.connect();
            }

            String msg = message;

            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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


    final class workerThread implements Runnable {

        private String btMsg;

        public workerThread(String msg) {
            btMsg = msg;
        }

        public void run() {
            sendBtMsg(btMsg);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    final InputStream mmInputStream;
                    mmInputStream = mmSocket.getInputStream();
                    byte[] readBuffer = new byte[1024];
                    if (mmInputStream.read(readBuffer) > 0) {
                        final String data = new String(readBuffer);

                        handler.post(new Runnable() {
                            public void run() {
                                //arrTime.append(data);
                            }
                        });
                        break;
                    } else {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            {
                try {
                    mmSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}