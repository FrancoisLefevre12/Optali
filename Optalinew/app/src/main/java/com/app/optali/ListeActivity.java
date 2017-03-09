package com.app.optali;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class ListeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bSuppr;
    private Button bFindRecipe;
    private EditText eFindProduct;
    private RadioButton radioName;
    private RadioButton radioDate;
    private RadioButton radioStock;
    private RadioButton radioHisto;
    private RadioButton radioSuppr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_alim);


        // Initialisation buttons
        bSuppr = (Button) findViewById(R.id.suppr);
        bSuppr.setOnClickListener(this);
        bFindRecipe = (Button) findViewById(R.id.recherche_recette);
        bFindRecipe.setOnClickListener(this);

        // Initialisation EditText
        eFindProduct = (EditText) findViewById(R.id.recherche_produit);

        // Initialisation RadioButton
        radioName = (RadioButton) findViewById(R.id.triNom);
        radioName.setOnClickListener(this);

        radioDate = (RadioButton) findViewById(R.id.triDate);
        radioDate.setOnClickListener(this);

        radioStock = (RadioButton) findViewById(R.id.triStock);
        radioStock.setOnClickListener(this);

        radioHisto = (RadioButton) findViewById(R.id.trihisto);
        radioHisto.setOnClickListener(this);

        radioSuppr = (RadioButton) findViewById(R.id.triSuppr);
        radioSuppr.setOnClickListener(this);


    }


    public void onClick(View v) {


        if (v.getId() == R.id.suppr) {
        }

    }

}
