package com.app.optali;

/**
 * Created by ahuet67 on 15/03/17.
 */

public class Produit {
    protected String nom;
    protected String date;
    protected String stock;
    protected String historique;

    public Produit(String nom, String date, String stock, String historique){
        this.setNom(nom);
        this.setDate(date);
        this.setStock(stock);
        this.setHistorique(historique);
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getHistorique() {
        return historique;
    }

    public void setHistorique(String historique) {
        this.historique = historique;
    }

}
