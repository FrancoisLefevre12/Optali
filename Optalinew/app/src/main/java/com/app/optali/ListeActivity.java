package com.app.optali;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

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

    private ListView listView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    String[] prenoms=new String[]{"Jean","Patrick","Pascal","Rodolphou"};


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


        listView = (ListView) findViewById(R.id.list_ing);
        // String à changer on doit mettre 5 string
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListeActivity.this, android.R.layout.simple_list_item_1,prenoms);
        listView.setAdapter(adapter);}

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

}
