package com.geofencing.stores;

import android.content.Context;

public class EventsStore extends BaseStore {

    private static final String SHARED_PREFS = "events";
    private static final String TIMESTAMP = "timestamp";
    private Context context;
    private static EventsStore instance = null;

    private EventsStore(Context context) {
        super(SHARED_PREFS, context);
        this.context = context;
    }

    //Singleton instance
    public static EventsStore getInstance(Context context) {
        if (instance == null) {
            instance = new EventsStore(context);
        }
        return instance;
    }

    public String getTimestamp() {
        return getString(TIMESTAMP, "");
    }

    public void setTimestamp(String value) {
        savePair(TIMESTAMP, value);
    }
}


