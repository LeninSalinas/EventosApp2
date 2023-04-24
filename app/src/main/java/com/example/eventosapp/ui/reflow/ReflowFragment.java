package com.example.eventosapp.ui.reflow;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventosapp.EventoApp;
import com.example.eventosapp.OnItemClickListener;
import com.example.eventosapp.R;
import com.example.eventosapp.databinding.FragmentReflowBinding;
import com.example.eventosapp.databinding.ItemReflowBinding;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.ui.actualizareventos.ActualizarEventosActivity;

import java.util.ArrayList;
import java.util.List;

public class ReflowFragment extends Fragment implements OnItemClickListener<Eventos>, SearchView.OnQueryTextListener {

    private FragmentReflowBinding binding;

    private ReflowAdapter adapter;

    private EventoApp app;

    private ReflowViewModel reflowViewModel;

    Eventos eventos;

    SearchView svSearch;

    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentReflowBinding.inflate(inflater, container, false);
        app=EventoApp.getInstance();
        reflowViewModel =
                new ViewModelProvider(this).get(ReflowViewModel.class);

        recyclerView = binding.rcReflow;
        adapter=new ReflowFragment.ReflowAdapter(new ArrayList<>(), this, getContext());
        recyclerView.setAdapter(adapter);

        reflowViewModel.getEndEvento().observe(getViewLifecycleOwner(), eventos -> {
            adapter.setItems(eventos);
            //aqui va los warnings
            validarDataset();
        });

        svSearch=binding.Buscador;

        setupReciclerView();
        svSearch.setOnQueryTextListener(this);

        binding.txtwarning.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        });

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //ESTABLECEMOS UN TITULO
        actionBar.setTitle("Eventos finalizados");

        View root = binding.getRoot();

        return root;
    }

    private void validarDataset() {
        if(adapter.getItemCount() == 0){
            binding.txtwarning.setVisibility(View.VISIBLE);
            binding.imageEmptyEve.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }else{
            binding.txtwarning.setVisibility(View.INVISIBLE);
            binding.imageEmptyEve.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupReciclerView() {
        LinearLayoutManager layoutLineal = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutLineal);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(Eventos data, int type) {
        /*Fragment settingsFragment=new SettingsFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("EVENTO_ID", data.getIdEvento());
        bundle.putString("EVENTO_TEMA", data.getTema());
        bundle.putString("EVENTO_FECHA", data.getFechaEvento());
        bundle.putString("EVENTO_EXPOSITOR", data.getExpositor());
        bundle.putString("EVENTO_ESTADO", data.getEstado());
        settingsFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_main, settingsFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();*/
        Intent intent=new Intent(getActivity(), ActualizarEventosActivity.class);
        intent.putExtra("EVENTO_ID", data.getIdEvento());
        intent.putExtra("EVENTO_TEMA", data.getTema());
        intent.putExtra("EVENTO_FECHA", data.getFechaEvento());
        intent.putExtra("EVENTO_EXPOSITOR", data.getExpositor());
        intent.putExtra("EVENTO_ESTADO", data.getEstado());
        intent.putExtra("GPS_ID_LOCATION", data.getIdLocation());
        startActivityForResult(intent, 6, ActivityOptions.makeSceneTransitionAnimation(this.getActivity()).toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==6){
            if (requestCode==RESULT_OK){
                int id = data.getIntExtra("ID", 0);//EL ID DEL EVENTO SELECCIONADO
                String eventoTema=data.getStringExtra("EVENTO_TEMA");
                String eventoFecha = data.getStringExtra("EVENTO_FECHA");
                String eventoExpositor = data.getStringExtra("EVENTO_EXPOSITOR");
                String eventoEstado= data.getStringExtra("EVENTO_ESTADO");
                eventos=new Eventos(eventoTema,eventoFecha,eventoExpositor,eventoEstado);
                eventos.setIdEvento(id);
                reflowViewModel.update(eventos);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    public class ReflowAdapter extends RecyclerView.Adapter<ReflowAdapter.ReflowViewHolder> {

        private List<Eventos> items2;
        private List<Eventos> originalItems;
        private OnItemClickListener<Eventos> manageClickAction;

        private Context context; // variable para almacenar el contexto
        public ReflowAdapter(List<Eventos> dataset, OnItemClickListener<Eventos> manageClickAction, Context context) {
            this.manageClickAction = manageClickAction;
            this.context = context; // inicializar el contexto
            this.items2=new ArrayList<>(dataset);
            this.originalItems=new ArrayList<>(dataset);
        }

        @NonNull
        @Override
        public ReflowAdapter.ReflowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemReflowBinding binding=ItemReflowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new ReflowViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ReflowAdapter.ReflowViewHolder holder, int position) {
            Eventos eventoMostrar = items2.get(position);
            holder.binding.tvTema.setText(eventoMostrar.getTema());
            holder.binding.tvDate.setText(eventoMostrar.getFechaEvento());
            holder.binding.exposerName.setText(eventoMostrar.getExpositor());
            holder.binding.tvStatus.setText(eventoMostrar.getEstado());
            holder.setOnClickListener(eventoMostrar, manageClickAction);
        }

        @Override
        public int getItemCount() {
            return items2 != null ? items2.size() : 0;
        }

        public void setItems(List<Eventos> newDataset) {
            copiarDataset(newDataset);
            notifyDataSetChanged();
        }

        public void filter(String strSearch){
            if (strSearch.isEmpty()){
                items2 =originalItems;
            }else{
                List<Eventos> filteredList=new ArrayList<>();
                for (Eventos e : originalItems) {
                    if (e.getTema().toLowerCase().contains(strSearch.toLowerCase())) {
                        filteredList.add(e);
                    }
                }
                if (filteredList.isEmpty() || strSearch.isEmpty()) {
                    binding.tvSinResultados.setVisibility(View.VISIBLE);
                } else {
                    binding.tvSinResultados.setVisibility(View.INVISIBLE);
                }
                items2 =filteredList;
            }
            notifyDataSetChanged();
        }
        public void copiarDataset(List<Eventos> datasetNuevo){
            originalItems = new ArrayList<>(datasetNuevo);
            items2.clear();
            items2.addAll(originalItems);
        }

        private class ReflowViewHolder extends RecyclerView.ViewHolder {
            public ItemReflowBinding binding;

            public ReflowViewHolder(ItemReflowBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setOnClickListener(final Eventos eventoSelected, final OnItemClickListener<Eventos> listner){
                binding.cvEndEve.setOnClickListener(v -> listner.onItemClick(eventoSelected, 1));
            }
        }
    }
}