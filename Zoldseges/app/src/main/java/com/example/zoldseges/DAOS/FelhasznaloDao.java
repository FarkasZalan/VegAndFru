package com.example.zoldseges.DAOS;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public interface FelhasznaloDao {
    Map<String, Object> ujFelhasznalo(Felhasznalo felhasznalo);

}
