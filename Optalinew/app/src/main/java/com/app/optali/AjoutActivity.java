package com.app.optali;

import android.content.Intent;
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

// Import lecteur de caractère
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import java.io.File;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.googlecode.tesseract.android.TessBaseAPI;

// Import pour l'envoie vers le serveur
import com.android.volley.toolbox.Volley;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AjoutActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String REGISTER_URL = "http://89.80.34.165/optali/post.php";
    public static final String KEY_PRODUCT = "Product";
    public static final String KEY_DATE = "Date";


    private Button scan;
    private Button scan2;
    private Button send;
    private boolean switchCam;

    private EditText EditTextProduct;
    private EditText EditTextDate;
    // Quand false alors appel à la première caméra sinon la deuxième

    /*
    // Variables dont on a l'air d'avoir besoin pour la détection de caractères
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/AjoutActivity/";
    public static final String TAG="AjoutActivity.java";
    public static final String lang="fra";
    String path= DATA_PATH+"/ocr.jpg";
*/

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


        /*
        // Creation d'un dossier et donc fichier image pour faire la lecture de caractère par la suite.
        String[] paths =new String[] {DATA_PATH,DATA_PATH+"tessdata/"};

        for(String path : paths){
            File dir=new File(path);
        }


        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Unable to copy " + lang + " traineddata " + e.toString());
            }
        }*/
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

        }

        if(v.getId() == R.id.envoi_ajout){
            sendData();
        }

    }

    // Methode d'envoi de la date et du produit au server.
    private void sendData() {
        final String product = EditTextProduct.getText().toString();
        final String date = EditTextDate.getText().toString();

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
                System.out.println(product);
                params.put(KEY_DATE,date);
                System.out.println(date);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        //if (switchCam=false){ //camera 1

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
        //else { //Camera deux

        //}
    }

    /*
    protected void onPhotoTaken(){

        // Aide fitou
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inSampleSize=4;
        Bitmap bitmap=BitmapFactory.decodeFile(path,options);


        // Pour la lecture de caractère
        try {
            ExifInterface exif = new ExifInterface(path);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.postRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }
        } catch(IOException e){
            Log.e(TAG, "On ne peut pas trouver la bonne orientation: "+e.toString());
        }

        // Convert to ARGB_8888, required by tess
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);
        String recognizedText = "";
        recognizedText = baseApi.getUTF8Text();
        baseApi.end();

    }


}*/
