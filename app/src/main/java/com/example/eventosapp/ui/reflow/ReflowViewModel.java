package com.example.eventosapp.ui.reflow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.eventosapp.database.EventoRepository;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;

import java.util.List;

public class ReflowViewModel extends AndroidViewModel {

    private EventoRepository eventoRepository;
    private final LiveData<List<Eventos>> eventosDataset;

    private final LiveData<List<GPSLocation>> locationDataset;

    public ReflowViewModel(@NonNull Application application) {
        //le agregue esta nueva linea pero me tira los datos en blanco
        super(application);
        //repository = new EndEventRepository(application);
        eventoRepository= new EventoRepository(application);
        this.eventosDataset = eventoRepository.getEndEvent();
        this.locationDataset = eventoRepository.getLocation();
    }

    /*public LiveData<List<Eventos>> getEndEventoDataset() {
        return eventosDataset;
    }*/
    public LiveData<List<Eventos>> getEndEvento(){return eventosDataset;};

    public LiveData<List<GPSLocation>> getLocationDataset() {
        return locationDataset;
    }

    public void insert(Eventos nuevoEvento){
        eventoRepository.insert(nuevoEvento);
    }

    public void update(Eventos modEvento){
        eventoRepository.update(modEvento);
    }

    public void delete(Eventos delEvento){
        eventoRepository.delete(delEvento);
    }

    public void deleteAll(){
        eventoRepository.deleteAll();
    }
}