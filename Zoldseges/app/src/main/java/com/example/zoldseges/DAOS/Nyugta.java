package com.example.zoldseges.DAOS;

import java.util.HashMap;
import java.util.Map;

public class Nyugta {
    public String getNyugtaId() {
        return nyugtaId;
    }

    public String getVegosszeg() {
        return vegosszeg;
    }

    public String getDatum() {
        return datum;
    }

    public String getUzletId() {
        return uzletId;
    }

    public String getTermkek() {
        return termkek;
    }

    private String nyugtaId;
    private String vegosszeg;
    private String datum;
    private String uzletId;
    private String termkek;
    private String nev;
    private String email;

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

    public String getTelefonszam() {
        return telefonszam;
    }

    public void setTelefonszam(String telefonszam) {
        this.telefonszam = telefonszam;
    }

    public String getSzallitasiCim() {
        return szallitasiCim;
    }

    public void setSzallitasiCim(String szallitasiCim) {
        this.szallitasiCim = szallitasiCim;
    }

    public String getAdoszam() {
        return adoszam;
    }

    public void setAdoszam(String adoszam) {
        this.adoszam = adoszam;
    }

    public String getSzekhely() {
        return szekhely;
    }

    public void setSzekhely(String szekhely) {
        this.szekhely = szekhely;
    }

    private String telefonszam;
    private String szallitasiCim;
    private String adoszam;
    private String szekhely;

    public String getUzletEmail() {
        return uzletEmail;
    }

    public void setUzletEmail(String uzletEmail) {
        this.uzletEmail = uzletEmail;
    }

    public String getUzletTelefon() {
        return uzletTelefon;
    }

    public void setUzletTelefon(String uzletTelefon) {
        this.uzletTelefon = uzletTelefon;
    }

    private String uzletEmail;
    private String uzletTelefon;

    public String getUzletNeve() {
        return uzletNeve;
    }

    public void setUzletNeve(String uzletNeve) {
        this.uzletNeve = uzletNeve;
    }

    private String uzletNeve;

    public String getUzletKepe() {
        return uzletKepe;
    }

    public void setUzletKepe(String uzletKepe) {
        this.uzletKepe = uzletKepe;
    }

    private String uzletKepe;

    public String getRendeloId() {
        return rendeloId;
    }

    public void setNyugtaId(String nyugtaId) {
        this.nyugtaId = nyugtaId;
    }

    public void setVegosszeg(String vegosszeg) {
        this.vegosszeg = vegosszeg;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public void setUzletId(String uzletId) {
        this.uzletId = uzletId;
    }

    public void setTermkek(String termkek) {
        this.termkek = termkek;
    }

    public void setRendeloId(String rendeloId) {
        this.rendeloId = rendeloId;
    }

    private String rendeloId;

    public String getUzletSzekhely() {
        return uzletSzekhely;
    }

    public void setUzletSzekhely(String uzletSzekhely) {
        this.uzletSzekhely = uzletSzekhely;
    }

    private String uzletSzekhely;


    private final Map<String, String> nyugtaMap = new HashMap<>();


    public Nyugta(String nyugtaId, String vegosszeg, String datum, String uzletId, String termkek, String rendeloId) {
        this.nyugtaId = nyugtaId;
        this.vegosszeg = vegosszeg;
        this.datum = datum;
        this.uzletId = uzletId;
        this.termkek = termkek;
        this.rendeloId = rendeloId;
    }

    public Nyugta() {
    }

    public Map<String, String> ujNyugta(Nyugta nyugta) {
        nyugtaMap.put("rendeloId", nyugta.getRendeloId());
        nyugtaMap.put("termekek", nyugta.getTermkek());
        nyugtaMap.put("vegosszeg", nyugta.getVegosszeg());
        nyugtaMap.put("idopont", nyugta.getDatum());
        nyugtaMap.put("nyugtaId", nyugta.getNyugtaId());
        nyugtaMap.put("rendeloNev", nyugta.getNev());
        nyugtaMap.put("rendeleoEmail", nyugta.getEmail());
        nyugtaMap.put("rendeloTelefonszam", nyugta.getTelefonszam());
        nyugtaMap.put("rendeloSzallitasiCim", nyugta.getSzallitasiCim());
        nyugtaMap.put("rendeloAdoszama", nyugta.getAdoszam());
        nyugtaMap.put("rendeloSzekhely", nyugta.getSzekhely());
        nyugtaMap.put("uzletId", nyugta.getUzletId());
        nyugtaMap.put("boltKepe", nyugta.getUzletKepe());
        nyugtaMap.put("uzletNeve", nyugta.getUzletNeve());
        nyugtaMap.put("uzletEmailCIm", nyugta.getUzletEmail());
        nyugtaMap.put("uzletTelefonszam", nyugta.getUzletTelefon());
        nyugtaMap.put("uzletErtesitesiCim", nyugta.getUzletSzekhely());
        return nyugtaMap;
    }
}
