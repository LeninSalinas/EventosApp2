package com.example.eventosapp.listEvent.entity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventoDao {

    @Insert
    void insert(Eventos nuevoEvento);

    @Update
    void Update(Eventos modEvento);

    @Delete
    void delete(Eventos delEvento);

    @Query("DELETE FROM evento_table WHERE NOT estado='Finalizado'")
    void deleteAll();

    @Query("DELETE FROM gps_location")
    void deleteGPSData();

    @Update
    void UpdateGps(GPSLocation gpsLocation);

    @Query("SELECT * FROM evento_table WHERE NOT estado='Finalizado' order by fecha")
    LiveData<List<Eventos>> getEvento();

    @Query("SELECT * FROM evento_table WHERE estado='Finalizado'")
    LiveData<List<Eventos>> getEndEvento();

    @Insert
    void insertGPSLocation(GPSLocation gpsLocation);

    @Query("SELECT * FROM gps_location WHERE id_Location = :idLocation")
    LiveData<List<GPSLocation>> getLocationById(int idLocation);
}
