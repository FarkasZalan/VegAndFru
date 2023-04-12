package com.example.zoldseges.DAOS;

import java.util.HashMap;
import java.util.Map;

public class Felhasznalo implements FelhasznaloDao {
    public String getNev() {
        return nev;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefonszam() {
        return telefonszam;
    }

    public String getLakcim() {
        return lakcim;
    }

    public String getCegNev() {
        return cegNev;
    }


    public String getAdoszam() {
        return adoszam;
    }


    private final String nev;
    private final String email;
    private final String telefonszam;
    private final String lakcim;

    private final String cegNev;
    private final String adoszam;

    public String getFelhasznaloTipus() {
        return felhasznaloTipus;
    }

    public String felhasznaloTipus;

    public String getSzekhely() {
        return szekhely;
    }


    private final String szekhely;

    public String getBoltKepe() {
        return boltKepe;
    }

    private final String boltKepe;

    public Felhasznalo(String nev, String email, String telefonszam, String lakcim, String cegNev, String adoszam, String szekhely, String felhasznaloTipus, String boltKepe) {
        this.nev = nev;
        this.email = email;
        this.telefonszam = telefonszam;
        this.lakcim = lakcim;
        this.cegNev = cegNev;
        this.adoszam = adoszam;
        this.szekhely = szekhely;
        this.felhasznaloTipus = felhasznaloTipus;
        this.boltKepe = boltKepe;
    }

    private final Map<String, Object> felhasznaloMAp = new HashMap<>();

    @Override
    public Map<String, Object> ujFelhasznalo(Felhasznalo felhasznalo) {
        felhasznaloMAp.put("nev", felhasznalo.getNev());
        felhasznaloMAp.put("email", felhasznalo.getEmail());
        felhasznaloMAp.put("telefonszam", felhasznalo.getTelefonszam());
        felhasznaloMAp.put("lakcim", felhasznalo.getLakcim());
        felhasznaloMAp.put("cegNev", felhasznalo.getCegNev());
        felhasznaloMAp.put("adoszam", felhasznalo.getAdoszam());
        felhasznaloMAp.put("szekhely", felhasznalo.getSzekhely());
        felhasznaloMAp.put("felhasznaloTipus", felhasznalo.getFelhasznaloTipus());
        felhasznaloMAp.put("uzletId", felhasznalo.getBoltKepe());
        return felhasznaloMAp;
    }

}
