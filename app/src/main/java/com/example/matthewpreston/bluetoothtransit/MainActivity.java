package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;


    public void sendBtMsg(String msg2send) {
        //UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee"); //Standard SerialPortService ID
        try {

            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            if (!mmSocket.isConnected()) {
                mmSocket.connect();
            }

            String msg = msg2send;
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final Handler handler = new Handler();
        final InternetClient internet = new InternetClient();
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


        final class workerThread implements Runnable {

            private String btMsg;

            public workerThread(String msg) {
                btMsg = msg;
            }

            public void run() {
                sendBtMsg(btMsg);
                while (!Thread.currentThread().isInterrupted()) {
                    int bytesAvailable;
                    boolean workDone = false;

                    try {
                        final InputStream mmInputStream;
                        mmInputStream = mmSocket.getInputStream();
                        bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {

                            byte[] packetBytes = new byte[bytesAvailable];
                            Log.e("Aquarium recv bt", "bytes available");
                            byte[] readBuffer = new byte[1024];

                            if (mmInputStream.read(packetBytes) > 0) {
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        //The variable data now contains our full command
                                        handler.post(new Runnable() {
                                            public void run() {
                                                arrTime.append(data);
                                            }
                                        });

                                        workDone = true;
                                        break;


                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }

                                if (workDone == true) {
                                    mmSocket.close();
                                    break;
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("IOException", e.toString());
                    }

                }
            }
        }


        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String routeNum = routeSpinner.getSelectedItem().toString();

                if (bluetoothBox.isChecked() && !wifiBox.isChecked()) {
                    (new Thread(new workerThread(routeSpinner.getSelectedItem().toString()))).start();
                } else if (!bluetoothBox.isChecked() && wifiBox.isChecked()) {
                    new Thread() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        public void run() {

                            final String result = internet.query(routeSpinner.getSelectedItem().toString());
                            //final byte[] data = internet.receive();
                            //final String s = new String(data, StandardCharsets.UTF_8);
                            if (result != null) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "Finished network transfer", Toast.LENGTH_SHORT).show();
                                        arrTime.append("Data:" + result + "| ");
                                    }
                                });
                            }

                        }
                    }.start();
                } else {
                    return;
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


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        // end light off button handler

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }

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

