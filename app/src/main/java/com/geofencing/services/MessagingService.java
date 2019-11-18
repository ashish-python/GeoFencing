package com.geofencing.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.geofencing.constants.Constants;
import com.geofencing.listeners.BaseListener;
import com.geofencing.stores.TokenStore;
import com.geofencing.utils.NetworkPostRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService implements BaseListener {
    public static final String FCM_TOKEN = "fcm_token";
    private static boolean SEND_LOCATION = false;
    private static LocationListener locationListener;
    private LocationManager locationManager;
    private Handler mainHandler = new Handler();
    private volatile static boolean sendLocationData = false;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.v("ACTION_PUSH_RECEIVED", remoteMessage.getNotification().getBody());
        if (remoteMessage.getNotification().getBody().equals("send location")) {
            sendLocationData = true;
            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (sendLocationData) {
                        Log.v("ACTION_", "YYYY");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            */
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    trackLocation();
                }
            });
        } else {
            sendLocationData = false;
        }
    }

    //this method sends location information to the server
    //the information will be retrieved by the parent app to display the child's movement
    private void trackLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!sendLocationData){
                    locationManager.removeUpdates(this);
                    locationManager = null;
                }
                else {
                    try{
                        Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        Log.v("ACTION_LATITUDE: ", String.valueOf(location.getLatitude()));
                        Log.v("ACTION_LONGITUDE: ", String.valueOf(location.getLongitude()));
                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());
                        new NetworkPostRequest(MessagingService.this, Constants.LAST_KNOWN_LOCATION_URL, MessagingService.this::callback, Constants.SAVE_LAST_KNOWN_LOCATION_TASK).execute(TokenStore.getInstance(getApplicationContext()).getUser(), TokenStore.getInstance(getApplicationContext()).getFCMToken(), latitude, longitude);

                    }
                    catch (SecurityException e){
                        locationManager.removeUpdates(this);
                        locationManager = null;
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("GEO_TRIGGER_TOKEN", token);
        //send the token in Shared Preferences
    }

    @Override
    public void callback(Context context, Integer status, String responseString) {
        Log.v("ACTION_LOC_SAVED", responseString);
    }
}
