package com.geofencing.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.geofencing.constants.Endpoints;

@Database(entities = {GeofenceEventEntity.class, GeofenceObjectEntity.class}, version = 2, exportSchema = false)
public abstract class EventsDatabase extends RoomDatabase {
    public static final String TAG = EventsDatabase.class.getSimpleName();
    private static EventsDatabase INSTANCE;

    public static EventsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, EventsDatabase.class, Endpoints.GEOFENCE_EVENT_ENTITY)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        super.close();
        INSTANCE = null;
    }

    public abstract EventsDao getEventsDao();

    public abstract GeofenceObjectDao getGeofenceObjectDao();
}
