package com.example.eventosapp.ui.actualizareventos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.eventosapp.R;
import com.example.eventosapp.databinding.ActivityActualizarEventosBinding;
import com.example.eventosapp.listEvent.entity.Contacto;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;
import com.example.eventosapp.ui.ListaEventos.TransformViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActualizarEventosActivity extends AppCompatActivity implements LocationListener{

    private AppBarConfiguration appBarConfiguration;
    private ActivityActualizarEventosBinding binding;

    Spinner spinnerEstado;
    ArrayAdapter<CharSequence> adapterEstado;
    private Eventos eventos;

    Calendar calendar=Calendar.getInstance();
    final int year= calendar.get(Calendar.YEAR);
    final int month= calendar.get(Calendar.MONTH);
    final int day= calendar.get(Calendar.DAY_OF_MONTH);

    int idEvent;

    TextInputLayout tilThemeA;

    TextInputLayout tilDateA;

    AutoCompleteTextView actExposer;

    TransformViewModel transformViewModel;

    Button btnUpdateData;

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 100;

    private static final int REQUEST_CODE_GPS = 78;

    private LocationManager locationManager;

    private GPSLocation ubicacionActual;

    ImageView imgUbicacion;

    TextInputLayout tvUbicacion;

    int idLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transformViewModel=new ViewModelProvider(this).get(TransformViewModel.class);
        binding = ActivityActualizarEventosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //ESTE METODO OBTIENE LOS DATOS DEL MAINVIEW
        //obtenerDatosIntent(getIntent());
        //INSTANCIA DEL EVENTO
        eventos=new Eventos();
        setSupportActionBar(binding.toolbar);

        tilThemeA = findViewById(R.id.tilThemeA);
        tilDateA= findViewById(R.id.tilFechaEventoA);
        actExposer=findViewById(R.id.actExposer);
        btnUpdateData=findViewById(R.id.btnGuardar);
        tvUbicacion=findViewById(R.id.tilUbicacionA);



        spinnerEstado=findViewById(R.id.spStatusEventA);
        adapterEstado = ArrayAdapter.createFromResource(this, R.array.estados_finalizar, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapterEstado.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

        //if (getIntent()!=null){
            idEvent = getIntent().getIntExtra("EVENTO_ID", 0);//EL ID DEL EVENTO SELECCIONADO
            String eventoTema=getIntent().getStringExtra("EVENTO_TEMA");
            String eventoFecha = getIntent().getStringExtra("EVENTO_FECHA");
            String eventoExpositor = getIntent().getStringExtra("EVENTO_EXPOSITOR");
            String eventoEstado= getIntent().getStringExtra("EVENTO_ESTADO");
            idLocation=getIntent().getIntExtra("GPS_LOCATION", 0);

            if (eventoTema!=null && !"".equals(eventoTema) || eventoFecha!=null && !"".equals(eventoFecha) || eventoExpositor!=null && !"".equals(eventoExpositor) || eventoEstado!=null && !"".equals(eventoEstado)){

                tilThemeA.getEditText().setText(eventoTema);
                tilDateA.getEditText().setText(eventoFecha);
                actExposer.setText(eventoExpositor);
                if (eventoEstado.equals("Calendarizado")){
                    spinnerEstado.setSelection(adapterEstado.getPosition("Calendarizado"));
                }

                if (eventoEstado.equals("Por iniciar")){
                    spinnerEstado.setSelection(adapterEstado.getPosition("Por iniciar"));
                }

                if (eventoEstado.equals("Iniciado")){
                    spinnerEstado.setSelection(adapterEstado.getPosition("Iniciado"));
                }

                if (eventoEstado.equals("Finalizado")){
                    spinnerEstado.setSelection(adapterEstado.getPosition("Finalizado"));
                }

                //SE LE ESTABLECE EL DATO AL SPINNER
                spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String estadoSeleccionado = parent.getItemAtPosition(position).toString();
                        eventos.setEstado(estadoSeleccionado);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // No hace nada
                    }
                });//FIN DE ESTA EJECUCION

                //AQUI OBTENEMOS LA FECHA
                tilDateA.getEditText().setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                tilDateA.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(
                                    ActualizarEventosActivity.this,
                                    (view, year, month, dayOfMonth) -> {
                                        month = month + 1;
                                        String date = dayOfMonth + "/" + month + "/" + year;
                                        tilDateA.getEditText().setText(date);
                                    },
                                    year, month, day);
                            datePickerDialog.show();
                            tilDateA.getEditText().clearFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(tilDateA.getEditText().getWindowToken(), 0);

                        }
                    }
                });//FIN DE LA EJECUCION DE LA FECHA

            }//FIN DEL CIERRE DEL IF

        //}
        try {
            //AQUI SOLICITAMOS LOS PERMISOS AL USUARIO
            List<Contacto> misContactos = solicitarPermisoContactos(this);
            ArrayAdapter<Contacto> adapter=new ArrayAdapter<Contacto>(this, android.R.layout.simple_dropdown_item_1line,misContactos);
            actExposer.setAdapter(adapter);
            actExposer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Contacto selectedContact = (Contacto) parent.getItemAtPosition(position);
                    // Aquí puedes hacer algo con el contacto seleccionado
                    actExposer.setText(selectedContact.toString());
                }
            });
            //FIN DE ESTA EJECUCION
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            imgUbicacion=findViewById(R.id.imgUbicacion);
            imgUbicacion.setOnClickListener(v -> {
                solicitarPermisoGPS(this);
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        //AQUI ES EL BOTON DE ACTUALIZAR
        btnUpdateData=findViewById(R.id.btnUpdate);
        btnUpdateData.setOnClickListener(v -> {
            String theme=tilThemeA.getEditText().getText().toString();
            String date=tilDateA.getEditText().getText().toString();
            String exposerName=actExposer.getText().toString();
            String gpsUbication=tvUbicacion.getEditText().toString();
            if (theme.isEmpty() || date.isEmpty() || exposerName.isEmpty() || gpsUbication.isEmpty()){
                //Toast.makeText(this, R.string.error_msg, Toast.LENGTH_SHORT).show();
                Snackbar.make(binding.clUpdate, R.string.error_msg, Snackbar.LENGTH_LONG).show();
            }else {
                try {

                    eventos=new Eventos(theme,date,exposerName,eventos.getEstado());
                    eventos.setIdEvento(idEvent);
                    eventos.setIdLocation(1);
                    transformViewModel.update(eventos);

                    transformViewModel.updateGpsData(new GPSLocation(ubicacionActual.getLatitude(), ubicacionActual.getLongitude()));

                    int numeroAsistencia = 10; // número de asistencia obtenido desde algún lugar


                    /*Intent intent=new Intent(getContext(), mainActivity.getClass());
                    startActivity(intent);
                    mainActivity.finish();*/
                    setResult(Activity.RESULT_OK);
                    if (eventos.getEstado().equals("Finalizado")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("El número de asistencia fue " + numeroAsistencia)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // acción a realizar al hacer clic en "Aceptar"
                                        finish();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000); //2 segundos (2000 milisegundos)
                    }else finish();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Ups! hubo un error al agregar los datos :/", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_actualizar_eventos);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opciones_de_actualizar_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id ==R.id.del_only_one_event) {

            Eventos delEvento=new Eventos(tilThemeA.getEditText().getText().toString(),

                    tilDateA.getEditText().toString(),actExposer.toString(),

                    spinnerEstado.getSelectedItem().toString());
            //Establecemos el id del evento
            delEvento.setIdEvento(idEvent);

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_message)
                    .setTitle(R.string.delete_dialog_title);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    transformViewModel.delete(delEvento);

                    Snackbar.make(findViewById(R.id.consUpdate),getString(R.string.elim_event), Snackbar.LENGTH_LONG).show();
                    finish();
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

            return super.onOptionsItemSelected(item);
    }

    public List<Contacto> solicitarPermisoContactos(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            //SI NO ME HAN DADO EL PERMISO, ENTRA Y DEBO DE SOLICITARLO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
            return new ArrayList<>();
        } else{
            //SI YA TENGO EL PERMISO DEL USUARIO
            return getContacts(context);
        }
    }


    private void abrirGoogleMaps() {
        Uri location = Uri.parse("geo:"+ubicacionActual.getLatitude()+","+ubicacionActual.getLongitude()+"?z=14");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        startActivity(mapIntent);
    }

    private void solicitarPermisoGPS(Context context) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //TENGO EL PERMISO DE USAR EL GPS
            useFineLocation();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_GPS);
        }
    }

    private void obtenerDatosIntent(Intent data){
        idEvent = data.getIntExtra("EVENTO_ID", 0);//EL ID DEL EVENTO SELECCIONADO
        String eventoTema=getIntent().getStringExtra("EVENTO_TEMA");
        String eventoFecha = getIntent().getStringExtra("EVENTO_FECHA");
        String eventoExpositor = getIntent().getStringExtra("EVENTO_EXPOSITOR");
        String eventoEstado= getIntent().getStringExtra("EVENTO_ESTADO");


        if (eventoTema!=null && !"".equals(eventoTema) || eventoFecha!=null && !"".equals(eventoFecha) || eventoExpositor!=null && !"".equals(eventoExpositor) || eventoEstado!=null && !"".equals(eventoEstado)){
            tilThemeA.getEditText().setText(eventoTema);
            tilDateA.getEditText().setText(eventoFecha);
            actExposer.setText(eventoExpositor);
            if (eventoEstado.equals("Calendarizado")){
                spinnerEstado.setSelection(adapterEstado.getPosition("Calendarizado"));
            }

            if (eventoEstado.equals("Por iniciar")){
                spinnerEstado.setSelection(adapterEstado.getPosition("Por iniciar"));
            }

            if (eventoEstado.equals("Iniciado")){
                spinnerEstado.setSelection(adapterEstado.getPosition("Iniciado"));
            }

            if (eventoEstado.equals("Finalizado")){
                spinnerEstado.setSelection(adapterEstado.getPosition("Finalizado"));
            }

            //binding.btnGuardar.setText(getString(R.string.title_update_event));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //AQUI ESTA EL DE LOS CONTACTOS
        if(requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //CONCEDIO EL PERMISO EN TIEMPO DE EJECUCIÓN
                int cantidad = 0;

                List<Contacto> misContactos = getContacts(this);
                //misContactos = misContactos.subList(0, 5);
                ArrayAdapter<Contacto> adapter=new ArrayAdapter<Contacto>(this, android.R.layout.simple_dropdown_item_1line,misContactos);
                actExposer.setAdapter(adapter);
                actExposer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Contacto selectedContact = (Contacto) parent.getItemAtPosition(position);
                        // Aquí puedes hacer algo con el contacto seleccionado
                        actExposer.setText(selectedContact.toString());
                    }
                });
                /*adaptador.setItems(misContactos);
                validateDataset();
                binding.contentLayout.pbContacts.setVisibility(View.INVISIBLE);*/
            }else{

                Snackbar.make(binding.clUpdate, "No se pueden mostrar los contactos, permiso rechazado", Snackbar.LENGTH_LONG).show();
            }
        }//ESTE ES EL CIERRE DE LOS PERMISOS PARA CONTACTOS

        //AQUI ESTA EL DE GPS
        if(requestCode == REQUEST_CODE_GPS){
            //LA SOLICITUD RECIBIDA ES PARA EL PERMISO DE GPS
            if(grantResults.length > 0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //PERMISO PRECISO OTORGADO
                    useFineLocation();
                }else if(grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    //PERMISO APROXIMADO OTORGADO
                    useCoarseLocation();
                }
            }else{
                Snackbar.make(binding.clUpdate,"No se pudo otorgar los permisos del mapa",Snackbar.LENGTH_LONG).show();
            }
            return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @SuppressLint("MissingPermission")
    private void useCoarseLocation() {
        //OBTIENE EL SERVICIO DE UBICACIÓN DEL DISPOSITIVO
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //SOLICITO ACTUALIZAR LA POSICIÓN GPS DE LA RED
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @SuppressLint("MissingPermission")
    private void useFineLocation() {
        //OBTIENE EL SERVICIO DE UBICACIÓN DEL DISPOSITIVO
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //SOLICITO ACTUALIZAR LA POSICIÓN GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }



    public static List<Contacto> getContacts(Context context){
        List<Contacto> contactos = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " DESC");
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
                int idColumIndex = Math.max(cursor.getColumnIndex(ContactsContract.Contacts._ID),0);
                int nameColumIndex = Math.max(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME),0);
                int phoneColumIndex = Math.max(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER),0);

                String id = cursor.getString(idColumIndex);
                String name = cursor.getString(nameColumIndex);

                if(Integer.parseInt(cursor.getString(phoneColumIndex)) > 0){
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?",
                            new String[]{id}, null);

                    while(pCur.moveToNext()){
                        int phoneCommonColumIndex = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        String phone = pCur.getString(phoneCommonColumIndex);

                        Contacto nuevo = new Contacto();
                        nuevo.setName(name);
                        nuevo.setPhone(phone);
                        contactos.add(nuevo);
                    }
                    pCur.close();
                }

            }
        }
        cursor.close();

        return contactos;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        ubicacionActual = new GPSLocation(latitude, longitude);

        tvUbicacion.getEditText().setText(latitude + ","+longitude);

        //DETENIENDO LA LECTURA DE GPS
        locationManager.removeUpdates(this);
    }
}


    /*@Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_actualizar_eventos);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/
