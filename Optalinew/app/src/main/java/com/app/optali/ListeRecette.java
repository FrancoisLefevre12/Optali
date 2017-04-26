package com.app.optali;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ListeRecette extends AppCompatActivity {

    public static final String REGISTER_CONSUME_URL = "http://89.80.34.165/optali/listeRecette.php";
    private Button bSuppr;
    private EditText eRecherche;


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
    }

    /*
    // Mauvais bail parce l'on ne peut pas modifier en menu deroulant en fonction de l'où on appui.
    public void OnCreateMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        menu.setHeaderTitle("Ingrédients");
        menu.add(0,v.getId(),0,"Youyou1"); //GroupId, itemId, order, title
        menu.add(0,v.getId(),0,"Youyou2");
    }
    */


}
