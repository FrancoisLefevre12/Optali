package com.app.optali;


import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ListeRecette extends AppCompatActivity {

    public static final String REGISTER_CONSUME_URL = "http://89.80.34.165/optali/listeRecette.php";
    public static final String[] listString = {"Jean","Pierre","Pascal"};
    public static final String[] listRecettes = {"Gateau au chocolat","Lasagne","Pizza"};
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

        for(String s : listRecettes){
            createRow(s);
        }

        // Dès qu'une recette est cliquée, on regarde les aliments dont on a besoin...
        listView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.liste_ingredients,R.id.textItem,listString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                ViewGroup viewGroup= (ViewGroup) view;
                                                TextView txt = (TextView) viewGroup.findViewById(R.id.textItem);
                                                Toast.makeText(ListeRecette.this,txt.getText().toString(),Toast.LENGTH_LONG).show();

                                            }
                                        });

    }


    public void showDialogListView(View view){
        AlertDialog.Builder builder=new AlertDialog.Builder(ListeRecette.this);
        builder.setCancelable(true);
        builder.setPositiveButton("Ok",null);
        builder.setView(listView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void createRow(String s1){
        // On crée une ligne avec les paramètres match_parent
        final TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));

        // Création d'un textView du nom
        final TextView tRecipe = new TextView(this);
        tRecipe.setText(s1);
        tRecipe.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        tRecipe.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

        tableRow.addView(tRecipe);

        tableLayout.addView(tableRow);
    }


}
