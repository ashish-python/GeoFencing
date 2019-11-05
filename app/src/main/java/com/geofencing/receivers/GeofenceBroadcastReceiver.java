package com.geofencing.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import androidx.room.Room;

import com.geofencing.constants.Constants;
import com.geofencing.database.EventsDatabase;
import com.geofencing.database.GeofenceEventEntity;
import com.geofencing.utils.PostGeofenceEventData;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
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
        eventsDatabase = Room.databaseBuilder(context, EventsDatabase.class, Constants.GEOFENCE_EVENT_ENTITY).fallbackToDestructiveMigration().build();
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            //for each Geofence event, save the data in the local db
            for (Geofence geofence : triggeringGeofences) {
                Log.v("DATA_INSERTED", "DATA INSERTED");
                String requestId = geofence.getRequestId();
                Location location = geofencingEvent.getTriggeringLocation();
                String user_id = "user_id";
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double accuracy = location.getAccuracy();
                float speed = location.getSpeed();
                double altitude = location.getAltitude();
                float bearing = location.getBearing();
                long timestamp = location.getTime();
                int id = 0;
                GeofenceEventEntity geofenceEventEntity = new GeofenceEventEntity();
                geofenceEventEntity.setId(id);
                geofenceEventEntity.setUserId(user_id);
                geofenceEventEntity.setGeofenceId(requestId);
                geofenceEventEntity.setLatitude(latitude);
                geofenceEventEntity.setLongitude(longitude);
                geofenceEventEntity.setAccuracy(accuracy);
                geofenceEventEntity.setBearing(bearing);
                geofenceEventEntity.setAltitude(altitude);
                geofenceEventEntity.setSpeed(speed);
                geofenceEventEntity.setTimestamp(timestamp);
                Log.v("DATA_INSERTED", "Trying to enter data");
                //eventsDatabase.getEventsDao().addEvent(geofenceEventEntity);
                //send push notification to the parents
                new PostGeofenceEventData(requestId).execute("", "");

            }
        }
    }
}
