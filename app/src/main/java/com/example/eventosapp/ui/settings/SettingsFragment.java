package com.example.eventosapp.ui.settings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventosapp.MainActivity;
import com.example.eventosapp.R;
import com.example.eventosapp.databinding.FragmentSettingsBinding;
import com.example.eventosapp.listEvent.entity.Contacto;
import com.example.eventosapp.listEvent.entity.Eventos;
import com.example.eventosapp.listEvent.entity.GPSLocation;
import com.example.eventosapp.ui.ListaEventos.TransformFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingsFragment extends Fragment implements LocationListener {

    private FragmentSettingsBinding binding;
    private int idEvent;

    private SettingsViewModel settingsViewModel;
    Spinner spinnerEstado;

    private Eventos eventos;

    Fragment fragment;

    MainActivity mainActivity;
    Calendar calendar=Calendar.getInstance();
    final int year= calendar.get(Calendar.YEAR);
    final int month= calendar.get(Calendar.MONTH);
    final int day= calendar.get(Calendar.DAY_OF_MONTH);

    private ArrayList<Eventos> list;

    AutoCompleteTextView actExposer;

    private static final int PERMISSION_REQUEST_READ_CONTACTS = 1;

    private static final int REQUEST_CODE_GPS = 2;

    private LocationManager locationManager;

    private GPSLocation ubicacionActual;

    int idLocation=1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //ESTABLECEMOS UN TITULO
        actionBar.setTitle("Crea el Evento");

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        eventos=new Eventos();
        fragment=new TransformFragment();
        mainActivity=(MainActivity) getActivity();
        list=new ArrayList<>();

        //AQUI SE OBTIENEN LOS DATOS DEL FRAGMENTO ORIGEN
        /*getChildFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (requestKey.equals("key")) {
                    int eventoId = result.getInt("EVENTO_ID");
                    String eventoTema = result.getString("EVENTO_TEMA");
                    String eventoFecha = result.getString("EVENTO_FECHA");
                    String eventoExpositor = result.getString("EVENTO_EXPOSITOR");
                    // Aquí puedes hacer algo con los valores obtenidos
                    idEvent = result.getInt("EVENTO_ID", 0);

                    binding.tilTheme.getEditText().setText(eventoTema);
                    binding.tilDate.getEditText().setText(eventoFecha);
                    binding.tilExposer.getEditText().setText(eventoExpositor);
                    binding.btnGuardar.setText(getString(R.string.btn_guardar_evento));
                }
            }
        });*/

        /*String[] opciones = {"Manzana", "Banana", "Pera", "Naranja"};
        TextInputLayout autoCompleteTextView = binding.tilExposer;
        ArrayAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, opciones);
        autoCompleteTextView.getEditText().setText((CharSequence) adapter);*/

        binding.tilDate.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            requireContext(),
                            (view, year, month, dayOfMonth) -> {
                                month = month + 1;
                                String date = dayOfMonth + "/" + month + "/" + year;
                                binding.tilDate.getEditText().setText(date);
                            },
                            year, month, day);
                    datePickerDialog.show();
                    binding.tilDate.getEditText().clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.tilDate.getEditText().getWindowToken(), 0);

                }
            }
        });

        spinnerEstado=binding.spinner;
        ArrayAdapter<CharSequence> adapterEstado = ArrayAdapter.createFromResource(getContext(), R.array.estados_crear, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapterEstado.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstado);

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
        });
            //AQUI SOLICITAMOS LOS PERMISOS AL USUARIO
            List<Contacto> misContactos = solicitarPermisoContactos(getActivity());
            ArrayAdapter<Contacto> adapter=new ArrayAdapter<Contacto>(getActivity(), android.R.layout.simple_dropdown_item_1line,misContactos);
            binding.tilExposer.setAdapter(adapter);
            binding.tilExposer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Contacto selectedContact = (Contacto) parent.getItemAtPosition(position);
                    // Aquí puedes hacer algo con el contacto seleccionado
                    binding.tilExposer.setText(selectedContact.toString());
                }
            });

            binding.imgPemisoGps.setOnClickListener(v -> {
                solicitarPermisoGPS(getActivity());
            });

        binding.btnGuardar.setText(getString(R.string.title_create_event));
        binding.btnGuardar.setOnClickListener(v -> {
            String theme=binding.tilTheme.getEditText().getText().toString();
            String date=binding.tilDate.getEditText().getText().toString();
            String exposerName=binding.tilExposer.getText().toString();
            if (theme.isEmpty() | date.isEmpty() | exposerName.isEmpty()){
                Toast.makeText(getContext(), R.string.error_msg, Toast.LENGTH_SHORT).show();
            }else {
                try {
                eventos=new Eventos(theme,date,exposerName,eventos.getEstado());
                eventos.setIdLocation(idLocation);
                settingsViewModel.insert(eventos);

                settingsViewModel.insertGPSLocation(new GPSLocation(ubicacionActual.getLatitude(), ubicacionActual.getLongitude()));

                    Intent intent=new Intent(getContext(), mainActivity.getClass());
                    startActivity(intent);
                    mainActivity.finish();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Ups hubo un error al agregar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        View root = binding.getRoot();
        /*final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/
        return root;
    }

    public List<Contacto> solicitarPermisoContactos(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            //SI NO ME HAN DADO EL PERMISO, ENTRA Y DEBO DE SOLICITARLO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
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
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_GPS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //AQUI ESTA EL DE LOS CONTACTOS
        if(requestCode == PERMISSION_REQUEST_READ_CONTACTS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //CONCEDIO EL PERMISO EN TIEMPO DE EJECUCIÓN
                int cantidad = 0;

                List<Contacto> misContactos = getContacts(getActivity());
                //misContactos = misContactos.subList(0, 5);
                ArrayAdapter<Contacto> adapter=new ArrayAdapter<Contacto>(getActivity(), android.R.layout.simple_dropdown_item_1line,misContactos);
                binding.tilExposer.setAdapter(adapter);
                binding.tilExposer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Contacto selectedContact = (Contacto) parent.getItemAtPosition(position);
                        // Aquí puedes hacer algo con el contacto seleccionado
                        binding.tilExposer.setText(selectedContact.toString());
                    }
                });
                /*adaptador.setItems(misContactos);
                validateDataset();
                binding.contentLayout.pbContacts.setVisibility(View.INVISIBLE);*/
            }else{

                Snackbar.make(binding.clSetting, "No se pueden mostrar los contactos, permiso rechazado", Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(binding.clSetting,"No se pudo otorgar los permisos del mapa",Snackbar.LENGTH_LONG).show();
            }
            return;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @SuppressLint("MissingPermission")
    private void useCoarseLocation() {
        //OBTIENE EL SERVICIO DE UBICACIÓN DEL DISPOSITIVO
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        //SOLICITO ACTUALIZAR LA POSICIÓN GPS DE LA RED
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @SuppressLint("MissingPermission")
    private void useFineLocation() {
        //OBTIENE EL SERVICIO DE UBICACIÓN DEL DISPOSITIVO
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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

        TextInputLayout ubicacion=binding.tilUbicacion;
        ubicacion.getEditText().setText(latitude + ","+longitude);

        System.out.println("ME ESTOY EJECUTANDO AQUI ESTOY");

        //DETENIENDO LA LECTURA DE GPS
        locationManager.removeUpdates(this);
    }

    private void obtenerDatosBundle(Bundle bundle) {
        if (bundle != null) {
            String theme = bundle.getString("EVENTO_TEMA");
            String date = bundle.getString("EVENTO_FECHA");
            String exposer = bundle.getString("EVENTO_EXPOSITOR");

            if (theme != null) {
                idEvent = bundle.getInt("EVENTO_ID", 0);

                binding.tilTheme.getEditText().setText(theme);
                binding.tilDate.getEditText().setText(date);
                binding.tilExposer.setText(exposer);
                binding.btnGuardar.setText(getString(R.string.btn_guardar_evento));

                //mainBinding.appBarMain.toolbar.setTitle(R.string.title_update_vehicle);
            } else {
                binding.btnGuardar.setText(getString(R.string.btn_crear_evento));

                //mainBinding.appBarMain.toolbar.setTitle(R.string.title_create_vehicle);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}