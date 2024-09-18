package com.example.zoldseges.Models;

import com.example.zoldseges.DAOS.ProductDAO;

import java.util.HashMap;
import java.util.Map;

public class Product implements ProductDAO {
    // Private fields for the Product attributes
    private String productName;
    private double price;
    private double availableStockQuantity;
    private String storeId;
    private String allProductCollectionId;
    private String productId;
    private double productWeight;
    private String productImage;

    // Constructor with parameters to initialize a Product object
    public Product(String productName, double price, double availableStockQuantity, double productWeight, String productImage, String storeId, String allProductCollectionId) {
        this.productName = productName;
        this.price = price;
        this.availableStockQuantity = availableStockQuantity;
        this.productWeight = productWeight;
        this.productImage = productImage;
        this.storeId = storeId;
        this.allProductCollectionId = allProductCollectionId;
    }

    // Default constructor
    public Product() {}

    // Getters and Setters
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public double getAvailableStockQuantity() {
        return availableStockQuantity;
    }
    public void setAvailableStockQuantity(double availableStockQuantity) {
        this.availableStockQuantity = availableStockQuantity;
    }

    public String getStoreId() {
        return storeId;
    }
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }


    public String getProductImage() {
        return productImage;
    }
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }


    public String getAllProductCollectionId() {
        return allProductCollectionId;
    }
    public void setAllProductCollectionId(String allProductCollectionId) {
        this.allProductCollectionId = allProductCollectionId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public double getProductWeight() {
        return productWeight;
    }
    public void setProductWeight(double productWeight) {
        this.productWeight = productWeight;
    }

    // Map to store product details
    private final Map<String, Object> productMap = new HashMap<>();

    // Implementation of the addNewProduct method from the ProductDAO interface
    @Override
    public Map<String, Object> addNewProduct(Product product) {
        productMap.put("termekNeve", product.getProductName());
        productMap.put("termekAra", product.getPrice());
        productMap.put("raktaronLevoMennyiseg", product.getAvailableStockQuantity());
        productMap.put("termekSulya", product.getProductWeight());
        productMap.put("termekKepe", product.getProductImage());
        productMap.put("uzletId", product.getStoreId());
        productMap.put("osszTermekCollection", product.getAllProductCollectionId());
        return productMap;
    }
}
