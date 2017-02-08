package com.app.optali;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mise en place du bouton ajout page
        Button buttonAjout = (Button) findViewById(R.id.ajoutPage);
        buttonAjout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),AjoutActivity.class);
                startActivity(i);
            }
        });

        // Déclare les images
        iwLed1=(ImageView)findViewById(R.id.notOn);
        iwLed2=(ImageView)findViewById(R.id.notEmpty);
        iwLed3=(ImageView)findViewById(R.id.notPerim);
        iwSpeaker=(ImageView)findViewById(R.id.noSpeaker);

        // Déclare les Text view
        twLed1=(TextView)findViewById(R.id.SLedOn);
        twLed2=(TextView)findViewById(R.id.SLedEmpty);
        twLed3=(TextView)findViewById(R.id.SLedPerim);
        twSpeaker=(TextView)findViewById(R.id.SSpeakerOn);


        // Debug Allume LED
        beTheLight = false;

        // Mise en place du bouton actualiser
        Button buttonActu = (Button) findViewById(R.id.actualiser);
        buttonActu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (beTheLight==false) {
                    iwLed1.setImageResource(R.drawable.green_circle);
                    iwLed2.setImageResource(R.drawable.red_circle);
                    iwLed3.setImageResource(R.drawable.red_circle);
                    iwSpeaker.setImageResource(R.drawable.speaker);

                    twLed1.setText(R.string.SLedon);
                    twLed2.setText(R.string.SLedempty);
                    twLed3.setText(R.string.SLedperim);
                    twSpeaker.setText(R.string.SSpeaker);
                    beTheLight = true;
                } else  {
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
}




