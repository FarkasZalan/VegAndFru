package com.example.zoldseges.DAOS;

public interface ReceiptDAO {

    // Method triggered when a receipt is generated at the given position
    void onReceiptSelect(int position);
}
