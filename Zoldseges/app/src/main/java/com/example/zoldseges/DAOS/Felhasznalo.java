package com.example.zoldseges.DAOS;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.internal.RecaptchaActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Felhasznalo implements FelhasznaloDao {
    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJelszo() {
        return jelszo;
    }

    public void setJelszo(String jelszo) {
        this.jelszo = jelszo;
    }

    public String getTelefonszam() {
        return telefonszam;
    }

    public void setTelefonszam(String telefonszam) {
        this.telefonszam = telefonszam;
    }

    public String getLakcim() {
        return lakcim;
    }

    public void setLakcim(String lakcim) {
        this.lakcim = lakcim;
    }

    public String getCegNev() {
        return cegNev;
    }

    public void setCegNev(String cegNev) {
        this.cegNev = cegNev;
    }

    public String getAdoszam() {
        return adoszam;
    }

    public void setAdoszam(String adoszam) {
        this.adoszam = adoszam;
    }

    private String nev;
    private String email;
    private String jelszo;
    private String telefonszam;
    private String lakcim;

    private String cegNev;
    private String adoszam;

    public String getFelhasznaloTipus() {
        return felhasznaloTipus;
    }

    public void setFelhasznaloTipus(String felhasznaloTipus) {
        this.felhasznaloTipus = felhasznaloTipus;
    }

    public String felhasznaloTipus;

    public String getSzekhely() {
        return szekhely;
    }

    public void setSzekhely(String szekhely) {
        this.szekhely = szekhely;
    }

    private String szekhely;

    public String getBoltKepe() {
        return boltKepe;
    }

    public void setBoltKepe(String boltKepe) {
        this.boltKepe = boltKepe;
    }

    private String boltKepe;

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

    private Map<String, Object> felhasznalok = new HashMap<>();

    @Override
    public Map<String, Object> ujFelhasznalo(Felhasznalo felhasznalo) {
        felhasznalok.put("nev", felhasznalo.getNev());
        felhasznalok.put("email", felhasznalo.getEmail());
        felhasznalok.put("telefonszam", felhasznalo.getTelefonszam());
        felhasznalok.put("lakcim", felhasznalo.getLakcim());
        felhasznalok.put("cegNev", felhasznalo.getCegNev());
        felhasznalok.put("adoszam", felhasznalo.getAdoszam());
        felhasznalok.put("szekhely", felhasznalo.getSzekhely());
        felhasznalok.put("felhasznaloTipus", felhasznalo.getFelhasznaloTipus());
        felhasznalok.put("uzletId", felhasznalo.getBoltKepe());
        return felhasznalok;
    }

}
