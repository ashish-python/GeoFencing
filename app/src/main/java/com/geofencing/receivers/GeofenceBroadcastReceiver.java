package com.geofencing.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.geofencing.activities.MainActivity;
import com.geofencing.constants.Constants;
import com.geofencing.database.EventsDatabase;
import com.geofencing.database.GeofenceEventEntity;
import com.geofencing.listeners.BaseListener;
import com.geofencing.stores.TokenStore;
import com.geofencing.utils.NetworkPostRequest;
import com.geofencing.utils.PostGeofenceEventData;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.UUID;

public class GeofenceBroadcastReceiver extends BroadcastReceiver implements BaseListener {
    private static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    private static EventsDatabase eventsDatabase;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            //for each Geofence event, save the data in the local db
            for (Geofence geofence : triggeringGeofences) {
                saveGeofenceEventInDB(context, geofencingEvent, geofence);
                //
                //send push notification to the parents
                sendPushNotificationToParent(context, geofencingEvent, geofence);
            }
        }
    }

    private void sendPushNotificationToParent(Context context, GeofencingEvent geofencingEvent, Geofence geofence) {
        Log.v("FCM_NOTIFICATION", "SENDING NOTIFICATION TO PARENT");
        new NetworkPostRequest(context, Constants.PUSH_NOTIFICATION_TO_PARENT_URL, this::callback, Constants.PUSH_NOTIFICATION_TO_PARENT_TASK).execute(TokenStore.getInstance(context).getUser(), geofence.getRequestId());
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void callback(Context context, Integer status, String json) {
        Log.v("FCM_NOTIFICATION_SENT", json);
    }

        @SuppressLint("StaticFieldLeak")
    private void saveGeofenceEventInDB(Context context, GeofencingEvent geofencingEvent, Geofence geofence) {
        new AsyncTask<String, Void, Long>() {

            @Override
            protected Long doInBackground(String... strings) {
                Log.v("DATA_INSERTED", "SAVING DATA IN DB");
                String requestId = geofence.getRequestId();
                Location location = geofencingEvent.getTriggeringLocation();
                String user_id = TokenStore.getInstance(context).getUser();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double accuracy = location.getAccuracy();
                float speed = location.getSpeed();
                double altitude = location.getAltitude();
                float bearing = location.getBearing();
                long timestamp = location.getTime();
                GeofenceEventEntity geofenceEventEntity = new GeofenceEventEntity();
                geofenceEventEntity.setId(UUID.randomUUID().toString());
                geofenceEventEntity.setUserId(user_id);
                geofenceEventEntity.setGeofenceId(requestId);
                geofenceEventEntity.setLatitude(latitude);
                geofenceEventEntity.setLongitude(longitude);
                geofenceEventEntity.setAccuracy(accuracy);
                geofenceEventEntity.setBearing(bearing);
                geofenceEventEntity.setAltitude(altitude);
                geofenceEventEntity.setSpeed(speed);
                geofenceEventEntity.setTimestamp(timestamp);
                return EventsDatabase.getInstance(context).getEventsDao().addEvent(geofenceEventEntity);
            }

            @Override
            protected void onPostExecute(Long i) {
                super.onPostExecute(i);
                Log.v("DATA_INSERTED: ", String.valueOf(i));
            }
        }.execute("");
    }
}
