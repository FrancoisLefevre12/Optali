package com.app.optali;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private Boolean beTheLight;
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

    private int state;

    private boolean scanStarted;
    private boolean scanning;

    private boolean flag_connect=false,flag_perim=false,flag_empty=false,flag_porte=false;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;

    private RFduinoService rfduinoService;

    private Button enableBluetoothButton;
    private TextView scanStatusText;
    private Button scanButton;
    private Button sendValueButton;



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



     /*   // Connect Device
        connectionStatusText = (TextView) findViewById(R.id.connectionStatus);

        connectButton = (Button) findViewById(R.id.connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                connectionStatusText.setText("Connecting...");
            }
        });
*/

        /* // Send
        valueEdit = (EditData) findViewById(R.id.value);
        valueEdit.setImeOptions(EditorInfo.IME_ACTION_SEND);
        valueEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendValueButton.callOnClick();
                    return true;
                }
                return false;
            }
        });
*/
        
        sendValueButton = (Button) findViewById(R.id.sendValue);
        sendValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rfduinoService.send("coucou".getBytes());
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

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                actualise();
                handler.postDelayed(this, 1000);
            }
        }, 300);



        // Mise en place du bouton actualiser
        Button buttonActu = (Button) findViewById(R.id.actualiser);
        buttonActu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actualise();
            }
        });

    }

    public void actualise(){
        // On récupère les données sur le singleton Etat
        Etat.getInstance(MainActivity.this).sendData();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                flag_empty=Etat.getInstance(MainActivity.this).getEmpty();
                flag_perim=Etat.getInstance(MainActivity.this).getPerim();
            }
        },300);

        if (flag_connect == true) {
            iwLed1.setImageResource(R.drawable.green_circle);
            twLed1.setText(R.string.SLedon);
        }
        else {
            iwLed1.setImageResource(R.drawable.grey_circle);
            twLed1.setText("");
        }

        //Il faudra activer ou non ce flag via la base de données
        if (flag_empty == true) {
            iwLed2.setImageResource(R.drawable.red_circle);
            twLed2.setText(R.string.SLedempty);
        }
        else {
            iwLed2.setImageResource(R.drawable.grey_circle);
            twLed2.setText("");
        }

        //Idem que pour flag_empty
        if (flag_perim == true) {
            iwLed3.setImageResource(R.drawable.red_circle);
            twLed3.setText(R.string.SLedperim);
        }
        else {
            iwLed3.setImageResource(R.drawable.grey_circle);
            twLed3.setText("");
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
                if (rfduinoService.connect(bluetoothDevice.getAddress())) {
                    upgradeState(STATE_CONNECTING);
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
    }


    @Override
    protected void onStop() {
        super.onStop();

        bluetoothAdapter.stopLeScan(this);

        unregisterReceiver(scanModeReceiver);
        unregisterReceiver(bluetoothStateReceiver);
        unregisterReceiver(rfduinoReceiver);
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
        boolean connected = false;
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
       // connectionStatusText.setText(connectionText);
       // connectButton.setEnabled(bluetoothDevice != null && state == STATE_DISCONNECTED);

        sendValueButton.setEnabled(connected);

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