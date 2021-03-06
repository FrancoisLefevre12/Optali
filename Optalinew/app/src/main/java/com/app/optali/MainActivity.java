package com.app.optali;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@TargetApi(18)
public class MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    ImageView iwLed1;
    ImageView iwLed2;
    ImageView iwLed3;
    ImageView iwSpeaker;

    TextView twLed1;
    TextView twLed2;
    TextView twLed3;
    TextView twSpeaker;


    // State machine
    final private static int STATE_BLUETOOTH_OFF = 1;
    final private static int STATE_DISCONNECTED = 2;
    final private static int STATE_CONNECTING = 3;
    final private static int STATE_CONNECTED = 4;
    public static final String TAG = "MainActivity.java";

    private int state;

    private boolean scanStarted;
    private boolean scanning;

    private boolean flag_connect=false;
    private boolean flag_perim=false;
    private boolean flag_empty=false;
    private boolean flag_porte=false;
    private boolean connected = false;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private RFduinoService rfduinoService;

    private Button enableBluetoothButton;
    private TextView scanStatusText;
    private TextView connectionStatusText;
    private Button scanButton;

    private Handler handler1;
    private Handler handler2;
    private Runnable runnable1;
    private Runnable runnable2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Activation du module Bluetooth
        enableBluetoothButton = (Button) findViewById(R.id.enableBluetooth);
        enableBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBluetoothButton.setEnabled(false);
                enableBluetoothButton.setText(
                        bluetoothAdapter.enable() ? "Enabling bluetooth..." : "Enable failed!");
            }
        });


        // Find and Connect Device
        scanStatusText = (TextView) findViewById(R.id.scanStatus);
        connectionStatusText = (TextView) findViewById(R.id.connectionStatus);

        scanButton = (Button) findViewById(R.id.scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanStarted = true;

                bluetoothAdapter.startLeScan(
                         new UUID[]{ RFduinoService.UUID_SERVICE },
                         MainActivity.this);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent rfduinoIntent = new Intent(MainActivity.this, RFduinoService.class);
                        bindService(rfduinoIntent, rfduinoServiceConnection, BIND_AUTO_CREATE);
                    }
                }, 1000);
            }
        });


        // Mise en place du bouton ajout page
        Button buttonAjout = (Button) findViewById(R.id.ajoutPage);

        buttonAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), AjoutActivity.class);
                startActivity(i);
            }
        });

        // Mise en place du bouton liste Ingredient
        Button buttonList = (Button) findViewById(R.id.listeIngredient);

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(getApplicationContext(), ListeActivity.class);
                startActivity(i2);
            }
        });

        // Déclare les images
        iwLed1 = (ImageView) findViewById(R.id.notOn);
        iwLed2 = (ImageView) findViewById(R.id.notEmpty);
        iwLed3 = (ImageView) findViewById(R.id.notPerim);
        iwSpeaker = (ImageView) findViewById(R.id.noSpeaker);

        // Déclare les Text view
        twLed1 = (TextView) findViewById(R.id.SLedOn);
        twLed2 = (TextView) findViewById(R.id.SLedEmpty);
        twLed3 = (TextView) findViewById(R.id.SLedPerim);
        twSpeaker = (TextView) findViewById(R.id.SSpeakerOn);

        handler1 = new Handler();
        runnable1 = new Runnable() {
            @Override
            public void run() {
                actualise();
                handler1.postDelayed(this, 2000);
            }
        };
        handler1.postDelayed(runnable1,800);



        // Mise en place du bouton actualiser
        Button buttonRecette = (Button) findViewById(R.id.recette);
        buttonRecette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListeRecette.class);
                startActivity(i);
            }
        });

    }


    public void actualise(){

        // On récupère les données sur le singleton Etat
        Etat.getInstance(MainActivity.this).sendData();
        handler2 = new Handler();
        runnable2 = new Runnable(){
            @Override
            public void run(){
                try {
                    flag_empty = Etat.getInstance(MainActivity.this).getEmpty();
                    flag_perim = Etat.getInstance(MainActivity.this).getPerim();
                }
                catch(NullPointerException e){
                    Toast.makeText(MainActivity.this,"Veuillez vérifier votre connection internet...",Toast.LENGTH_LONG).show();
                }
            }
        };
        handler2.postDelayed(runnable2,500);

        if (flag_connect == true) {
            iwLed1.setImageResource(R.drawable.green_circle);
            twLed1.setText(R.string.SLedon);
        }
        else {
            iwLed1.setImageResource(R.drawable.grey_circle);
            twLed1.setText("");
        }

        if (flag_empty == true) {
            iwLed2.setImageResource(R.drawable.red_circle);
            twLed2.setText(R.string.SLedempty);
            if (connected) rfduinoService.send("Vide".getBytes());
        }
        else {
            iwLed2.setImageResource(R.drawable.grey_circle);
            twLed2.setText("");
            if (connected) rfduinoService.send("Plein".getBytes());
        }

        if (flag_perim == true) {
            iwLed3.setImageResource(R.drawable.red_circle);
            twLed3.setText(R.string.SLedperim);
            if (connected) rfduinoService.send("Perim".getBytes());
        }
        else {
            iwLed3.setImageResource(R.drawable.grey_circle);
            twLed3.setText("");
            if (connected) rfduinoService.send("Sain".getBytes());
        }


        if (flag_porte == true) {
            iwSpeaker.setImageResource(R.drawable.speaker);
            twSpeaker.setText(R.string.SSpeaker);
        }
        else {
            iwSpeaker.setImageResource(R.drawable.speaker2);
            twSpeaker.setText("");
        }
    }

    private final BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (state == BluetoothAdapter.STATE_ON) {
                upgradeState(STATE_DISCONNECTED);
            } else if (state == BluetoothAdapter.STATE_OFF) {
                downgradeState(STATE_BLUETOOTH_OFF);
            }
        }
    };

    private final BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            scanning = (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_NONE);
            scanStarted &= scanning;
            updateUi();
        }
    };


    private final ServiceConnection rfduinoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            rfduinoService = ((RFduinoService.LocalBinder) service).getService();
            if (rfduinoService.initialize()) {
                try {
                    // On obtient null ici... (sur 1 portable sur 2, appareillage?)
                    String address = bluetoothDevice.getAddress();
                    if (rfduinoService.connect(address)) {
                        upgradeState(STATE_CONNECTING);
                    }
                }
                catch(NullPointerException e){
                    Log.d(TAG,"Connection à l'OptaliBox a échoué, pas de service bluetooth détecté!");
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            rfduinoService = null;
            downgradeState(STATE_DISCONNECTED);
        }
    };


    private final BroadcastReceiver rfduinoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (RFduinoService.ACTION_CONNECTED.equals(action)) {
                upgradeState(STATE_CONNECTED);
            } else if (RFduinoService.ACTION_DISCONNECTED.equals(action)) {
                downgradeState(STATE_DISCONNECTED);
            } else if (RFduinoService.ACTION_DATA_AVAILABLE.equals(action)) {
                addData(intent.getByteArrayExtra(RFduinoService.EXTRA_DATA));
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(scanModeReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(rfduinoReceiver, RFduinoService.getIntentFilter());

        updateState(bluetoothAdapter.isEnabled() ? STATE_DISCONNECTED : STATE_BLUETOOTH_OFF);

        handler1 = new Handler();
        handler1.post(runnable1);
        handler2 = new Handler();
        handler2.post(runnable2);
    }


    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateUi();
    }

    public void stop(){
        bluetoothAdapter.stopLeScan(this);

        unregisterReceiver(scanModeReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(rfduinoReceiver);

        handler1.removeCallbacks(runnable1);
        handler2.removeCallbacks(runnable2);
    }

    private void upgradeState(int newState) {
        if (newState > state) {
            updateState(newState);
        }
    }

    private void downgradeState(int newState) {
        if (newState < state) {
            updateState(newState);
        }
    }

    private void updateState(int newState) {
        state = newState;
        updateUi();
    }


    private void updateUi() {
        // Enable Bluetooth
        boolean on = state > STATE_BLUETOOTH_OFF;
        enableBluetoothButton.setEnabled(!on);
        enableBluetoothButton.setText(on ? "Bluetooth enabled" : "Enable Bluetooth");
        scanButton.setEnabled(on);

        // Scan
        if (scanStarted && scanning) {
            scanStatusText.setText("Scanning...");
            scanButton.setText("Stop Scan");
            scanButton.setEnabled(true);
        } else if (scanStarted) {
            scanStatusText.setText("Scan started...");
            scanButton.setEnabled(false);
        } else {
            scanStatusText.setText("");
            scanButton.setText("Scan");
            scanButton.setEnabled(true);
        }

        // Connect
        String connectionText = "Disconnected";
        if (state == STATE_CONNECTING) {
            connectionText = "Connecting...";
        } else if (state == STATE_CONNECTED) {
            connected = true;
            scanStatusText.setText("");
            scanButton.setText("Connecté au RFOptali");
            scanButton.setEnabled(false);
            connectionText = "Connected";
        }

       connectionStatusText.setText(connectionText);
    }


    private void addData(byte[] data) {

        String data_received = HexAsciiHelper.bytesToAsciiMaybe(data);

        if (data_received.contains("On")) {
            flag_connect = true;
        }

        else if(data_received.contains("Off")) {
            flag_connect = false;
        }

        else if (data_received.contains("Open")) {
            flag_porte = true;
        }

        else if (data_received.contains("Close")) {
            flag_porte = false;
        }

    }


    @Override
    public void onLeScan(BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        bluetoothAdapter.stopLeScan(this);
        bluetoothDevice = device;

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUi();
            }
        });
    }

}