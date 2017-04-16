package com.app.optali;

import java.io.Serializable;

/**
 * Created by ahuet67 on 15/03/17.
 */

public class Produit implements Comparable<Produit>{
    protected String nom;
    protected String date;
    protected String stock;
    protected String historique;
    protected int order;
    // 0 = Nom, 1= Date, 2=Stock, 3=historique

    public Produit(String nom, String date, String stock, String historique){
        this.setNom(nom);
        this.setDate(date);
        this.setStock(stock);
        this.setHistorique(historique);
        // Trié par Date par défaut
        this.setOrder(1);
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

    public int getOrder() {return order;}

    public void setOrder(int order) {
        this.order= order;
    }

    @Override
    public int compareTo(Produit o) {
        if (order==0){
            return this.getNom().compareTo(o.getNom());
        }
        else if (order==1){
            return this.getDate().compareTo(o.getDate());
        }
        else if (order==2){
            return this.getStock().compareTo(o.getStock());
        }
        else if (order==3){
            return this.getHistorique().compareTo(o.getHistorique());
        }
        else{
            return 0;
        }

    }

}
