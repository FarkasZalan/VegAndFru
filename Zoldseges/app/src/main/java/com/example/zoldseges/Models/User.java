package com.example.zoldseges.Models;

import com.example.zoldseges.DAOS.UserDao;

import java.util.HashMap;
import java.util.Map;

public class User implements UserDao {
    // User's full name
    private final String fullName;

    // User's email address
    private final String emailAddress;

    // User's phone number
    private final String phoneNumber;

    // User's home address
    private final String homeAddress;

    // User's company name (if applicable)
    private final String companyName;

    // User's tax ID number
    private final String taxNumber;

    // User type (Private customer, Corporate Customer, Seller (which must always be a company))
    public String userType;

    // User's business address
    private final String businessAddress;

    // Store image or logo (if applicable)
    private final String storeImage;

    // Constructor to initialize User object
    public User(String fullName, String emailAddress, String phoneNumber, String homeAddress, String companyName, String taxNumber, String businessAddress, String userType, String storeImage) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.homeAddress = homeAddress;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.businessAddress = businessAddress;
        this.userType = userType;
        this.storeImage = storeImage;
    }

    // Getters
    public String getFullName() {
        return fullName;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getHomeAddress() {
        return homeAddress;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getTaxNumber() {
        return taxNumber;
    }
    public String getUserType() {
        return userType;
    }
    public String getBusinessAddress() {
        return businessAddress;
    }
    public String getStoreImage() {
        return storeImage;
    }


    // Map to hold user details for storage or processing
    private final Map<String, Object> userMap = new HashMap<>();

    @Override
    public Map<String, Object> addUser(User user) {
        // Populate the map with user details
        userMap.put("nev", user.getFullName());
        userMap.put("email", user.getEmailAddress());
        userMap.put("telefonszam", user.getPhoneNumber());
        userMap.put("lakcim", user.getHomeAddress());
        userMap.put("cegNev", user.getCompanyName());
        userMap.put("adoszam", user.getTaxNumber());
        userMap.put("szekhely", user.getBusinessAddress());
        userMap.put("felhasznaloTipus", user.getUserType());
        userMap.put("uzletId", user.getStoreImage());
        return userMap;
    }

}
