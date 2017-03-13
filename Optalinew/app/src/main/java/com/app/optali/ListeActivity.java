package com.app.optali;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

public class ListeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String REGISTER_URL = "http://89.80.34.165/get.php";
    public static final String KEY_ORDER = "OrderBy";
    public static final String KEY_SEARCH = "Search";


    private Button bSuppr;
    private Button bFindRecipe;
    private EditText eFindProduct;
    private RadioButton radioName;
    private RadioButton radioDate;
    private RadioButton radioStock;
    private RadioButton radioHisto;
    private RadioButton radioSuppr;

    private TableLayout tableLayout;
    private CheckBox[] checkBox;


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

        tableLayout = (TableLayout) findViewById(R.id.table);
        checkBox = new CheckBox[100];

        createRow("Pizzaaa","2017-04-12","3","5",0);
        createRow("Riz","2017-04-12","3","4",1);
        createRow("semoule","2017-05-12","3","5",2);
        createRow("taboulé","2017-01-10","2","7",3);
        createRow("Pizzaaa","2017-04-18","1","5",4);
        createRow("semoule","2017-05-12","3","4",5);
        createRow("taboulé","2017-01-10","2","6",6);
        createRow("Pizzaaa","2017-04-18","1","5",7);
        createRow("semoule","2017-05-12","3","2",8);
        createRow("taboulé","2017-01-10","2","5",9);
        createRow("Pizzaaa","2017-04-18","1","5",10);

    }



/*
        // Volley prepare la requete
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, REGISTER_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject reponse) {
                        log.d("Reponse", reponse.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getRequest);
    }
*/

    public void onClick(View v) {


        if (v.getId() == R.id.suppr) {
        }

    }


    public void createRow(String s1, String s2, String s3, String s4, int nbre){
        // On crée une ligne avec les paramètres match_parent
        final TableRow tableRow = new TableRow (this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un textView du nom
        final TextView tProduct = new TextView(this);
        tProduct.setText(s1);
        tProduct.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un textView de la date
        final TextView tDate = new TextView(this);
        tDate.setText(s2);
        tDate.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un textView du Stock
        final TextView tStock= new TextView(this);
        tStock.setText(s3);
        tStock.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un textView de l'historique
        final TextView tHisto= new TextView(this);
        tHisto.setText(s3);
        tHisto.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un Checkbox
        checkBox[nbre]= new CheckBox(this);
        tHisto.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        tableRow.addView(tProduct);
        tableRow.addView(tDate);
        tableRow.addView(tStock);
        tableRow.addView(tHisto);
        tableRow.addView(checkBox[nbre]);

        tableLayout.addView(tableRow);
    }

}
