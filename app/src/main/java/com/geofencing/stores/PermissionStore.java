package com.geofencing.stores;

import android.content.Context;

public class PermissionStore extends BaseStore {
    private static String SHARED_PREFS = "permission";
    private static String LOCATION_PERMISSION = "location";
    private Context context;
    private static PermissionStore instance = null;

    private PermissionStore(Context context){
        super(SHARED_PREFS, context);
        this.context = context;
    }

    //Singleton instance
    public static PermissionStore getInstance(Context context){
        if (instance == null){
            instance = new PermissionStore(context);
        }
        return instance;
    }

    public Boolean getLocationPermission(){
        return getBoolean(LOCATION_PERMISSION, null);
    }

    public void setLocationPermission(Boolean value){
        savePair(LOCATION_PERMISSION, value);
    }
}
