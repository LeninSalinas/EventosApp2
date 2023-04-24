package com.example.eventosapp.ui.settings;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventosapp.database.EventoRepository;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;

import java.util.List;

public class SettingsViewModel extends AndroidViewModel {

    private EventoRepository repository;

    private LiveData<List<Eventos>> data;

    public SettingsViewModel(Application application) {
        super(application);
        repository=new EventoRepository(application);

    }

    public LiveData<List<Eventos>> getData() {
        return data;
    }

    public void insert(Eventos nuevoEvento){
        repository.insert(nuevoEvento);
    }

    public void update(Eventos modEvento){
        repository.update(modEvento);
    }

    public void insertGPSLocation(GPSLocation location){
        repository.insertGPSLocation(location);
    }
}