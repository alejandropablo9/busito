package mashup.tecemer.com.busito.ui;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mashup.tecemer.com.busito.Busito;
import mashup.tecemer.com.busito.R;
import mashup.tecemer.com.busito.dialog.FormDialog;
import mashup.tecemer.com.busito.estimote.BeaconID;
import mashup.tecemer.com.busito.modelo.Bus;
import mashup.tecemer.com.busito.modelo.Coordenadas;
import mashup.tecemer.com.busito.modelo.Pasajero;
import mashup.tecemer.com.busito.modelo.Usuario;

public class PerfilFragment extends Fragment implements ValueEventListener {

    private static final String TAGLOG = "firebase-db";
    private TextView tvNombre;
    private TextView tvEmail;
    private TextView tvTutor;
    private TextView tvPhone;
    private TextView tvNoBus;
    private TextView tvEstadoBus;
    private ImageView imgEsatoBus;

    private View rootView;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseBuses;
    private DatabaseReference mDatabaseBeacon;
    private DatabaseReference mDatabaseRuta;
    private List<Bus> listBuses;
    private List<BeaconID> listBeacons;

    private FirebaseUser user;
    private Usuario usuario;
    private Pasajero yo;

    private CardView cardView;
    private BeaconManager beaconManager;
    private List<Region> regionsToMonitor = new ArrayList<>();

    public PerfilFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_perfil, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardView = (CardView) rootView.findViewById(R.id.cardDatos);

        beaconManager = new BeaconManager(getContext());
        listBeacons = new ArrayList<>();
        listBuses = new ArrayList<>();
        yo = new Pasajero();

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(user.getUid());
        mDatabase.addValueEventListener(this);

        mDatabaseBeacon = FirebaseDatabase.getInstance().getReference("beacons");
        mDatabaseBeacon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                regionsToMonitor.clear();
                listBeacons.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BeaconID beacon = snapshot.getValue(BeaconID.class);
                    listBeacons.add(beacon);
                    regionsToMonitor.add( beacon.toBeaconRegion() );
                }
                startMonitoring();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseBuses = FirebaseDatabase.getInstance().getReference("buses");
        mDatabaseBuses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listBuses.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bus bus = snapshot.getValue(Bus.class);
                    listBuses.add(bus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setBusView(null);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Busito.onRegion = true;
                Busito.region_autobus = region;
                enableAbordo();
                for(BeaconID beaconID : listBeacons){
                   if(beaconID.toBeaconRegion().equals(region))
                       onBus(beaconID);
                }
                Location location = getMyLocation();
                if(location != null){
                    yo.setUid(user.getUid());
                    yo.setSubida( new Coordenadas(location.getLatitude(), location.getLongitude()) );
                    yo.setBajada(null);
                    guardarInfo(yo);
                }
            }

            @Override
            public void onExitedRegion(Region region) {
                disabelAbordo();
                setBusView(null);
                Location location = getMyLocation();
                if(location != null){
                    yo.setBajada( new Coordenadas(location.getLatitude(), location.getLongitude()) );
                    guardarInfo(yo);
                }

            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuario.getRoll() != null && !usuario.getRoll().equals("conductor")) {
                    FragmentManager fragmentManager = getFragmentManager();
                    new FormDialog().show(fragmentManager, "FormDialog");
                }
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        usuario = dataSnapshot.getValue(Usuario.class);
        if (usuario == null){
            usuario = new Usuario();
            usuario.setNombre(user.getDisplayName());
            usuario.setEmail(user.getEmail());
            usuario.setRoll("user");
            guardarUsuario(usuario);
        }
        setDatosView();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAGLOG, "Error!", databaseError.toException());
    }

    private void guardarUsuario(Usuario user){
        mDatabase.setValue(user);
    }

    private void setDatosView(){
        tvNombre = (TextView) rootView.findViewById(R.id.texto_nombre);
        tvEmail = (TextView) rootView.findViewById(R.id.texto_email);
        tvTutor = (TextView) rootView.findViewById(R.id.texto_tutor);
        tvPhone = (TextView) rootView.findViewById(R.id.texto_telefono);

        tvNombre.setText(usuario.getNombre());
        tvEmail.setText(usuario.getEmail());

        if(usuario.getTutor() != null)
            tvTutor.setText(usuario.getTutor());
        if(usuario.getNumero_emergencia() != null)
            tvPhone.setText(usuario.getNumero_emergencia());
        if(usuario.getRoll().equals("conductor")){
            tvTutor.setText(usuario.getRoll());
        }
    }

    public void startMonitoring() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                for (Region region : regionsToMonitor) {
                    beaconManager.startMonitoring(region);
                }
            }
        });
    }

    private void onBus(BeaconID beaconID){
        for(Bus bus : listBuses){
            if(beaconID.getIdentifier().equals(bus.getIdentifier())){
                setBusView(bus);
                Busito.bus = bus;
                initDatabaseRuta(bus);
            }
        }
    }

    private void setBusView(Bus onthisbus){
        tvNoBus = (TextView) rootView.findViewById(R.id.texto_n_bus);
        tvEstadoBus = (TextView) rootView.findViewById(R.id.texto_bus_estado);
        imgEsatoBus = (ImageView) rootView.findViewById(R.id.imageEstadoView);
        if(onthisbus != null){
            tvNoBus.setText("No. de autobus: " + onthisbus.getId());
            enableAbordo();
        }else{
            tvNoBus.setText("...");
            disabelAbordo();
        }
    }

    private void enableAbordo(){
        imgEsatoBus = (ImageView) rootView.findViewById(R.id.imageEstadoView);
        imgEsatoBus.setImageResource(R.drawable.ruta);
        tvEstadoBus.setText("ABORDO");
    }

    private void disabelAbordo(){
        imgEsatoBus = (ImageView) rootView.findViewById(R.id.imageEstadoView);
        tvEstadoBus.setText("¿Buscando?...");
        imgEsatoBus.setImageResource(R.drawable.run);
    }

    private String getFecha(){
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        return formato.format(new Date());
    }

    private void guardarInfo(Pasajero yo){
        if(mDatabaseRuta != null && !usuario.getRoll().equals("conductor")){
            mDatabaseRuta.child("info").child(yo.getUid()).setValue(yo);
            if(yo.getBajada() == null)
                mDatabaseRuta.child("pasajeros").child(yo.getUid()).setValue(yo.getUid());
            else
                mDatabaseRuta.child("pasajeros").child(yo.getUid()).setValue(null);
        }
    }

    private void initDatabaseRuta(Bus bus){
        mDatabaseRuta =  FirebaseDatabase.getInstance().getReference("ruta").child(getFecha())
                .child(bus.getIdentifier());
    }

    private Location getMyLocation() {
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            myLocation = lm.getLastKnownLocation(provider);
        }
        return myLocation;
    }
}
