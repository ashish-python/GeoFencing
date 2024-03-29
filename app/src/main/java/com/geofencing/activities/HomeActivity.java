package com.geofencing.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.geofencing.R;
import com.geofencing.constants.Endpoints;
import com.geofencing.listeners.BaseListener;
import com.geofencing.stores.PermissionStore;
import com.geofencing.stores.TokenStore;
import com.geofencing.sync.GeofenceSyncer;
import com.geofencing.utils.NetworkPostRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.LinkedList;
import java.util.List;

public class HomeActivity extends BaseAppCompatActivity implements BaseListener {
    private List<Geofence> geofenceList = new LinkedList<>();
    private PendingIntent geofencePendingIntent;
    private static int LOCATION_PERMISSION_CODE = 123;
    private TextView textViewMessage;
    private Button buttonLocationPermission;
    private static final String GEOFENCES_LIST = "geofence";
    private TextView signOutTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        setListeners();
        //syncGeofences();
    }

    private void initViews() {
        textViewMessage = findViewById(R.id.textview_message);
        buttonLocationPermission = findViewById(R.id.button_permission);
        signOutTV = findViewById(R.id.sign_out_tv);
    }

    private void setListeners() {
        buttonLocationPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLocationPermission();
            }
        });

        signOutTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TokenStore.getInstance(getApplicationContext()).setUser("");
                startActivity(HomeActivity.this, MainActivity.class, FINISH_CURRENT_ACTIVITY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permission = checkLocationPermission();
        if (permission) {
            showSuccessMessage();
            setFCMToken();
            syncGeofences();
        }
    }
    //region LocationPermission
    //Check if the user has allowed location tracking
    private boolean checkLocationPermission() {
        //location permission granted
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (!PermissionStore.getInstance(this).getLocationPermissionAskedOnce()) {
            textViewMessage.setText(R.string.location_request_message);
            buttonLocationPermission.setVisibility(View.VISIBLE);
            PermissionStore.getInstance(this).setLocationPermissionAskedOnce(true);
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            textViewMessage.setText(R.string.location_request_message);
            buttonLocationPermission.setVisibility(View.VISIBLE);
        } else {
            textViewMessage.setText(R.string.location_do_not_show_again);
            buttonLocationPermission.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Button should go", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Open location permission dialogF
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
                //if the user has clicked on Don't Ask Again
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    textViewMessage.setText(R.string.location_do_not_show_again);
                    buttonLocationPermission.setVisibility(View.INVISIBLE);
                } else {
                    textViewMessage.setText(R.string.location_request_message);
                    buttonLocationPermission.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }
    //endregion

    //Network request to get the list of geofences for the user from the backend
    private void syncGeofences() {
        GeofenceSyncer.getInstance(this).syncGeofences();
    }

    private void showSuccessMessage() {
        textViewMessage.setText(R.string.welcome_message);
        buttonLocationPermission.setVisibility(View.GONE);
        textViewMessage.setVisibility(View.VISIBLE);
        //showGeofenceData();
    }

    private void setFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM_TOKEN: ", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        TokenStore tokenStore = TokenStore.getInstance(getApplicationContext());
                        if (!token.equals(tokenStore.getFCMToken())) {
                            tokenStore.setFCMToken(token);
                            childUpdateFCMToken(token);
                        }
                        Log.v("FCM_TOKEN_CHILD: ", token);
                        //childUpdateFCMToken(token);
                    }
                });
    }

    private void childUpdateFCMToken(String childFCMToken) {
        new NetworkPostRequest(this, Endpoints.CHILD_FCM_TOKEN_UPDATE_URL, this::callback, Endpoints.CHILD_UPDATE_FCM_TOKEN_TASK).execute(TokenStore.getInstance(getApplicationContext()).getUser(), childFCMToken);
    }

    @Override
    public void callback(Context context, Integer status, String responseString) {
        Log.v("FCM_RETURN", responseString);
    }
}
