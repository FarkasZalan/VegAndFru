package com.example.zoldseges.DAOS;

public class Bolt {
    private String nev;

    public Bolt(String nev, String tulajNeve, String tulajEmailCime, String adoszam, String telephely, String telefonszam, Termek[] termekek) {
        this.nev = nev;
        this.tulajNeve = tulajNeve;
        this.tulajEmailCime = tulajEmailCime;
        this.adoszam = adoszam;
        this.telephely = telephely;
        this.telefonszam = telefonszam;
        this.termekek = termekek;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getTulajNeve() {
        return tulajNeve;
    }

    public void setTulajNeve(String tulajNeve) {
        this.tulajNeve = tulajNeve;
    }

    public String getTulajEmailCime() {
        return tulajEmailCime;
    }

    public void setTulajEmailCime(String tulajEmailCime) {
        this.tulajEmailCime = tulajEmailCime;
    }

    public String getAdoszam() {
        return adoszam;
    }

    public void setAdoszam(String adoszam) {
        this.adoszam = adoszam;
    }

    public String getTelephely() {
        return telephely;
    }

    public void setTelephely(String telephely) {
        this.telephely = telephely;
    }

    public String getTelefonszam() {
        return telefonszam;
    }

    public void setTelefonszam(String telefonszam) {
        this.telefonszam = telefonszam;
    }

    public Termek[] getTermekek() {
        return termekek;
    }

    public void setTermekek(Termek[] termekek) {
        this.termekek = termekek;
    }

    private String tulajNeve;
    private String tulajEmailCime;
    private String adoszam;
    private String telephely;
    private String telefonszam;
    private Termek[] termekek;
}
