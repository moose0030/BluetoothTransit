package com.example.matthewpreston.bluetoothtransit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.provider.Telephony;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by matthewpreston on 2016-01-07.
 */
public class BluetoothClient implements CommunicationClient{
    BluetoothSocket socket;
    BluetoothDevice device;
    UUID uuid = new UUID(1000,1000);
    public BluetoothClient(){
        //socket = new BluetoothSocket();
    }

    public boolean connect(String name, String addr){
        try
        {
            device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
        }
        catch(IOException e)
        {
            System.out.println(e);
            return false;
        }

        return true;
    }

    public boolean send(String data){
        byte[] buffer = data.getBytes();

        if(!socket.isConnected())
            return false;

        try
        {
            socket.getOutputStream().write(buffer);
        }

        catch(IOException e)
        {
            System.out.println(e);
            return false;
        }

        return true;
    }

    public boolean receive(){
        byte[] buffer = null;

        if(!socket.isConnected())
            return false;

        try
        {
            socket.getInputStream().read(buffer);
        }

        catch(IOException e)
        {
            System.out.println(e);
            return false;
        }

        return true;
    }

    //  Checks if connection is closed
    //  if not connected         |   TRUE
    //  if connected disconnect  |   TRUE
    //  otherwise                |   FALSE
    public boolean closeConnection()
    {
        if(!socket.isConnected())
            return true;
        try
        {
            socket.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
            return false;
        }

        return true;
    }

}
