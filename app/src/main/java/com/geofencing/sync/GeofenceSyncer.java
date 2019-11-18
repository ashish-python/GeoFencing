package com.geofencing.sync;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.geofencing.activities.MainActivity;
import com.geofencing.constants.Constants;
import com.geofencing.listeners.BaseListener;
import com.geofencing.receivers.GeofenceBroadcastReceiver;
import com.geofencing.stores.TokenStore;
import com.geofencing.utils.NetworkPostRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class GeofenceSyncer implements BaseListener {
    private Context context;
    private static GeofenceSyncer instance;

    private List<Geofence> geofenceList = new LinkedList<>();
    private PendingIntent geofencePendingIntent;
    private static int LOCATION_PERMISSION_CODE = 123;
    private static final String GEOFENCES_LIST = "geofence";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static GeofencingClient geofencingClient;

    private GeofenceSyncer(Context context) {
        this.context = context;
    }

    public static GeofenceSyncer getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        return new GeofenceSyncer(context);
    }

    //Network request to get the list of geofences for the user from the backend
    public void syncGeofences() {
        new NetworkPostRequest(context, Constants.GEOFENCE_URL, this::callback, Constants.GET_GEOFENCES_TASK).execute(TokenStore.getInstance(context).getUser());
    }

    //Geofencing request with the list of all the geofences. This GeofencingRequest will be given to the Geofencing client to add geofences to the user's device
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        //INITIAL_TRIGGER_ENTER tells the Location services that GEOFENCE_TRANSITION_ENTER should be triggered if the device is already inside the geofence
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    //A Pending Intent given to the Geofencing client. i.e. A class that will handle the Geofencing events
    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    /**
     * This is a callback method
     * After we get a list of geofences from the server, we call this method to sync the locations in the user's local db
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    public void callback(Context context, Integer status, String json) {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                try {
                    //We could check if the timestamp since the last update has changed - but for now we remove all the geofences
                    //We should fix this in the next release
                    geofencingClient = LocationServices.getGeofencingClient(context);
                    removeGeofences();
                    //JSONObject jsonObject = new JSONObject(json);
                    //JSONArray array = jsonObject.getJSONArray("geofences");
                    JSONArray array = new JSONArray(json);
                    //EventsDatabase eventsDatabase = Room.databaseBuilder(context, EventsDatabase.class, Constants.GEOFENCE_OBJECT_ENTITY).build();
                    for (int i = 0; i < array.length(); i++) {
                        Log.v("FCM_GEO_NUM", String.valueOf(i + 1));
                        JSONObject obj = array.getJSONObject(i);
                        geofenceList.add(new Geofence.Builder()
                                .setRequestId(obj.getString("geofenceId"))
                                .setCircularRegion(obj.getDouble("lat"), obj.getDouble("lng"), obj.getInt("radius"))
                                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                .build());
                    }

                    geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.v("GEOFENCES_ADDED", "GEOFENCES_ADDED");
                                    Toast.makeText(context, "Geofences added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v("GEOFENCES_ADDED", "GEOFENCES_ADDED FAILED");
                                    Toast.makeText(context, "Geofences FAILURE: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception: " + e);
                }
                return null;
            }
        }.execute();

    }

    private void removeGeofences(){
        geofencingClient.removeGeofences(getGeofencePendingIntent());
        Log.v("FCM_DELETE", "GEOFENCES_DELETED");
    }
}
