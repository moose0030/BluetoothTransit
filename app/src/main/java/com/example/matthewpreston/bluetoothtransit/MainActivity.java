package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.text.format.Time;

public class MainActivity extends AppCompatActivity {




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        Time now = new Time();

        final Handler handler = new Handler();
        final InternetClient internet = new InternetClient();
        final BluetoothClient bluetooth = new BluetoothClient(mBluetoothAdapter);
        final TextView arrTime = (TextView) this.findViewById(R.id.results);
        final Spinner routeSpinner = (Spinner) this.findViewById(R.id.routeSpinner);
        final Button getButton = (Button) this.findViewById(R.id.nextBusButton);
        final Button btcButton = (Button) this.findViewById(R.id.bluetoothConnectButton);
        final Button wfcButton = (Button) this.findViewById(R.id.wifiConnectButton);
        final RadioButton wifiRadio = (RadioButton) this.findViewById(R.id.wifiRadio);
        final CheckBox wifiBox = (CheckBox) this.findViewById(R.id.wifiBox);
        final CheckBox bluetoothBox = (CheckBox) this.findViewById(R.id.bluetoothBox);
        final RadioButton bluetoothRadio = (RadioButton) this.findViewById(R.id.bluetoothRadio);

        bluetoothRadio.setEnabled(false);
        wifiRadio.setEnabled(false);



        ColorStateList colorStateList = new ColorStateList(
                new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{android.R.attr.state_enabled}}, //disable, enabled
                new int[]{Color.RED, Color.GREEN} //disabled:red, enabled:green
        );

        bluetoothRadio.setButtonTintList(colorStateList);//set the color tint list
        wifiRadio.setButtonTintList(colorStateList);//set the color tint list

        List<Map<String, String>> items = new ArrayList<Map<String, String>>();
        initRouteSpinner(items);
        SimpleAdapter adapter = new SimpleAdapter(this, items,
                android.R.layout.simple_list_item_2, // This is the layout that will be used for the standard/static part of the spinner. (You can use android.R.layout.simple_list_item_2 if you want the subText to also be shown here.)
                new String[]{"text", "subText"},
                new int[]{android.R.id.text1, android.R.id.text2});

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
                                        arrTime.append("\n" + now.hour + ":" + now.minute + ":" + now.second + " " + route + " "+ result);
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
                } else {
                }
            }
        });

        btcButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //(new Thread(new workerThread(routeSpinner.getSelectedItem().toString()))).start();

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
            public void onClick(View view) {

                new Thread() {
                    public void run() {
                        if (!internet.testConnection()) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Did not connect", Toast.LENGTH_SHORT).show();
                                    wifiRadio.setEnabled(false);
                                    String conn = "Failed to connect";
                                    wifiRadio.setText(conn.toCharArray(), 0, conn.toCharArray().length);
                                }
                            });
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                                    wifiRadio.setEnabled(true);
                                    String conn = "Connected";
                                    wifiRadio.setText(conn.toCharArray(), 0, conn.toCharArray().length);
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

    public void initRouteSpinner(List<Map<String, String>> items){
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
                        Map<String, String> item = new HashMap<String, String>(2);
                        item.put("text", line.trim());
                        if ((line = in.readLine()) != null)
                            item.put("subText", line.trim());
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

