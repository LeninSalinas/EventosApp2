package com.example.eventosapp.ui.actualizareventos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.eventosapp.database.EventoRepository;
import com.example.eventosapp.listEvent.entity.Contacto;
import com.example.eventosapp.listEvent.entity.Eventos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActualizarEventosViewModel extends AndroidViewModel {

    private EventoRepository repository;
    private List<Contacto> dataset;

    public ActualizarEventosViewModel(@NonNull Application application) {
        super(application);
        repository=new EventoRepository(application);
    }

    public ActualizarEventosViewModel(@NonNull Application application, List<Contacto> dataset) {
        super(application);
        this.dataset = dataset;
    }

    public void update(Eventos modEvento){
        repository.update(modEvento);
    }

    public void setItems(List<Contacto> dataset){
        this.dataset = dataset;
        Map<String, Contacto> mapa = new HashMap<>();
        for (Contacto contacto:dataset) {
            String key = contacto.getPhone()+contacto.getName();
            mapa.put(key, contacto);
        }
        this.dataset.clear();
        for (Map.Entry<String, Contacto> contacto:mapa.entrySet()) {
            this.dataset.add(contacto.getValue());
        }

        //notifyDataSetChanged();
    }
}
