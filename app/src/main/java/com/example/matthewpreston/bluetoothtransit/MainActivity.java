package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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

        //final BluetoothClient bluetooth = new BluetoothClient();
        final InternetClient internet = new InternetClient();
        final TextView arrTime = (TextView)this.findViewById(R.id.results);
        final Spinner routeSpinner = (Spinner)this.findViewById(R.id.routeSpinner);
        final Button getButton = (Button)this.findViewById(R.id.nextBusButton);
        final Button btcButton = (Button)this.findViewById(R.id.bluetoothConnectButton);
        final Button wfcButton = (Button)this.findViewById(R.id.wifiConnectButton);
        final RadioButton wifiRadio = (RadioButton)this.findViewById(R.id.wifiRadio);
        wifiRadio.setEnabled(false);
        String notConn = "Not Connected";
        wifiRadio.setText(notConn.toCharArray(), 0, notConn.toCharArray().length);
        final RadioButton bluetoothRadio = (RadioButton)this.findViewById(R.id.bluetoothRadio);
        bluetoothRadio.setEnabled(false);
        bluetoothRadio.setText(notConn.toCharArray(), 0, notConn.toCharArray().length);


        ColorStateList colorStateList = new ColorStateList(
                new int[][]{

                        new int[]{-android.R.attr.state_enabled}, //disabled
                        new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {

                        Color.RED //disabled
                        , Color.GREEN //enabled

                }
        );

        bluetoothRadio.setButtonTintList(colorStateList);//set the color tint list
        wifiRadio.setButtonTintList(colorStateList);//set the color tint list

        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        Map<String, String> item0 = new HashMap<String, String>(2);
        item0.put("text", "1");
        item0.put("subText", "Greenboro");
        items.add(item0);

        Map<String, String> item1 = new HashMap<String, String>(2);
        item1.put("text", "1");
        item1.put("subText", "Ottawa-Rockcliffe");
        items.add(item1);

        Map<String, String> item2 = new HashMap<String, String>(2);
        item2.put("text", "2");
        item2.put("subText", "Bayshore");
        items.add(item2);

        SimpleAdapter adapter = new SimpleAdapter(this, items,
                android.R.layout.simple_list_item_2, // This is the layout that will be used for the standard/static part of the spinner. (You can use android.R.layout.simple_list_item_2 if you want the subText to also be shown here.)
                new String[] {"text", "subText"},
                new int[] {android.R.id.text1, android.R.id.text2});

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
                                    arrTime.append("String: " + s + "  Data:" + data + " minutes till arrival.");
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
                bluetoothRadio.setEnabled(true);
                String conn = "Connected";
                bluetoothRadio.setText(conn.toCharArray(), 0, conn.toCharArray().length);
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
                                wifiRadio.setEnabled(true);
                                String conn = "Connected";
                                wifiRadio.setText(conn.toCharArray(),0,conn.toCharArray().length);
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
