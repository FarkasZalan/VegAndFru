package com.example.zoldseges.DAOS;

import com.example.zoldseges.Models.Product;

import java.util.Map;

public interface ProductDAO {

    // Method to add a new product, returning a map of results
    Map<String, Object> addNewProduct(Product product);
}
