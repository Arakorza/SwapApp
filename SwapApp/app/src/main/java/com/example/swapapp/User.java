package com.example.swapapp;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String email, username, bio, contactInfo;
    public double rating;
    public int numOfTrades, numOfRatings;

    public User() { }

    public User(String email, String username) {
        this.email = email;
        this.username = username;
        bio = "";
        contactInfo = "";
        rating = 5;
        numOfTrades = 0;
        numOfRatings = 0;
    }
}


