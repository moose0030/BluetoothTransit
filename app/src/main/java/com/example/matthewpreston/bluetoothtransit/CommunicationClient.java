package com.example.matthewpreston.bluetoothtransit;

import java.util.Set;

/**
 * Created by matthewpreston on 2016-01-11.
 */
public interface CommunicationClient {


    boolean connect(String i, String a);
    boolean send(String data);
    //boolean receive();
    boolean closeConnection();
}
