package com.app.optali;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class ListeRecette extends AppCompatActivity {

    public static final String REGISTER_RECIPE_URL = "http://89.80.34.165/optali/listeRecette.php";
    public static final String[] listString = {"avocat","crevette"};
    public static final String[] listRecettes = {"Gateau au chocolat","Lasagne","Avocat-Crevettes"};
    public static final int[] listValidation = {0,0,1};
    private List<Recette> recettes;
    private Button bSuppr;
    private EditText eRecherche;
    private ListView listView;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_recette);

        bSuppr = (Button) findViewById(R.id.suppr);
        bSuppr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        eRecherche = (EditText) findViewById(R.id.recherche_recette);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tableLayout = (TableLayout) findViewById(R.id.tableRecette);

        for(int i=0;i<listRecettes.length;i++){
            createRow(listRecettes[i],listValidation[i]);
        }
    }


    public void showDialogListView(View view){
        // Dès qu'une recette est cliquée, on regarde les aliments dont on a besoin...
        listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.liste_ingredients,R.id.textItem,listString);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder=new AlertDialog.Builder(ListeRecette.this);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok",null);
        builder.setView(listView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void createRow(String s1,int bo){
        // On crée une ligne avec les paramètres match_parent
        final TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un textView du nom
        final TextView tRecipe = new TextView(this);
        tRecipe.setText(s1);
        tRecipe.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        tRecipe.setGravity(Gravity.CENTER_VERTICAL);
        tRecipe.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

        final ImageView okImage = new ImageView(this);
        if(bo==1) {
            okImage.setImageResource(R.drawable.ok);
        }
        else{
            okImage.setImageResource(R.mipmap.no);
        }
        okImage.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        tableRow.addView(tRecipe);
        tableRow.addView(okImage);

        tableLayout.addView(tableRow);
    }

    public void sendData(){
        recettes = new ArrayList<Recette>();
        JsonArrayRequest jsonArrayRequest =new JsonArrayRequest(Request.Method.POST, REGISTER_RECIPE_URL, (String)null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        int nbre=0;
                        while(nbre<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(nbre);
                                String testRecette =jsonObject.getString("sNomRecette");
                                int bo=0,i=0;
                                // On cherche si la recette existe déjà ou pas
                                while(i<=recettes.size()&&bo==0){
                                    if(recettes.get(i).getNom().compareTo(testRecette)==0){
                                        bo=1;
                                    }
                                    i++;
                                }
                                // S'il n'existe pas, alors on le crée et on le stock dans la liste
                                if(bo==0){
                                    Recette recette = new Recette(testRecette,jsonObject.getString("sAliment"));
                                    recettes.add(recette);
                                }
                                else{
                                    recettes.get(i-1).addAliment(jsonObject.getString("sAliment"));
                                }
                                nbre++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // Une fois que l'acquisition est terminée, on met à jour la liste
                        refreshList();
                    }
                }   ,
                new Response.ErrorListener() {
                    // The error is captured here.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListeRecette.this,error.toString(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }

        );
        MySingleton.getInstance(ListeRecette.this).addToRequestQueue(jsonArrayRequest);
    }


    public void refreshList(){

        tableLayout.removeAllViews();

        // On met a jour la faisabilité
        for(Recette recette : recettes){
            for(String str : recette.getAliments()){
                for(Produit p : Etat.getInstance(ListeRecette.this).getList()){
                    if(p.getNom()==str){
                        if(Integer.parseInt(p.getStock())<=0){
                            recette.setFaisable(0);
                        }
                    }
                }
            }
        }

        for(Recette recette :recettes){
            createRow(recette.getNom(),recette.getFaisable());
        }

    }

}
