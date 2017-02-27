package com.app.optali;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Import de la librairie zxing (lecture code barre)
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

// Import pour l'envoie vers le serveur
import com.android.volley.toolbox.Volley;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;


// Add implements EasyOcrScannerListener
public class AjoutActivity extends AppCompatActivity  implements View.OnClickListener{

    public static final String REGISTER_URL = "89.80.34.165/post.php";
    public static final String KEY_PRODUCT = "Product";
    public static final String KEY_DATE = "Date";


    private Button scan;
    private Button scan2;
    private Button send;

    private EditText EditTextProduct;
    private EditText EditTextDate;
    // Quand false alors appel à la première caméra sinon la deuxième
    private boolean switchCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout);

        switchCam=false; // Première caméra par défaut
        scan= (Button)findViewById(R.id.camera_code);
        scan.setOnClickListener(this);

        scan2= (Button)findViewById(R.id.camera_date);
        scan2.setOnClickListener(this);

        send=(Button)findViewById(R.id.envoi_ajout);
        send.setOnClickListener(this);

        EditTextProduct = (EditText) findViewById(R.id.code_barre);
        EditTextDate = (EditText) findViewById(R.id.date_etiquette);

        //mEasyOcrScanner = new EasyOcrScanner(AjoutActivity.this, "EasyOcrScanner", Config.REQUEST_CODE_CAPTURE_IMAGE, 'fr');
    }

    public void onClick(View v) {

        if(v.getId() == R.id.camera_code){
            // On pose switchCam=false
            switchCam=false;
            // on lance le scanner au clic sur notre bouton
            new IntentIntegrator(this).initiateScan();
        }

        if(v.getId() == R.id.camera_date){
            // On pose switchCam=true
            switchCam=true;
            // on lance le scanner au clic sur notre bouton
            //mEasyOcrScanner.takePicture();
        }

        if(v.getId() == R.id.envoi_ajout){
            registerUser();
        }

    }

    private void registerUser() {
        final String product = EditTextProduct.getText().toString().trim();
        final String date = EditTextDate.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AjoutActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AjoutActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(KEY_PRODUCT,product);
                params.put(KEY_DATE,date);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (switchCam=false){ //camera 1

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                // nous récupérons le contenu du code barre
                String scanContent = scanningResult.getContents();
                TextView scan_content = (TextView) findViewById(R.id.code_barre);

                // nous affichons le résultat dans nos TextView
                scan_content.setText(scanContent);
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Aucune donnée reçu!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else { //Camera deux
            /*
            super.onActivityResult(requestCode,resultCode,intent);

            if (resultCode==RESULT_OK && requestCode == Config.REQUEST_CODE_CAPTURE_IMAGE){
                mEasyOcrScanner.onImageTaken();
            }
            */
        }



    }

    public void onOcrScanFinished(Bitmap bitmap, String recognizedText){
        // nous récupérons le contenu de l'image
        TextView scan_content2 = (TextView) findViewById(R.id.date_etiquette);
        scan_content2.setText(recognizedText);
    }


}
