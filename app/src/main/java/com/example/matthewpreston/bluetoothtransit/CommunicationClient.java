package com.example.matthewpreston.bluetoothtransit;

/**
 * Created by matthewpreston on 2016-01-11.
 */
public interface CommunicationClient {

    boolean connect();
    boolean send(int i);
    boolean receive();
}
