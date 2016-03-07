package com.example.matthewpreston.bluetoothtransit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import java.io.IOException;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by matthewpreston on 2016-01-07.
 */
public class BluetoothClient implements CommunicationClient{
    BluetoothSocket socket;
    BluetoothDevice device;
    BluetoothAdapter adapter;
    int REQUEST_ENABLE_BT = 1;
    UUID uuid = new UUID(1000,1000);
    public BluetoothClient(){
        adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.startDiscovery();
        try {


            socket = device.createRfcommSocketToServiceRecord(uuid);
        }
        catch(IOException e){
            System.out.println(e);
        }

        //socket = new BluetoothSocket();
    }

    public void init(Context c)
    {
        if(!adapter.isEnabled()){
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //c.startActivityForResult(enableBt, REQUEST_ENABLE_BT);
            //c.startActivity(enableBt);
        }
    }
    public boolean connect(String name, String addr){
        try
        {
            if(!adapter.isDiscovering())
                socket.connect();
            else
                return false;
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



/*
private class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                mmServerSocket.close();
                break;
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
   /* public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}*/

/*

private class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    /*
public void cancel() {
    try {
        mmSocket.close();
    } catch (IOException e) { }
}
}
 */