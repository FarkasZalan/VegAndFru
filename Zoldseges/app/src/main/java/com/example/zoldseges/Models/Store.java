package com.example.zoldseges.Models;

import java.util.Random;

public class Store {
    // Store name
    private String storeName;

    // Store address
    private String businessAddress;

    // Image representing the store
    private String storeImage;

    // Unique identifier for the store
    private String storeId;

    // Unique identifier for the store owner
    private String ownerId;

    // Constructor to initialize Store object
    public Store(String storeName, String businessAddress, String storeImage, String ownerId) {
        this.storeName = storeName;
        this.businessAddress = businessAddress;
        this.storeImage = storeImage;
        this.ownerId = ownerId;
    }

    // Default constructor
    public Store() {
    }

    // Getters and Setters

    public String getStoreId() {
        return storeId;
    }
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getShippingCost() {
        // Placeholder cost. To be updated based on distance and other factors
        return "5000Ft szállítási díj";
    }
    public void setShippingCost(String shippingCost) {
    }

    public String getDeliveryDuration() {
        Random random = new Random();
        // Generate a random delivery time between 1 and 4 days
        int start = random.nextInt(4 - 1) + 1;
        int end = random.nextInt((start + 4) - (start + 1)) + start + 1;
        return "3 - 5 munkanap";  // Example duration
    }
    public void setDeliveryDuration(String deliveryDuration) {
        // Shipping cost for deliveries
    }

    public String getStoreName() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }
    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getStoreImage() {
        return storeImage;
    }
    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }




}
