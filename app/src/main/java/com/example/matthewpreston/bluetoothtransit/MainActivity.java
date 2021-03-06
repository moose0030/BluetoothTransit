package com.example.matthewpreston.bluetoothtransit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //BluetoothClient bluetooth;
    //InternetClient internet;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Online"));
        tabLayout.addTab(tabLayout.newTab().setText("Offline:Bluetooth"));
        tabLayout.addTab(tabLayout.newTab().setText("Offline:Wi-Fi"));
        
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        /*
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetooth = new BluetoothClient(mBluetoothAdapter);
        internet = new InternetClient();

        final TextView arrTime = (TextView) this.findViewById(R.id.results);
        final Spinner routeSpinner = (Spinner) this.findViewById(R.id.routeSpinner);
        final Button getButton = (Button) this.findViewById(R.id.nextBusButton);
        final Button testBluetoothButton = (Button) this.findViewById(R.id.bluetoothConnectButton);
        final Button testWifiButton = (Button) this.findViewById(R.id.wifiConnectButton);
        final RadioButton wifiRadio = (RadioButton) this.findViewById(R.id.wifiRadio);
        final RadioButton bluetoothRadio = (RadioButton) this.findViewById(R.id.bluetoothRadio);
        final RadioButton useWifiRadio = (RadioButton) this.findViewById(R.id.useWifiRadio);
        final RadioButton useBluetoothRadio = (RadioButton) this.findViewById(R.id.useBluetoothRadio);
        final ProgressBar progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        List<String> items = new ArrayList<>();
        initRouteSpinner(items);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.new_spinner, items);
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
                progressBar.setVisibility(View.VISIBLE);
                new Thread() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    public void run() {
                        final String route = routeSpinner.getSelectedItem().toString();
                        final String result;
                        final long before = new Date().getTime();

                        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                            if (useBluetoothRadio.isChecked() && !useWifiRadio.isChecked()) {
                                checkBluetoothConnection();
                                result = bluetooth.query(route);
                            } else if (!useBluetoothRadio.isChecked() && useWifiRadio.isChecked()) {
                                result = internet.query(routeSpinner.getSelectedItem().toString());
                            } else {
                                result = internet.query(routeSpinner.getSelectedItem().toString());
                            }

                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    final long after = new Date().getTime();
                                    arrTime.setText(route + result + "\n" + arrTime.getText());
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            bluetooth.setup(BluetoothAdapter.getDefaultAdapter());
                            progressBar.setVisibility(View.INVISIBLE);
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Could not connect", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }.start();
            }
        });

        testBluetoothButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final long before = new Date().getTime();
                new Thread() {
                    public void run() {
                        final String status;
                        final boolean enable;
                        if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                            if (!bluetooth.testConnection()) {
                                status = "Failed to connect";
                                enable = false;
                            } else {
                                status = "Success";
                                enable = true;
                            }

                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    final long after = new Date().getTime();
                                    Toast.makeText(MainActivity.this, status , Toast.LENGTH_SHORT).show();
                                    bluetoothRadio.setChecked(enable);
                                    bluetoothRadio.setText(status.toCharArray(), 0, status.toCharArray().length);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }else {
                            enable = false;
                            status = "Failed to connect";
                            bluetooth.setup(BluetoothAdapter.getDefaultAdapter());
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    final long after = new Date().getTime();
                                    Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                                    bluetoothRadio.setChecked(enable);
                                    bluetoothRadio.setText(status.toCharArray(), 0, status.toCharArray().length);
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                }.start();
            }
        });

        testWifiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final long before = new Date().getTime();
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
                                final long after = new Date().getTime();
                                Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
                                wifiRadio.setChecked(enable);
                                wifiRadio.setText(status.toCharArray(), 0, status.toCharArray().length);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }.start();
            }
        });*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        //bluetooth.close();
        //internet.close();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            int RESULT_ABOUT = 0;
            Intent i = new Intent(this, AboutActivity.class);
            startActivityForResult(i, RESULT_ABOUT);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRouteSpinner(List<String> items) {
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
                String line;

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

                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkBluetoothConnection() {
        Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBluetooth, 0);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.matthewpreston.bluetoothtransit/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.matthewpreston.bluetoothtransit/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}