package com.app.optali;

/**
 * Created by root on 17/04/17.
 */

public class Etat {

    private Boolean perim;
    private Boolean empty;

    public Boolean getPerim() {return perim;}
    public Boolean getEmpty() {return empty;}
    public void setPerim(Boolean perim) {this.perim = perim;}
    public void setEmpty(Boolean empty) {this.empty = empty;}

    private static final Etat etat = new Etat();
    public static Etat getInstance() {return etat;}
}
