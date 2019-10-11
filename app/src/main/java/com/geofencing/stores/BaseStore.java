package com.geofencing.stores;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class BaseStore {
    private final SharedPreferences sharedPreferences;
    protected final Context context;

    public BaseStore(String sharedPrefName, Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
    }

    public void clearAll(){
        sharedPreferences.edit().clear().apply();
    }

    protected void savePair(String key, Boolean value){
        if (sharedPreferences != null){
            sharedPreferences.edit().putBoolean(key, value).apply();
        }
    }

    protected  void savePair(String key, String value){
        if (sharedPreferences != null){
            sharedPreferences.edit().putString(key, value).apply();
        }
    }

    protected Boolean getBoolean(String key, Boolean defaultValue){
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    protected String getString(String key, String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }

}


