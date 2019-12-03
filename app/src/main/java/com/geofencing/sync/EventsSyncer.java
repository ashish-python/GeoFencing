package com.geofencing.sync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.geofencing.constants.Endpoints;
import com.geofencing.database.EventsDatabase;
import com.geofencing.database.GeofenceEventEntity;
import com.geofencing.listeners.BaseListener;
import com.geofencing.utils.NetworkPostRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class EventsSyncer implements BaseListener {
    private Context context;
    private static EventsSyncer instance;

    private EventsSyncer(Context context) {
        this.context = context;
    }

    public static EventsSyncer getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        return new EventsSyncer(context);
    }

    @SuppressLint("StaticFieldLeak")
    public void syncGeofenceEvents() {
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
                Log.v("FCM_JSON_EVENT_BLANK", json);
                new NetworkPostRequest(context, Endpoints.SEND_EVENT_DATA_TO_SERVER_URL, EventsSyncer.this::callback, Endpoints.SEND_EVENT_DATA_TO_SERVER_TASK).execute(json);
            }
        }.execute("");
    }

    //Delete events from local db once sent to the server
    @SuppressLint("StaticFieldLeak")
    public void callback(Context context, Integer status, String json) {
        Log.v("FCM_saved_date", json);
        if (!json.equals("fail")) {
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {
                        JSONArray jsonArray = new JSONArray(json);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            //JSONObject jsonObject = jsonArray.getJSONObject(i).getString()
                            EventsDatabase.getInstance(context).getEventsDao().deleteAllEvents();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute("");
        }
    }
}
