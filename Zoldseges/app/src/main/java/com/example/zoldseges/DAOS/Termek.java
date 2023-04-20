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

    public double getRaktaronLevoMennyiseg() {
        return raktaronLevoMennyiseg;
    }

    public String getUzletId() {
        return uzletId;
    }

    public String getTermekKepe() {
        return termekKepe;
    }

    private String nev;
    private double ar;
    private double raktaronLevoMennyiseg;
    private String uzletId;

    public String getOsszTermekColectionId() {
        return osszTermekColectionId;
    }

    public void setOsszTermekColectionId(String osszTermekColectionId) {
        this.osszTermekColectionId = osszTermekColectionId;
    }

    private String osszTermekColectionId;

    public String getSajatId() {
        return sajatId;
    }

    public void setSajatId(String sajatId) {
        this.sajatId = sajatId;
    }

    private String sajatId;

    public double getTermekSulya() {
        return termekSulya;
    }

    private double termekSulya;

    public void setNev(String nev) {
        this.nev = nev;
    }

    public void setAr(double ar) {
        this.ar = ar;
    }

    public void setRaktaronLevoMennyiseg(double raktaronLevoMennyiseg) {
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

    private String termekKepe;

    public Termek() {
    }

    public Termek(String nev, double ar, double raktaronLevoMennyiseg, double termekSulya, String termekKepe, String uzletId, String osszTermekColectionId) {
        this.nev = nev;
        this.ar = ar;
        this.raktaronLevoMennyiseg = raktaronLevoMennyiseg;
        this.termekSulya = termekSulya;
        this.termekKepe = termekKepe;
        this.uzletId = uzletId;
        this.osszTermekColectionId = osszTermekColectionId;
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
        termekMap.put("osszTermekCollection", termek.getOsszTermekColectionId());
        return termekMap;
    }
}
