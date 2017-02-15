package com.app.optali;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Import de la librairie zxing
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AjoutActivity extends AppCompatActivity  implements View.OnClickListener{

    private Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout);

        scan= (Button)findViewById(R.id.camera_code);
        scan.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.camera_code){
            // on lance le scanner au clic sur notre bouton
            new IntentIntegrator(this).initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
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


}
