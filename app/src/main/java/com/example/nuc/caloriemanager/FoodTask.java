package com.example.nuc.caloriemanager;

/**
 * Created by NUC on 2017/11/4.
 */


import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by zf on 2017/10/30.
 */


public class FoodTask {

    private Bitmap image;
    private String token;

    public FoodTask(String token, Bitmap image) throws IllegalArgumentException {
        if (token == null || image == null) {
            throw new IllegalArgumentException("Both parameters required");
        }
        this.token = token;
        this.image = image;
    }

    public String getToken() {
        return this.token;
    }

    public Bitmap getImage() {
        return this.image;
    }
}