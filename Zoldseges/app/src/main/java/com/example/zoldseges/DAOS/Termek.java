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

    private final String nev;
    private final double ar;
    private final int raktaronLevoMennyiseg;
    private final String uzletId;

    public double getTermekSulya() {
        return termekSulya;
    }

    private final double termekSulya;
    private final String termekKepe;

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
