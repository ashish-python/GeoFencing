package com.geofencing.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.geofencing.constants.Endpoints;

import java.util.List;

@Dao
public interface EventsDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public long addEvent(GeofenceEventEntity geofenceEventEntity);

    @Query("SELECT * FROM " + Endpoints.GEOFENCE_EVENT_ENTITY)
    public List<GeofenceEventEntity> getAll();

    @Query("DELETE FROM "+ Endpoints.GEOFENCE_EVENT_ENTITY+" WHERE id LIKE :id")
    public void deleteEventData(String id);

    @Query("DELETE FROM "+ Endpoints.GEOFENCE_EVENT_ENTITY)
    public void deleteAllEvents();
}
