package com.geofencing.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.geofencing.constants.Endpoints;
import com.geofencing.database.EventsDatabase;
import com.geofencing.database.GeofenceEventEntity;
import com.geofencing.listeners.BaseListener;
import com.geofencing.stores.TokenStore;
import com.geofencing.sync.EventsSyncer;
import com.geofencing.utils.NetworkPostRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                //send push notification to the parents
                sendPushNotificationToParent(context, geofencingEvent, geofence);
            }
        }
    }

    private void sendPushNotificationToParent(Context context, GeofencingEvent geofencingEvent, Geofence geofence) {
        Log.v("FCM_NOTIFICATION", "SENDING NOTIFICATION TO PARENT");
        new NetworkPostRequest(context, Endpoints.PUSH_NOTIFICATION_TO_PARENT_URL, this::callback, Endpoints.PUSH_NOTIFICATION_TO_PARENT_TASK).execute(TokenStore.getInstance(context).getUser(), geofence.getRequestId());
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
                Log.v("FCM_DATA_INSERTED", "SAVING DATA IN DB");
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
                /**
                 * Once the Geofence event data is saved in local db, try to send it to the server
                 * Delete from local db once saved confirmation is received from the server
                 */
                sendGeofenceEventDataToServer();
                //EventsSyncer.getInstance(context).syncGeofenceEvents();

            }
        }.execute("");
    }

    @SuppressLint("StaticFieldLeak")
    private void sendGeofenceEventDataToServer() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                Log.v("DATA_INSERTED", "SENDING DATA TO SERVER");
                List<GeofenceEventEntity> allEvents = EventsDatabase.getInstance(context).getEventsDao().getAll();
                JSONArray jsonArray = new JSONArray();
                for (GeofenceEventEntity eventEntity : allEvents) {
                    JSONObject jsonObject = new JSONObject();
                    double latitude = eventEntity.getLatitude();
                    double longitude = eventEntity.getLongitude();
                    double accuracy = eventEntity.getAccuracy();
                    float speed = eventEntity.getSpeed();
                    double altitude = eventEntity.getAltitude();
                    float bearing = eventEntity.getBearing();
                    double timestamp = eventEntity.getTimestamp();
                    String geofenceId = eventEntity.getGeofenceId();
                    String user_id = eventEntity.getUserId();
                    String id = eventEntity.getId();
                    try {
                        jsonObject.put("latitude", latitude);
                        jsonObject.put("longitude", longitude);
                        jsonObject.put("accuracy", accuracy);
                        jsonObject.put("speed", speed);
                        jsonObject.put("altitude", altitude);
                        jsonObject.put("bearing", bearing);
                        jsonObject.put("timestamp", timestamp);
                        jsonObject.put("geofenceId", geofenceId);
                        jsonObject.put("childId", user_id);
                        jsonObject.put("id", id);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {

                    }
                }
                return jsonArray.toString();
            }

            @Override
            protected void onPostExecute(String json) {
                super.onPostExecute(json);
                //Send all the events saved in local db to server as JSON
                new NetworkPostRequest(context, Endpoints.SEND_EVENT_DATA_TO_SERVER_URL, GeofenceBroadcastReceiver.this::callback2, Endpoints.SEND_EVENT_DATA_TO_SERVER_TASK).execute(json);
            }
        }.execute("");
    }

    //Delete events from local db once sent to the server
    @SuppressLint("StaticFieldLeak")
    public void callback2(Context context, Integer status, String json) {
        if (!json.equals("fail")) {
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {
                        JSONArray jsonArray = new JSONArray(json);
                        String idToDelete = jsonArray.getJSONObject(0).toString();
                        EventsDatabase.getInstance(context).getEventsDao().deleteEventData(idToDelete);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute("");
        }
    }
}


