
package com.geofencing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geofencing.interfaces.GeofenceInterface;
import com.geofencing.stores.PermissionStore;
import com.geofencing.utils.NetworkGetRequest;
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

public class MainActivity extends AppCompatActivity implements GeofenceInterface {

    private List<Geofence> geofenceList = new LinkedList<>();
    private PendingIntent geofencePendingIntent;
    private static int LOCATION_PERMISSION_CODE = 123;
    private TextView textViewMessage;
    private Button buttonLocationPermission;
    private static final String GEOFENCES_LIST = "geofence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListeners();
        checkLocationPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initViews() {
        textViewMessage = findViewById(R.id.textview_message);
        buttonLocationPermission = findViewById(R.id.button_permission);
    }

    private void setListeners() {
        buttonLocationPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
            }
        });
    }

    //Check if the user has allowed location tracking
    private void checkLocationPermission() {
        //location permission granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            syncGeofences();
            return;
        }
        //location permission never asked
        PermissionStore.getInstance(getApplicationContext()).getLocationPermission();

        /*
        if (PermissionStore.getInstance(this).getLocationPermission() == null){
            requestLocationPermission();
        }
        //location permission denied before
        else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            textViewMessage.setText(R.string.location_request_message);
            buttonLocationPermission.setVisibility(View.VISIBLE);
            PermissionStore.getInstance(this).setLocationPermission(false);
        }
        else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            textViewMessage.setText(R.string.location_do_not_show_again);
            buttonLocationPermission.setVisibility(View.INVISIBLE);
        }

         */
    }

    //Open location permission dialog
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
    }

    //This method is called when the user denies or accepts location tracking
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                textViewMessage.setText(R.string.welcome_message);
                buttonLocationPermission.setVisibility(View.GONE);
                syncGeofences();
            } else {
                textViewMessage.setText(R.string.location_request_message);
                buttonLocationPermission.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //This method gets the list of geofences for the user from the database
    private void syncGeofences() {
        new NetworkGetRequest(this::addGeofences, GEOFENCES_LIST).execute("", "");
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        //INITIAL_TRIGGER_ENTER tells the Location services that GEOFENCE_TRANSITION_ENTER should be triggered if the device is already inside the geofence
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;

    }

    /**
     * This is a callback method
     * When the list of Geofences is updated on the server we update the geofences on the phone
     */
    @Override
    public void addGeofences(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray array = jsonObject.getJSONArray("geofences");
        String timestamp = jsonObject.getString("timestamp");
        for(int i = 0; i < array.length(); i++){
            JSONObject obj = array.getJSONObject(i);
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(obj.getString("geofence_id"))
                    .setCircularRegion(obj.getDouble("latitude"), obj.getDouble("longitude"), obj.getInt("radius"))
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(this);
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Geofences added", Toast.LENGTH_SHORT).show();
                        textViewMessage.setText(R.string.welcome_message);
                        buttonLocationPermission.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textViewMessage.setText(R.string.geofence_set_failure);
                        Toast.makeText(MainActivity.this, "Geofences FAILURE: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


