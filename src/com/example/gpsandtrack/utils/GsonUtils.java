package com.example.gpsandtrack.utils;

import com.google.gson.Gson;

public class GsonUtils {

    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;

    }
}
