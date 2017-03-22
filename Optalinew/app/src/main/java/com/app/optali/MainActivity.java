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
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@TargetApi(21)
public class MainActivity extends AppCompatActivity {

    private Boolean beTheLight;
    ImageView iwLed1;
    ImageView iwLed2;
    ImageView iwLed3;
    ImageView iwSpeaker;

    TextView twLed1;
    TextView twLed2;
    TextView twLed3;
    TextView twSpeaker;

    //Variables pour le bluetooth

    private BluetoothAdapter mBluetoothAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        //On vérifie que notre système supporte le BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        //Initialisation de la gestion du bluetooth
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


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


        // Debug Allume LED
        beTheLight = false;

        // Mise en place du bouton actualiser
        Button buttonActu = (Button) findViewById(R.id.actualiser);
        buttonActu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (beTheLight == false) {
                    iwLed1.setImageResource(R.drawable.green_circle);
                    iwLed2.setImageResource(R.drawable.red_circle);
                    iwLed3.setImageResource(R.drawable.red_circle);
                    iwSpeaker.setImageResource(R.drawable.speaker);

                    twLed1.setText(R.string.SLedon);
                    twLed2.setText(R.string.SLedempty);
                    twLed3.setText(R.string.SLedperim);
                    twSpeaker.setText(R.string.SSpeaker);
                    beTheLight = true;
                } else {
                    iwLed1.setImageResource(R.drawable.grey_circle);
                    iwLed2.setImageResource(R.drawable.grey_circle);
                    iwLed3.setImageResource(R.drawable.grey_circle);
                    iwSpeaker.setImageResource(R.drawable.speaker2);

                    twLed1.setText("");
                    twLed2.setText("");
                    twLed3.setText("");
                    twSpeaker.setText("");
                    beTheLight = false;
                }

            }
        });

    }


    //Définition des paramètres bluetooth lors de la reprise de l'application
    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
            scanLeDevice(true);
        }
    }


    //Fonction à réaliser lorsqu'on met l'appli en tâche de fond
    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }


    //Fonction produite lors de la fermeture de l'appli
    @Override
    protected void onDestroy() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
        super.onDestroy();
    }


    //Fonction qui va nous permettre de récupérer les infos de ce que le BLE va traiter
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //Permet de retrouver les différents systèmes BLE dans notre environnement
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, mScanCallback);
        } else {
            mLEScanner.stopScan(mScanCallback);
        }
    }


    //Gère les différentes fonctions de retour suite à un scan et réalise
    //un certain affichage en conséquence
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            //"D8:CA:58:FB:73:79"
            BluetoothDevice btDevice = result.getDevice();
            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };


    //Permet de se connecter à un appareil bluetooth
    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            scanLeDevice(false);// will stop after first device detection
        }
    }

    //Effectue un retour sur la tentative de connexion à un appareil BLE
    //Donne la liste des services disponibles ainsi que les caractéristiques du gatt
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };
}