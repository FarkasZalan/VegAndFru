package com.example.zoldseges.DAOS;

import java.util.HashMap;
import java.util.Map;

public class Termek implements TermekDao {

    public String getNev() {
        return nev;
    }

    public double getAr() {
        return ar;
    }

    public int getRaktaronLevoMennyiseg() {
        return raktaronLevoMennyiseg;
    }

    public String getUzletId() {
        return uzletId;
    }

    public String getTermekKepe() {
        return termekKepe;
    }

    private  String nev;
    private  double ar;
    private  int raktaronLevoMennyiseg;
    private  String uzletId;

    public double getTermekSulya() {
        return termekSulya;
    }

    private  double termekSulya;

    public void setNev(String nev) {
        this.nev = nev;
    }

    public void setAr(double ar) {
        this.ar = ar;
    }

    public void setRaktaronLevoMennyiseg(int raktaronLevoMennyiseg) {
        this.raktaronLevoMennyiseg = raktaronLevoMennyiseg;
    }

    public void setUzletId(String uzletId) {
        this.uzletId = uzletId;
    }

    public void setTermekSulya(double termekSulya) {
        this.termekSulya = termekSulya;
    }

    public void setTermekKepe(String termekKepe) {
        this.termekKepe = termekKepe;
    }

    private  String termekKepe;

    public Termek() {}

    public Termek(String nev, double ar, int raktaronLevoMennyiseg, double termekSulya, String termekKepe, String uzletId) {
        this.nev = nev;
        this.ar = ar;
        this.raktaronLevoMennyiseg = raktaronLevoMennyiseg;
        this.termekSulya = termekSulya;
        this.termekKepe = termekKepe;
        this.uzletId = uzletId;
    }

    private final Map<String, Object> termekMap = new HashMap<>();

    @Override
    public Map<String, Object> ujTermek(Termek termek) {
        termekMap.put("termekNeve", termek.getNev());
        termekMap.put("termekAra", termek.getAr());
        termekMap.put("raktaronLevoMennyiseg", termek.getRaktaronLevoMennyiseg());
        termekMap.put("termekSulya", termek.getTermekSulya());
        termekMap.put("termekKepe", termek.getTermekKepe());
        termekMap.put("uzletId", termek.getUzletId());
        return termekMap;
    }
}
