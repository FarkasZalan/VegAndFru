package com.example.zoldseges.Models;

import java.util.HashMap;
import java.util.Map;

public class Receipt {

    // Private fields for receipt details
    private String receiptId;
    private String totalAmount;
    private String orderDate;
    private String storeId;
    private String items;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;
    private String taxNumber;
    private String businessAddress;
    private String storeEmail;
    private String storePhone;
    private String storeName;
    private String customerId;
    private String storeBusinessAddress;
    private String storeImage;

    // Constructor to initialize Receipt object
    public Receipt(String receiptId, String totalAmount, String orderDate, String storeId, String items, String customerId) {
        this.receiptId = receiptId;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.storeId = storeId;
        this.items = items;
        this.customerId = customerId;
    }

    // Default constructor
    public Receipt() {}

    // Getters and Setters
    public String getReceiptId() {
        return receiptId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getItems() {
        return items;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStoreBusinessAddress() {
        return storeBusinessAddress;
    }

    public void setStoreBusinessAddress(String storeBusinessAddress) {
        this.storeBusinessAddress = storeBusinessAddress;
    }

    // Method to create a map of receipt details for processing
    public Map<String, String> createReceiptMap(Receipt receipt) {
        Map<String, String> receiptMap = new HashMap<>();

        receiptMap.put("rendeloId", receipt.getCustomerId());
        receiptMap.put("termekek", receipt.getItems());
        receiptMap.put("vegosszeg", receipt.getTotalAmount());
        receiptMap.put("idopont", receipt.getOrderDate());
        receiptMap.put("nyugtaId", receipt.getReceiptId());
        receiptMap.put("rendeloNev", receipt.getCustomerName());
        receiptMap.put("rendeleoEmail", receipt.getCustomerEmail());
        receiptMap.put("rendeloTelefonszam", receipt.getCustomerPhone());
        receiptMap.put("rendeloSzallitasiCim", receipt.getDeliveryAddress());
        receiptMap.put("rendeloAdoszama", receipt.getTaxNumber());
        receiptMap.put("rendeloSzekhely", receipt.getBusinessAddress());
        receiptMap.put("uzletId", receipt.getStoreId());
        receiptMap.put("boltKepe", receipt.getStoreImage());
        receiptMap.put("uzletNeve", receipt.getStoreName());
        receiptMap.put("uzletEmailCIm", receipt.getStoreEmail());
        receiptMap.put("uzletTelefonszam", receipt.getStorePhone());
        receiptMap.put("uzletErtesitesiCim", receipt.getStoreBusinessAddress());
        return receiptMap;
    }
}
