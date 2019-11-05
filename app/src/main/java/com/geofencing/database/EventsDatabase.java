package com.geofencing.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GeofenceEventEntity.class, GeofenceObjectEntity.class}, version = 2, exportSchema = false)
public abstract class EventsDatabase extends RoomDatabase {

    public abstract EventsDao getEventsDao();

    public abstract GeofenceObjectDao geofenceObjectDao();
}
