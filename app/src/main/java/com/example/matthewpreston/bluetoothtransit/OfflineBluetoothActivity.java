package com.example.matthewpreston.bluetoothtransit;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by matthewpreston on 2016-04-23.
 */
public class OfflineBluetoothActivity extends MainActivity {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        TextView tv=new TextView(this);
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setText("This Is offlinebluetooth Activity");

        setContentView(tv);
    }
}
