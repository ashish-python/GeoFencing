package com.geofencing.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.geofencing.constants.Constants;

import java.util.List;

@Dao
public interface EventsDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    public void addEvent(GeofenceEventEntity geofenceEventEntity);

    @Query("SELECT * FROM " + Constants.GEOFENCE_EVENT_ENTITY)
    List<GeofenceEventEntity> getAll();

    @Query("DELETE FROM "+Constants.GEOFENCE_EVENT_ENTITY+" WHERE id LIKE :id")
    void deleteEventData(String id);

    @Query("DELETE FROM "+Constants.GEOFENCE_EVENT_ENTITY)
    void deleteAllEvents();
}
