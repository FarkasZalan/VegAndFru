package com.example.zoldseges.DAOS;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Uzlet {
    private String uzletNeve;
    private String szekhely;
    private String boltKepe;

    private String szallitasiDij;

    public String getSzallitasiDij() {
        //ha lesz idő akkor távolság alapú, 10 km + 500Dt
        return "5000Ft szállítási díj";
    }

    public void setSzallitasiDij(String szallitasiDij) {
        this.szallitasiDij = szallitasiDij;
    }

    public String getSzallitasIdotartama() {
        Random r = new Random();
        int kezdo = r.nextInt(4 - 1) + 1;
        int vegzo = r.nextInt((kezdo + 4) - (kezdo + 1)) + kezdo + 1;
        return "3 - 5 munkanap";
    }

    public void setSzallitasIdotartama(String szallitasIdotartama) {
        this.szallitasIdotartama = szallitasIdotartama;
    }

    private String szallitasIdotartama;

    public String getUzletNeve() {
        return uzletNeve;
    }

    public void setUzletNeve(String uzletNeve) {
        this.uzletNeve = uzletNeve;
    }

    public String getSzekhely() {
        return szekhely;
    }

    public void setSzekhely(String szekhely) {
        this.szekhely = szekhely;
    }

    public String getBoltKepe() {
        return boltKepe;
    }

    public void setBoltKepe(String boltKepe) {
        this.boltKepe = boltKepe;
    }

    public String getTulajId() {
        return tulajId;
    }

    public void setTulajId(String tulajId) {
        this.tulajId = tulajId;
    }

    private String tulajId;

    public Uzlet(String uzletNeve, String szekhely, String boltKepe, String tulajId) {
        this.uzletNeve = uzletNeve;
        this.szekhely = szekhely;
        this.boltKepe = boltKepe;
        this.tulajId = tulajId;
    }

    public Uzlet() {
    }
}
