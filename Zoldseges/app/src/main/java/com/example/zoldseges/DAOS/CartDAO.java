package com.example.zoldseges.DAOS;

public interface CartDAO {
    // Called when an item is edited in the cart
    boolean onEdit(int position, double newQuantity);

    // Called when an item is deleted from the cart
    void onDelete(int position);

    // Called when a product is selected in the cart
    void onProductSelect(int position);

    // Called when proceeding to payment
    void onProceedToPayment();
}
