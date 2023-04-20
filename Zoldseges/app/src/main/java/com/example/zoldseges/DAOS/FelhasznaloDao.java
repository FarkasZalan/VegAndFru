package com.example.zoldseges.DAOS;

import com.example.zoldseges.DAOS.Felhasznalo;

import java.util.Map;

public interface FelhasznaloDao {
    Map<String, Object> ujFelhasznalo(Felhasznalo felhasznalo);

}
