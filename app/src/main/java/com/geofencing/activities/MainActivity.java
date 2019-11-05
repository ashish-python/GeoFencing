
package com.geofencing.activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geofencing.R;
import com.geofencing.stores.TokenStore;

public class MainActivity extends BaseAppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TokenStore.getInstance(getApplicationContext()).getUser().equals("")) {
            startActivity(MainActivity.this, SignInActivity.class, FINISH_NO_ACTIVITY);
        }
        else{
            startActivity(MainActivity.this, HomeActivity.class, FINISH_NO_ACTIVITY);
        }
    }

}


