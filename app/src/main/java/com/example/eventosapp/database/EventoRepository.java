package com.example.eventosapp.database;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.eventosapp.listEvent.entity.EventoDao;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;

import java.util.List;

public class EventoRepository {
    private final EventoDao eventoDao;
    private final LiveData<List<Eventos>> eventoDataset;
    private final LiveData<List<Eventos>> endEvent;
    private final LiveData<List<GPSLocation>> location;

    //AQUI SE HACE EL USO DEL QUERY SELECT * FROM
    public EventoRepository(Application app) {
        EventoDatabase db = EventoDatabase.getDatabase(app);
        eventoDao = db.eventoDao();
        eventoDataset = eventoDao.getEvento();
        endEvent = eventoDao.getEndEvento();
        location = eventoDao.getLocationById(1);
    }

    public LiveData<List<Eventos>> getEventoDataset(){return eventoDataset;}

    public LiveData<List<Eventos>> getEndEvent(){return endEvent;}

    public LiveData<List<GPSLocation>> getLocation() {
        return location;
    }

    public void insert(Eventos nuevoEvento){
        EventoDatabase.dataWriterExecutor.execute(()->{
            eventoDao.insert(nuevoEvento);
        });
    }

    public void update(Eventos modEvento){
        EventoDatabase.dataWriterExecutor.execute(()->{
            eventoDao.Update(modEvento);
        });
    }
    public void updateGPS(GPSLocation gpsLocation){
        EventoDatabase.dataWriterExecutor.execute(()->{
            eventoDao.UpdateGps(gpsLocation);
        });
    }

    public void delete(Eventos delEvento){
        EventoDatabase.dataWriterExecutor.execute(()->{
            eventoDao.delete(delEvento);
        });
    }

    public void deleteAll(){
        EventoDatabase.dataWriterExecutor.execute(()->{
            eventoDao.deleteAll();
        });
    }

    public void insertGPSLocation(GPSLocation gpsLocation) {
        EventoDatabase.dataWriterExecutor.execute(() -> {
            eventoDao.insertGPSLocation(gpsLocation);
        });
    }


    public void deleteGPSData(){
        EventoDatabase.dataWriterExecutor.execute(()->{
            eventoDao.deleteGPSData();
        });
    }

}
