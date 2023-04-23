package com.example.zoldseges.DAOS;

public class KosarElem {
    private Termek termek;
    private double mennyiseg;

    public KosarElem(Termek termek, Double mennyiseg) {
        this.termek = termek;
        this.mennyiseg = mennyiseg;
    }

    public KosarElem() {
    }

    public Termek getTermek() {
        return termek;
    }

    public void setTermek(Termek termek) {
        this.termek = termek;
    }

    public double getMennyiseg() {
        return mennyiseg;
    }

    public void setMennyiseg(Double mennyiseg) {
        this.mennyiseg = mennyiseg;
    }
}
