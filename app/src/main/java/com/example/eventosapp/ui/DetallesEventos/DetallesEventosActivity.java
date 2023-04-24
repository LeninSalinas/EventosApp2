package com.example.eventosapp.ui.DetallesEventos;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.eventosapp.databinding.ActivityDetallesEventosBinding;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventosapp.R;
import com.example.eventosapp.listEvent.entity.Eventos;

public class DetallesEventosActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityDetallesEventosBinding binding;

    private int idEvent;

    TextView txtTema;
    TextView txtFechaEvento;
    TextView txtNombreExpositor;
    TextView txtStatus;
    TextView txtObtenerUbicacion;

    Eventos eventos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetallesEventosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);


        /*idEvent = getIntent().getIntExtra("EVENTO_ID", 0);//EL ID DEL EVENTO SELECCIONADO
        String eventoTema=getIntent().getStringExtra("EVENTO_TEMA");
        String eventoFecha = getIntent().getStringExtra("EVENTO_FECHA");
        String eventoExpositor = getIntent().getStringExtra("EVENTO_EXPOSITOR");
        String eventoEstado= getIntent().getStringExtra("EVENTO_ESTADO");

        eventos = new Eventos();
        eventos.setIdEvento(idEvent);

        txtTema=findViewById(R.id.txtTema);
        txtTema.setText(eventoTema);

        txtFechaEvento=findViewById(R.id.txtFechaEvent);
        txtFechaEvento.setText(eventoFecha);

        txtNombreExpositor=findViewById(R.id.txtNameExpose);
        txtNombreExpositor.setText(eventoExpositor);

        txtStatus=findViewById(R.id.txtState);
        txtStatus.setText(eventoEstado);*/

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_detalles_eventos);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Mostrar botón de "atrás" en el ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ESTABLECEMOS UN TITULO
        getSupportActionBar().setTitle("Detalles del Evento");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_detalles_eventos);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}