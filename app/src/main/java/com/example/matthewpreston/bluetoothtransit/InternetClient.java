package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.text.format.Time;

/**
 * Created by matthewpreston on 2016-01-11.
 */
public class InternetClient {
    DatagramSocket sock;
    public boolean inUse = false;
    public InternetClient(){
        try {
        sock = new DatagramSocket(3034, InetAddress.getByName("0.0.0.0"));
            sock.setSoTimeout(5000);
            sock.setBroadcast(true);
            sock.setReuseAddress(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //  Checks if connection is closed
    //  if not connected         |   TRUE
    //  if connected disconnect  |   TRUE
    //  otherwise                |   FALSE
    public boolean closeConnection()
    {
        if(!sock.isConnected())
            return true;

        sock.close();
        return true;
    }

    public boolean testConnection()
    {
        if(inUse)
            return false;
        else
            inUse = true;
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            while (true) {
                Log.i("", "Ready to receive broadcast packets!");
                //Receive a packet
                byte[] recvBuf = new byte[15000];

                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                //RECEIVE REQ
                sock.receive(packet);

                String input = "TEST_WIFI";
                //SEND QUERY
                recvBuf = input.getBytes();
                packet.setData(recvBuf);
                packet.setAddress(packet.getAddress());
                sock.send(packet);

                //RECEIVE RESULT
                recvBuf = new byte[15000];
                packet.setData(recvBuf);
                String s;
                do {
                    sock.receive(packet);
                    s = new String(packet.getData()).trim();
                }while(s.equals("REQ") || s.length() == 0);
                inUse = false;
                return true;
            }
        } catch (IOException ex) {
            Log.i("", "Oops" + ex.getMessage());
            inUse = false;
            return false;
        }
    }

    public String query(String input)
    {
        if(inUse)
            return "Socket in use";
        else{
            inUse = true;
        }
            try {
                //Keep a socket open to listen to all the UDP trafic that is destined for this port

                while (true) {
                    Log.i("", "Ready to receive broadcast packets!");

                    //Receive a packet
                    byte[] recvBuf = new byte[15000];

                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                    //RECEIVE REQ
                    sock.receive(packet);

                    //SEND QUERY
                    recvBuf = input.getBytes();
                    packet.setData(recvBuf);
                    packet.setAddress(packet.getAddress());
                    sock.send(packet);

                    //RECEIVE RESULT
                    recvBuf = new byte[15000];
                    packet.setData(recvBuf);
                    String s;
                    do {
                        sock.receive(packet);
                        s = new String(packet.getData()).trim();
                    }while(s.equals("REQ") || s.length() == 0);
                    inUse = false;
                    return s;
                }
            } catch (IOException ex) {
                inUse = false;
                return "Error exchanging with server";
            }

    }
}
