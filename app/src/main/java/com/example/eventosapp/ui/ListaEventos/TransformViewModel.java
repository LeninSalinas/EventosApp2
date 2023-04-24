package com.example.eventosapp.ui.ListaEventos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.eventosapp.database.EventoRepository;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;

import java.util.List;

public class TransformViewModel extends AndroidViewModel {

    private EventoRepository repository;
    private final LiveData<List<Eventos>> eventosDataset;

    private final LiveData<List<GPSLocation>> locationDataset;

    public TransformViewModel(@NonNull Application application) {
        //le agregue esta nueva linea pero me tira los datos en blanco
        super(application);
        repository = new EventoRepository(application);
        this.eventosDataset = repository.getEventoDataset();
        this.locationDataset = repository.getLocation();
    }

    public LiveData<List<Eventos>> getEventoDataset() {
        return eventosDataset;
    }

    public LiveData<List<GPSLocation>> getLocationDataset() {
        return locationDataset;
    }

    public void insert(Eventos nuevoEvento){
        repository.insert(nuevoEvento);
    }

    public void update(Eventos modEvento){
        repository.update(modEvento);
    }

    public void delete(Eventos delEvento){
        repository.delete(delEvento);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public void deleteGpsData(){
        repository.deleteGPSData();
    }

    public void updateGpsData(GPSLocation location){
        repository.updateGPS(location);
    }
}