package com.example.zoldseges.Models;

public class CartItem {
    // The product that this cart item refers to
    private Product product;

    // The quantity of the product in the cart
    private double quantity;

    // Constructor that initializes the product and quantity
    public CartItem(Product product, Double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Default constructor
    public CartItem() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
