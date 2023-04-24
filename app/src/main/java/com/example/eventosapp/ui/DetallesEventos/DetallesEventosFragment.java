package com.example.eventosapp.ui.DetallesEventos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventosapp.databinding.FragmentFirst2Binding;
import com.example.eventosapp.listEvent.entity.Eventos;

public class DetallesEventosFragment extends Fragment {

    private FragmentFirst2Binding binding;

    int idEvent;

    Eventos eventos;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirst2Binding.inflate(inflater, container, false);

        idEvent = getActivity().getIntent().getIntExtra("EVENTO_ID", 0);//EL ID DEL EVENTO SELECCIONADO
        String eventoTema=getActivity().getIntent().getStringExtra("EVENTO_TEMA");
        String eventoFecha = getActivity().getIntent().getStringExtra("EVENTO_FECHA");
        String eventoExpositor = getActivity().getIntent().getStringExtra("EVENTO_EXPOSITOR");
        String eventoEstado= getActivity().getIntent().getStringExtra("EVENTO_ESTADO");

        eventos = new Eventos();
        eventos.setIdEvento(idEvent);

        binding.txtTema.setText(eventoTema);
        binding.txtFechaEvent.setText(eventoFecha);
        binding.txtNameExpose.setText(eventoExpositor);
        binding.txtState.setText(eventoEstado);

        setHasOptionsMenu(true);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}