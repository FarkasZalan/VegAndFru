package com.example.zoldseges.DAOS;

import com.example.zoldseges.DAOS.Termek;

import java.util.Map;

public interface TermekDao {

    Map<String, Object> ujTermek(Termek termek);
}
