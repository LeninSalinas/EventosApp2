package com.example.eventosapp.ui.ListaEventos;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventosapp.EventoApp;
import com.example.eventosapp.OnItemClickListener;
import com.example.eventosapp.R;
import com.example.eventosapp.databinding.FragmentTransformBinding;
import com.example.eventosapp.databinding.ItemTransformBinding;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;
import com.example.eventosapp.ui.DetallesEventos.DetallesEventosActivity;
import com.example.eventosapp.ui.actualizareventos.ActualizarEventosActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
public class TransformFragment extends Fragment implements OnItemClickListener<Eventos>, SearchView.OnQueryTextListener {

    private FragmentTransformBinding binding;

    private TransformAdapter adapter;

    private EventoApp app;

    private TransformViewModel transformViewModel;

    Eventos eventos;

    private SearchView svSearch;

    RecyclerView recyclerView;

    private static final int REQUEST_CODE_GPS = 2;

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransformBinding.inflate(inflater, container, false);
        app=EventoApp.getInstance();
        transformViewModel =
                new ViewModelProvider(this).get(TransformViewModel.class);

        recyclerView = binding.recyclerviewTransform;
        adapter=new TransformAdapter(new ArrayList<>(), this, getContext());
        recyclerView.setAdapter(adapter);

        svSearch=binding.svSearchs;


        transformViewModel.getEventoDataset().observe(getViewLifecycleOwner(), eventos -> {
            adapter.setItems(eventos);
            //aqui va los warnings
            validarDataset();
        });

        setupReciclerView();
        svSearch.setOnQueryTextListener(this);

        binding.tvprecaucion.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
        });

        //initListener();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //ESTABLECEMOS UN TITULO
        actionBar.setTitle("Lista de Eventos");

        View root = binding.getRoot();

        return root;
    }

    private void validarDataset() {
        if(adapter.getItemCount() == 0){
            binding.tvprecaucion.setVisibility(View.VISIBLE);
            binding.imagen.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }else{
            binding.tvprecaucion.setVisibility(View.INVISIBLE);
            binding.imagen.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setupReciclerView(){
        LinearLayoutManager layoutLineal = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutLineal);
        recyclerView.setAdapter(adapter);
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
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 6){
            //EDICIÓN DE UN VEHICULO EXISTENTE
            if(resultCode == RESULT_OK){
                if (getArguments()!=null) {
                    int eventoId= getArguments().getInt("EVENTO_ID");
                    String eventoTema = getArguments().getString("EVENTO_TEMA");
                    String eventoFecha = getArguments().getString("EVENTO_FECHA");
                    String eventoExpositor = getArguments().getString("EVENTO_EXPOSITOR");

                    Eventos actualizar = new Eventos(eventoTema, eventoFecha, eventoExpositor, "");
                    actualizar.setIdEvento(eventoId);
                    transformViewModel.update(actualizar);

                    //adaptador.notifyDataSetChanged();
                }
            }
        }
    }

    private void initListener(){
        svSearch.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    public class TransformAdapter extends RecyclerView.Adapter<TransformAdapter.TransformViewHolder> {

        private List<Eventos> items;

        private List<Eventos> originalItems;
        private OnItemClickListener<Eventos> manageClickAction;
        private Context context; // variable para almacenar el contexto

        public TransformAdapter(List<Eventos> dataset, OnItemClickListener<Eventos> manageClickAction, Context context) {
            this.manageClickAction = manageClickAction;
            this.context = context; // inicializar el contexto
            this.items=new ArrayList<>(dataset);
            this.originalItems=new ArrayList<>(dataset);
        }

        @NonNull
        @Override
        public TransformAdapter.TransformViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransformBinding binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new TransformViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TransformAdapter.TransformViewHolder holder, int position) {
            /*holder.textView.setText(getItem(position));
            holder.imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(holder.imageView.getResources(),
                            drawables.get(position),
                            null));*/
            //if (holder != null) {
                Eventos eventoMostrar = items.get(position);
                holder.binding.ThemeEnd.setText(eventoMostrar.getTema());
                holder.binding.DateEveEnd.setText(eventoMostrar.getFechaEvento());
                holder.binding.exposerEnd.setText(eventoMostrar.getExpositor());
                holder.binding.statusEnd.setText(eventoMostrar.getEstado());
                holder.binding.btnDetalles.setOnClickListener(v -> {
                    eventos=eventoMostrar;
                    obtenerDatos(eventos);
                });
                holder.setOnClickListener(eventoMostrar, manageClickAction);
            //}
        }

        public void obtenerDatos(Eventos data){
            Intent intent=new Intent(getActivity(), DetallesEventosActivity.class);
            intent.putExtra("EVENTO_ID", data.getIdEvento());
            intent.putExtra("EVENTO_TEMA", data.getTema());
            intent.putExtra("EVENTO_FECHA", data.getFechaEvento());
            intent.putExtra("EVENTO_EXPOSITOR", data.getExpositor());
            intent.putExtra("EVENTO_ESTADO", data.getEstado());
            startActivity(intent);
        }

        @Override
        public int getItemCount() {
            return items != null ? items.size() : 0;
        }

        public void setItems(List<Eventos> newDataset){
            copiarDataset(newDataset);
            notifyDataSetChanged();
        }

        public void filter(String strSearch){
            if (strSearch.isEmpty()){
                items =originalItems;
            }else{
                List<Eventos> filteredList=new ArrayList<>();
                for (Eventos e : originalItems) {
                    if (e.getTema().toLowerCase().contains(strSearch.toLowerCase())) {
                        filteredList.add(e);
                    }
                }
                items =filteredList;
                if (filteredList.isEmpty() || strSearch.isEmpty()) {
                    binding.tvNoResults.setVisibility(View.VISIBLE);
                } else {
                    binding.tvNoResults.setVisibility(View.INVISIBLE);
                }
            }
            notifyDataSetChanged();
        }


        public void copiarDataset(List<Eventos> datasetNuevo){
            originalItems = new ArrayList<>(datasetNuevo);
            items.clear();
            items.addAll(originalItems);
        }

        public class TransformViewHolder extends RecyclerView.ViewHolder {
            ItemTransformBinding binding;
            public TransformViewHolder(ItemTransformBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setOnClickListener(final Eventos eventoSelected, final OnItemClickListener<Eventos> listner){
                binding.cardEventoEnd.setOnClickListener(v -> listner.onItemClick(eventoSelected, 1));
            }
        }
    }
}