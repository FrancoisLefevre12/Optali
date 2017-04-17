package com.app.optali;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// Import lecteur de caractère
import android.os.Environment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.content.ActivityNotFoundException;
import android.media.ExifInterface;
import android.util.Log;
import java.io.File;
import android.content.res.AssetManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.googlecode.tesseract.android.TessBaseAPI;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

// Import pour l'envoie vers le serveur
import com.android.volley.toolbox.Volley;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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


    // Variables dont on a l'air d'avoir besoin pour la détection de caractères
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/AjoutActivity/tesseract/";
    //public static final String DATA_PATH = "/mnt/sdcard/tesseract/";
    public static final String TAG="AjoutActivity.java";
    public static final String lang="fra";
    public String path= DATA_PATH+"ocr.jpg";
    private static final String EXPECTED_FILE=DATA_PATH + "tessdata/" + lang + ".traineddata";


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

        runFile();

    }

    public void onClick(View v) {

        if(v.getId() == R.id.camera_code){
            // On interagit avec EditTextProduit
            Toast.makeText(AjoutActivity.this,"Veuillez scanner votre nom de produit",Toast.LENGTH_LONG).show();
            switchCam=false;
            startCameraActivity();

        }

        if(v.getId() == R.id.camera_date){
            // On interagit avec EditTextDate
            Toast.makeText(AjoutActivity.this,"Veuillez scanner votre date de péremption",Toast.LENGTH_LONG).show();
            switchCam=true;
            startCameraActivity();


        }

        if(v.getId() == R.id.envoi_ajout){
            //Toast.makeText(AjoutActivity.this,EditTextProduct.getText().toString(),Toast.LENGTH_LONG).show();
            if (EditTextProduct.getText()==null){
                //Toast.makeText(AjoutActivity.this,"Veuillez rentrer votre nom de produit",Toast.LENGTH_LONG).show();
            }
            else{
                sendData();
            }
        }

    }

    public String translateDate(String d1){
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat finalFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date datef = new Date();
        try {
                datef = originalFormat.parse(d1);
        }catch (java.text.ParseException e){}
        return finalFormat.format(datef);
    }

    // Methode d'envoi de la date et du produit au server.
    private void sendData() {
        final String product = EditTextProduct.getText().toString();
        final String date = translateDate(EditTextDate.getText().toString());

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


            Log.i(TAG, "resultCode: " + resultCode);

            if (resultCode == -1) {
                if (requestCode == 0) // Camera capture
                {
                    File file = new File(path);
                    performCrop(Uri.fromFile(file));

                }
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                if (resultCode == RESULT_OK)
                {
                    //On fait appelle au moteur de reconnaissance de caractère
                    onPhotoTaken();

                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error = result.getError();
                }
            } else {
                Log.v(TAG, "User cancelled");
            }


    }


    // Méthodes pour lancer la reconnaissance de caractères
    protected void startCameraActivity() {
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, 0);
    }


    protected void runFile(){
        // Creation d'un dossier et donc fichier image pour faire la lecture de caractère par la suite.
        String[] paths =new String[] {DATA_PATH,DATA_PATH+"tessdata/"};

        for(String path : paths){
            File dir=new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }
        }

        if (!(new File(EXPECTED_FILE)).exists()) {

            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open(DATA_PATH+"tessdata/" + lang + ".traineddata");
                OutputStream out = new FileOutputStream(EXPECTED_FILE);
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
        }
    }

    protected void performCrop(Uri data)
    {

        File file = new File(path);
        try
        {
            CropImage.activity(data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputUri(Uri.fromFile(file))
                    .start(this);

        }

        catch(ActivityNotFoundException e)
        {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    protected void onPhotoTaken(){

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

        // You now have the text in recognizedText var, you can do anything with it.
        // We will display a stripped out trimmed alpha-numeric version of it (if lang is eng)
        // so that garbage doesn't make it to the display.

        Log.v(TAG, "OCRED TEXT: " + recognizedText);

        if ( lang.equalsIgnoreCase("en") ) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

       recognizedText = recognizedText.trim();

        if ( recognizedText.length() != 0 ) {
            if (!switchCam){
                EditTextProduct.setText(recognizedText);
                EditTextProduct.setSelection(EditTextProduct.getText().toString().length());
            }
            else{
               EditTextDate.setText(recognizedText);
               EditTextDate.setSelection(EditTextDate.getText().toString().length());
            }

        }

    }


}
