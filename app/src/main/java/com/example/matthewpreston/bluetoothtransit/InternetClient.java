package com.example.matthewpreston.bluetoothtransit;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by matthewpreston on 2016-01-11.
 */
public class InternetClient implements CommunicationClient {
    Socket socket;
    InetSocketAddress address;// = new SocketAddress();

    public InternetClient(){
        socket = new Socket();
    }
    public boolean connect(String addr, String port){
        if(socket.isConnected())
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
        }
        return true;
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
            System.out.println(e);
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
            socket.getInputStream().read(buffer);
            System.out.println("Received: " + buffer.toString());

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
            System.out.println(e);
            return false;
        }
        return true;
    }
}
