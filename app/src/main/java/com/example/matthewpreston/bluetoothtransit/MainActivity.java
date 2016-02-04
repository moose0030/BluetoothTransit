package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final BluetoothClient bluetooth = new BluetoothClient();
        final InternetClient internet = new InternetClient();
        final TextView arrTime = (TextView)this.findViewById(R.id.arrTime);
        final Spinner routeSpinner = (Spinner)this.findViewById(R.id.routePicker);
        final Button getButton = (Button)this.findViewById(R.id.getButton);
        final Button btcButton = (Button)this.findViewById(R.id.BT_Connect);
        final Button wfcButton = (Button)this.findViewById(R.id.WF_Connect);

        Integer[] routes = new Integer[]{1,2,3,4};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, routes);

        routeSpinner.setAdapter(adapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getButton.setEnabled(false);

            }
        });


        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String routeNum = routeSpinner.getSelectedItem().toString();
                //bluetooth.send(routeNum);
                //int result = bluetooth.receive();


                new Thread() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    public void run() {

                        internet.send(routeSpinner.getSelectedItem().toString());
                        final byte[] data = internet.receive();
                        final String s = new String(data, StandardCharsets.UTF_8);
                        System.out.println(".........sdsl");
                        if (data != null) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Finished network transfer" + s, Toast.LENGTH_SHORT).show();
                                    arrTime.setText("String: " + s + "  Data:" + data + " minutes till arrival.");
                                }
                            });
                        }

                    }
                }.start();
            }


                /*String output = "";
                if (result == -1) {
                    output = "Route not availble.";
                } else if (result == 1)
                    output = result + " minute til bus " + routeNum + " arrives.";
                else
                    output = result + " minutes til bus " + routeNum + " arrives.";

                arrTime.setText(output);*/
        });

        btcButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int REQUEST_ENABLE_BT = 1;
                Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBt, REQUEST_ENABLE_BT);

                //if (!bluetooth.connect("", "")) {
                    Toast.makeText(MainActivity.this, "Did not connect", Toast.LENGTH_SHORT).show();
                //}
            }
        });

        wfcButton.setOnClickListener(new OnClickListener() {
        @Override
            public void onClick (View view){

            new Thread()
            {
                public void run()
                {
                    if(!internet.connect("192.168.0.18","6789")) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Did not connect", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Connected to " + internet.address.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }.start();
            }
    });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
