package com.example.matthewpreston.bluetoothtransit;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by matthewpreston on 2016-01-11.
 */
public class InternetClient implements CommunicationClient {
    Socket socket;
    DatagramSocket sock;


    public InternetClient(){
        socket = new Socket();
        try {
        sock = new DatagramSocket(3034, InetAddress.getByName("0.0.0.0"));
            sock.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean connect(String addr, String port){
        /*if(socket.isConnected())
            return true;

        try
        {
            address = new InetSocketAddress(addr,Integer.parseInt(port));
            socket.connect(address);
        }
        catch(IOException e)
        {
            System.out.println(e);
            return false;
        }*/
        return false;
    }

    //  Sends data through socket
    //  if not connected    |   FALSE
    //  if exception        |   FALSE
    //  otherwise           |   TRUE
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
            Log.e("Exception", e.toString());
            return false;
        }
        return true;
    }

    //  Receives a byte[] from socket
    //  if not connected     |   FALSE
    //  if exception         |   FALSE
    //  otherwise            |   TRUE
    public byte[] receive(){
        byte[] buffer = new byte[1024];
/*
        if(!socket.isConnected())
            return false;
*/
        try
        {
            if(socket.getInputStream().read(buffer) > 0)
                System.out.println("Received: " + String.valueOf(buffer));

        }
        catch(IOException e)
        {
            System.out.println("Error:" + e);
            //return false;
        }
        System.out.println("End of receive");
        return buffer;
        //return true;
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
            Log.e("Exception", e.toString());
            return false;
        }
        return true;
    }

    public boolean testConnection()
    {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            //sock = new DatagramSocket(3034, InetAddress.getByName("0.0.0.0"));
            sock.setBroadcast(true);

            while (true) {
                Log.i("", "Ready to receive broadcast packets!");


                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                sock.receive(packet);

                //Packet received
                Log.i("", "Packet received from: " + packet.getAddress().getHostAddress());
                String data = new String(packet.getData()).trim();
                Log.i("", "Packet received; data: " + data);
                return true;
            }
        } catch (IOException ex) {
            Log.i("", "Oops" + ex.getMessage());
            return false;
        }
    }

    public String query(String input)
    {
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

                    return s;
                }
            } catch (IOException ex) {
                Log.i("", "Oops" + ex.getMessage());
                return "Error";
            }

    }
}
