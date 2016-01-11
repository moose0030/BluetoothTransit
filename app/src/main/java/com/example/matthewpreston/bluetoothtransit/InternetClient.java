package com.example.matthewpreston.bluetoothtransit;

/**
 * Created by matthewpreston on 2016-01-11.
 */
public class InternetClient implements CommunicationClient {
    public boolean connect(){
        return true;
    }
    public boolean send(int i){
        return true;
    }

    public boolean receive(){
        return true;
    }
}
