package com.example.zoldseges.DAOS;

public class Termek {
    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    public String getAllergenek() {
        return allergenek;
    }

    public void setAllergenek(String allergenek) {
        this.allergenek = allergenek;
    }

    public boolean isRaktaron() {
        return raktaron;
    }

    public void setRaktaron(boolean raktaron) {
        this.raktaron = raktaron;
    }

    public int getRaktaronLevoMennyiseg() {
        return raktaronLevoMennyiseg;
    }

    public void setRaktaronLevoMennyiseg(int raktaronLevoMennyiseg) {
        this.raktaronLevoMennyiseg = raktaronLevoMennyiseg;
    }

    private String nev;
    private int ar;
    private String allergenek;
    private boolean raktaron;
    private int raktaronLevoMennyiseg;

    public Termek(String nev, int ar, String allergenek, boolean raktaron, int raktaronLevoMennyiseg) {
        this.nev = nev;
        this.ar = ar;
        this.allergenek = allergenek;
        this.raktaron = raktaron;
        this.raktaronLevoMennyiseg = raktaronLevoMennyiseg;
    }
}
