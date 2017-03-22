package com.app.optali;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String REGISTER_URL = "http://89.80.34.165/optali/get.php";
    public int nbre;


    private Button bSuppr;
    private Button bFindRecipe;
    private EditText eFindProduct;

    private RadioGroup radioGroup;
    private RadioButton radioName;
    private RadioButton radioDate;
    private RadioButton radioStock;
    private RadioButton radioHisto;
    private RadioButton radioSuppr;


    private String rech;
    private String intRadio;
    private TableLayout tableLayout;
    private CheckBox[] checkBox;

    //protected List<Produit> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_alim);

        //array = new ArrayList<Produit>();

        nbre=0;
        // Initialisation buttons
        bSuppr = (Button) findViewById(R.id.suppr);
        bSuppr.setOnClickListener(this);
        bFindRecipe = (Button) findViewById(R.id.recherche_recette);
        bFindRecipe.setOnClickListener(this);

        // Initialisation EditText
        eFindProduct = (EditText) findViewById(R.id.recherche_produit);

        // Initialisation RadioButton
        radioGroup = (RadioGroup) findViewById(R.id.rdGroup);
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

        /*
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
        createRow("Pizzaaa","2017-04-18","1","5",10);*/

        sendData();
        // A chaque changement de text, faire la requête
        eFindProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Rien a faire
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Rien a faire
            }

            @Override
            public void afterTextChanged(Editable s) {
                rech=eFindProduct.getText().toString();
                sendData();
            }
        });

        // A chaque changement de radiobouton, faire la requete.
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.triNom){
                    intRadio="0";
                }

                if(checkedId==R.id.triDate){
                    intRadio="1";
                }

                if(checkedId==R.id.triStock){
                    intRadio="2";
                }

                if(checkedId==R.id.trihisto){
                    intRadio="3";
                }

                if(checkedId==R.id.triSuppr){
                    checkall();
                }
                sendData();
            }
        });
    }


    // méthodes
    public void checkall(){
    }

    public void onClick(View v) {


        if (v.getId() == R.id.suppr) {
        }

    }


    public void sendData(){
        //array.clear();

        JsonArrayRequest jsonArrayRequest =new JsonArrayRequest(Request.Method.POST, REGISTER_URL, (String)null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    nbre=0;

                        while(nbre<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(nbre);
                                Produit produit = new Produit(jsonObject.getString("sProduct"),jsonObject.getString("sDate"),jsonObject.getString("sStock"),jsonObject.getString("sHisto"));
                                createRow(produit.getNom(),produit.getDate(),produit.getStock(),produit.getHistorique(),nbre);
                                nbre++;
                                //array.add(produit);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ListeActivity.this,"Bonjouuuur",Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                }   ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListeActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }

        );
        MySingleton.getInstance(ListeActivity.this).addToRequestQueue(jsonArrayRequest);

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
        tHisto.setText(s4);
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
