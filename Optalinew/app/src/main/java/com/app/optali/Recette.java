package com.app.optali;


import java.util.ArrayList;
import java.util.List;

public class Recette {
    protected String Nom;
    protected List<String> aliments;
    protected int faisable;

    public Recette(String Nom, String Alim){
        this.Nom=Nom;
        faisable=1;
        aliments=new ArrayList<String>();
        aliments.add(Alim);
    }

    public List<String> getAliments() {
        return aliments;
    }

    public void setAliments(List<String> aliments) {
        this.aliments = aliments;
    }

    public void addAliment(String Alim){
        aliments.add(Alim);
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public int getFaisable() {
        return faisable;
    }

    public void setFaisable(int faisable) {
        this.faisable = faisable;
    }
}

