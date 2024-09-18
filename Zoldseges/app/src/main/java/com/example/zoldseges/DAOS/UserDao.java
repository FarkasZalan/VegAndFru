package com.example.zoldseges.DAOS;

import com.example.zoldseges.Models.User;

import java.util.Map;

public interface UserDao {

    // Adds a new user and returns a map containing relevant user data or status
    Map<String, Object> addUser(User user);

}
