package com.app.optali;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "ListeActivity.java";
    public static final String REGISTER_DELETE_URL = "http://89.80.34.165/optali/delete.php";
    public static final String REGISTER_CONSUME_URL = "http://89.80.34.165/optali/consume.php";
    public static final String KEY_PRODUCT = "Product";
    public static final String KEY_DATE = "Date";
    public static final int REFRESH_TIME = 300;
    public int nbre;
    private List<Produit> arrayList;


    private Button bSuppr;
    private Button bConsume;
    private EditText eFindProduct;

    private RadioGroup radioGroup;
    private RadioButton radioName;
    private RadioButton radioDate;
    private RadioButton radioStock;
    private RadioButton radioHisto;
    private RadioButton radioSuppr;


    private String rech;
    private int intRadio;
    private TableLayout tableLayout;
    private CheckBox[] checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_alim);

        nbre=0;
        // Initialisation buttons
        bSuppr = (Button) findViewById(R.id.suppr);
        bSuppr.setOnClickListener(this);
        bConsume = (Button) findViewById(R.id.consume);
        bConsume.setOnClickListener(this);

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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        doAndRefresh();

        // A chaque changement de text, actualiser la liste
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
                refreshList();
            }
        });

        // A chaque changement de radiobouton, actualiser la liste.
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.triNom){
                    intRadio=0;
                }

                if(checkedId==R.id.triDate){
                    intRadio=1;
                }

                if(checkedId==R.id.triStock){
                    intRadio=2;
                }

                if(checkedId==R.id.trihisto){
                    intRadio=3;
                }

                // On indique la condition de tri
                for (Produit prod : arrayList){
                    prod.setOrder(intRadio);
                }
                refreshList();


                if(checkedId==R.id.triSuppr){
                    checkall();
                }
            }
        });

    }


    // méthodes
    public void doAndRefresh(){
        // Récupération du tableau de produit
        Etat.getInstance(ListeActivity.this).sendData();
        this.arrayList=Etat.getInstance(ListeActivity.this).getList();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                // Après REFRESH_TIME millisecondes, on refresh la liste.
                refreshList();
            }
        },REFRESH_TIME);
    }


    public void checkall(){
        int i;
        for(i=0;i<nbre;i++){
            checkBox[i].setChecked(true);
        }
    }

    public void onClick(View v) {

        if (v.getId() == R.id.suppr) {
            for(int i=0;i<nbre;i++){
                if(checkBox[i].isChecked()) {
                    delete(arrayList.get(i).getNom(),arrayList.get(i).getDate(),REGISTER_DELETE_URL);
                }
            }
        }

        if (v.getId() == R.id.consume) {
            for(int i=0;i<nbre;i++) {
                if (checkBox[i].isChecked()) {
                    if (arrayList.get(i).getStock().compareTo("0") > 0) {
                        delete(arrayList.get(i).getNom(), arrayList.get(i).getDate(), REGISTER_CONSUME_URL);
                    }

                }
            }
        }
    }


    public void refreshList(){
        // On trie la liste
        Collections.sort(arrayList);
        tableLayout.removeAllViews();
        nbre=0;
        for(Produit produit : arrayList){
            if(rech!=null){
                if(produit.getNom().startsWith(rech)) {
                    createRow(produit.getNom(),produit.getDate(),produit.getStock(),produit.getHistorique(),nbre);
                    nbre++;
                }
            }
            else{
                createRow(produit.getNom(),produit.getDate(),produit.getStock(),produit.getHistorique(),nbre);
                nbre++;
            }
        }
    }


    // Methode d'envoi de la date et du produit au server.
    private void delete(String dnom, String ddate, String REGISTER) {
        final String product = dnom;
        final String date = ddate;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ListeActivity.this,response,Toast.LENGTH_LONG).show();
                        doAndRefresh();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListeActivity.this,error.toString(),Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ListeActivity.this);
        requestQueue.add(stringRequest);
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
