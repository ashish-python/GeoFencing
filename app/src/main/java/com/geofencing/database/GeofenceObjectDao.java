package com.geofencing.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.geofencing.constants.Constants;

import java.util.List;

@Dao
public interface GeofenceObjectDao {
    //region INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void addGeofence(GeofenceObjectEntity geofenceObjectEntity);
    //endregion

    //region SELECT
    @Query("SELECT * FROM " + Constants.GEOFENCE_OBJECT_ENTITY)
    List<GeofenceObjectEntity> getAll();

    @Query("SELECT  * FROM "+Constants.GEOFENCE_OBJECT_ENTITY+" WHERE geofence_id LIKE :geofenceId")
    List<GeofenceObjectEntity> getGeofence(String geofenceId);
    //endregion

    //region Delete
    @Query("DELETE FROM "+Constants.GEOFENCE_OBJECT_ENTITY+" WHERE geofence_id LIKE :geofenceId")
    void deleteGeofence(String geofenceId);

    @Query("DELETE FROM "+Constants.GEOFENCE_OBJECT_ENTITY)
    void deleteAllGeofences();
    //endregion


}
