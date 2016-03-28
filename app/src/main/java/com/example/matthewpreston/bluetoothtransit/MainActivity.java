package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.text.format.Time;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        isBluetoothOn(mBluetoothAdapter);

        final InternetClient internet = new InternetClient();
        final BluetoothClient bluetooth = new BluetoothClient(mBluetoothAdapter);
        final TextView arrTime = (TextView) this.findViewById(R.id.results);
        final Spinner routeSpinner = (Spinner) this.findViewById(R.id.routeSpinner);
        final Button getButton = (Button) this.findViewById(R.id.nextBusButton);
        final Button testBluetoothButton = (Button) this.findViewById(R.id.bluetoothConnectButton);
        final Button testWifiButton = (Button) this.findViewById(R.id.wifiConnectButton);
        final RadioButton wifiRadio = (RadioButton) this.findViewById(R.id.wifiRadio);
        final RadioButton bluetoothRadio = (RadioButton) this.findViewById(R.id.bluetoothRadio);
        final CheckBox wifiBox = (CheckBox) this.findViewById(R.id.wifiBox);
        final CheckBox bluetoothBox = (CheckBox) this.findViewById(R.id.bluetoothBox);

        bluetoothRadio.setEnabled(false);
        wifiRadio.setEnabled(false);


        List<String> items = new ArrayList<String>();
        initRouteSpinner(items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.new_spinner,items);
        routeSpinner.setAdapter(adapter);

        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {getButton.setEnabled(true);}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {getButton.setEnabled(false);}
        });

        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bluetoothBox.isChecked() && !wifiBox.isChecked()) {
                    new Thread() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        public void run() {
                            final String route = routeSpinner.getSelectedItem().toString();
                            final String result = bluetooth.query(route);

                            if (result != null) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Finished network transfer", Toast.LENGTH_SHORT).show();
                                        Time now = new Time();
                                        now.setToNow();
                                        arrTime.append("\n" + now.hour + ":" + now.minute + ":" + now.second + " " + route  +  " " + result);
                                    }
                                });
                            }

                        }
                    }.start();
                } else if (!bluetoothBox.isChecked() && wifiBox.isChecked()) {
                    new Thread() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        public void run() {
                            final String result = internet.query(routeSpinner.getSelectedItem().toString());

                            if (result != null) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Finished network transfer", Toast.LENGTH_SHORT).show();
                                        Time now = new Time();
                                        now.setToNow();
                                        arrTime.append("\n" + now.hour + ":" + now.minute + ":" + now.second + " " + result);
                                    }
                                });
                            }
                        }
                    }.start();
                }
            }
        });

        testBluetoothButton.setOnClickListener(new OnClickListener() {
            @Override
            //TODO: Have one UI thread that prints the status instead of three
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        final String status;
                        final boolean enable;
                        if(isBluetoothOn(mBluetoothAdapter)) {
                            if (!bluetooth.testConnection()) {
                                status = "Failed to connect";
                                enable = false;

                            } else {
                                status = "Success";
                                enable = true;
                            }
                        }
                        else{
                            status = "Failed to use Bluetooth adapter";
                            enable = false;
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                                bluetoothRadio.setEnabled(enable);
                                bluetoothRadio.setText(status.toCharArray(), 0, status.toCharArray().length);
                            }
                        });
                    }
                }.start();
            }
        });

        testWifiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        final String status;
                        final boolean enable;

                        if (!internet.testConnection()) {
                            status = "Failed to connect";
                            enable = false;
                        } else {
                            status = "Success";
                            enable = true;
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                                wifiRadio.setEnabled(enable);
                                wifiRadio.setText(status.toCharArray(), 0, status.toCharArray().length);
                            }
                        });
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
        int id = item.getItemId();

        if(id == R.id.action_about){
            int RESULT_ABOUT=0;
            Intent i = new Intent(this, AboutActivity.class);
            startActivityForResult(i, RESULT_ABOUT);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isBluetoothOn(BluetoothAdapter mBluetoothAdapter){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        return mBluetoothAdapter.isEnabled();
    }

    public void initRouteSpinner(List<String> items){
        File ids = new File(getExternalFilesDir("txt") + File.separator + "routes.txt");
        InputStream is = null;
        if (!ids.exists()) {
            AssetManager manager = getAssets();
            try {
                is = manager.open("routes.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (is != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(is));
                String line = null;

                try {
                    while ((line = in.readLine()) != null) {
                        String item;
                        item = line.trim();
                        if ((line = in.readLine()) != null)
                            item += ": " + line.trim();
                        items.add(item);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}