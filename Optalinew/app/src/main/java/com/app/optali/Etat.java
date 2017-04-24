package com.app.optali;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Etat {

    public static final int SEUIL_NB_ALIM = 30;
    public static final int SEUIL_NB_JOURS = 3;

    private Boolean perim;
    private Boolean empty;
    private List<Produit> arrayList;
    private int nbre;
    private static Etat mEtat;
    private static Context context;

    public Boolean getPerim() {return perim;}
    public Boolean getEmpty() {return empty;}
    public List<Produit> getList() {return arrayList;}
    public void setPerim(Boolean perim) {this.perim = perim;}
    public void setEmpty(Boolean empty) {this.empty = empty;}

    private Etat(Context context){
        this.context=context;
    }

    public static synchronized Etat getInstance(Context context){
        if (mEtat==null){
            mEtat=new Etat(context);
        }
        return mEtat;
    }


    // Méthodes d'appel à la base de donnée

    public static final String REGISTER_URL = "http://89.80.34.165/optali/get.php";

    public void sendData(){
        arrayList = new ArrayList<Produit>();
        JsonArrayRequest jsonArrayRequest =new JsonArrayRequest(Request.Method.POST, REGISTER_URL, (String)null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        nbre=0;

                        while(nbre<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(nbre);

                                Produit produit = new Produit(jsonObject.getString("sProduct"),jsonObject.getString("sDate"),jsonObject.getString("sStock"),jsonObject.getString("sHisto"));
                                nbre++;
                                arrayList.add(produit);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        checkData(arrayList);
                    }
                }   ,
                new Response.ErrorListener() {
                    // The error is captured here.
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }

        );
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    // Méthode de traitement de la liste des produits.
    // Envoie true si un aliment est proche de périmer
    // Envoie true si le frigo est vide ou bientôt vide
    public void checkData(List<Produit> list){
        int cpt=0;
        Boolean perim=false;

        Date now=new Date();
        Date date = new Date(now.getTime());
        Date dProd = null;
        SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
        for(Produit prod : list){
            cpt+=Integer.parseInt(prod.getStock());
            try {
                dProd =textFormat.parse(prod.getDate());
            }catch (java.text.ParseException e){}
            if(daysBetween(date,dProd)<SEUIL_NB_JOURS){
                perim=true;
            }
        }

        // On envoie les données sur le singleton Etat
        this.setEmpty(cpt<=SEUIL_NB_ALIM);
        this.setPerim(perim);
    }

    // Méthode pour compter le nombre de jours entre 2 dates
    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }


}
