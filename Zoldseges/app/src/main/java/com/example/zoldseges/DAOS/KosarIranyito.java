package com.example.zoldseges.DAOS;

import java.util.ArrayList;

public interface KosarIranyito {
    boolean onSzerkesztes(int position, double ujMEnnyiseg);

    void onTorles(int position);

    void onTermek(int position);

    void onFizeteshez();
}
