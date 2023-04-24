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

    public String getRendeloId() {
        return rendeloId;
    }

    private String rendeloId;


    private final Map<String, String> nyugtaMap = new HashMap<>();


    public Nyugta(String nyugtaId, String vegosszeg, String datum, String uzletId, String termkek, String rendeloId) {
        this.nyugtaId = nyugtaId;
        this.vegosszeg = vegosszeg;
        this.datum = datum;
        this.uzletId = uzletId;
        this.termkek = termkek;
        this.rendeloId = rendeloId;
    }

    public Nyugta(){}

    public Map<String, String> ujNyugta(Nyugta nyugta) {
        nyugtaMap.put("rendeloId", nyugta.getRendeloId());
        nyugtaMap.put("uzletId", nyugta.getUzletId());
        nyugtaMap.put("termkek", nyugta.getTermkek());
        nyugtaMap.put("vegosszeg", nyugta.getVegosszeg());
        nyugtaMap.put("idopont", nyugta.getDatum());
        nyugtaMap.put("nyugtaId", nyugta.getNyugtaId());
        return nyugtaMap;
    }
}
