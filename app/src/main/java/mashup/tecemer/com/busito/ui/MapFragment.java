package mashup.tecemer.com.busito.ui;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.estimote.sdk.Region;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import mashup.tecemer.com.busito.dialog.ListBusDialog;
import mashup.tecemer.com.busito.dialog.ListPasajerosDialog;
import mashup.tecemer.com.busito.estimote.BeaconID;
import mashup.tecemer.com.busito.modelo.Bus;
import mashup.tecemer.com.busito.modelo.Coordenadas;
import mashup.tecemer.com.busito.modelo.Pasajero;
import mashup.tecemer.com.busito.modelo.Usuario;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener, DirectionCallback {

    private boolean selRegion = false;
    private static final int metros = 5;

    private View rootView;
    private GoogleMap gMap;
    private MapView mapView;
    private LocationManager locationManager;
    private Location mLastLocation;
    private CameraPosition camera;

    private static final String TAG = "firebase-db";

    private DatabaseReference mDatabaseRuta;
    private DatabaseReference mDatabaseBeacon;
    private DatabaseReference mDatabaseBuses;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabasePasajeros;
    private DatabaseReference mDatabaseUsuarioTemp;
    private DatabaseReference mDatabaseInfo;

    private FirebaseUser user;

    private Usuario usuario;
    private Usuario usuario_temp;
    private Bus busTracking;

    private LatLng ubicacion;
    private List<Bus> listBuses;
    private List<BeaconID> listBeacons;
    private List<String> listPasajeros;
    private List<String> listNombres;
    private List<Pasajero> listaPasajeros;

    private FloatingActionMenu menuRed;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private ListBusDialog dialogList;
    private ListPasajerosDialog pasajerosDialog;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listBeacons = new ArrayList<>();
        listBuses = new ArrayList<>();
        dialogList = new ListBusDialog();
        pasajerosDialog = new ListPasajerosDialog();
        listPasajeros = new ArrayList<>();
        listNombres = new ArrayList<>();
        listaPasajeros = new ArrayList<>();

        menuRed = (FloatingActionMenu) view.findViewById(R.id.menu_red);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.fab3);
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);

        menuRed.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuRed.toggle(true);
            }
        });

        mDatabaseUsuarioTemp = FirebaseDatabase.getInstance().getReference("usuarios");
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid());
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mapView = (MapView) rootView.findViewById(R.id.map);

        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        gMap.setMinZoomPreference(12);
        gMap.setMaxZoomPreference(21);

        mDatabaseBuses = FirebaseDatabase.getInstance().getReference("buses");
        mDatabaseBuses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listBuses.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Bus bus = new Bus();
                    bus.setId((long) snapshot.child("id").getValue() );
                    bus.setIdentifier((String) snapshot.child("identifier").getValue());
                    bus.setOrigen(snapshot.child("origen").getValue(Coordenadas.class));
                    bus.setDestino(snapshot.child("destino").getValue(Coordenadas.class));
                    listBuses.add(bus);

                }
                dialogList.setListBus(listBuses);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseRuta = FirebaseDatabase.getInstance().getReference("ruta");
        mDatabaseBeacon = FirebaseDatabase.getInstance().getReference("beacons");
        mDatabaseBeacon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listBeacons.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    BeaconID beacon = snapshot.getValue(BeaconID.class);
                    listBeacons.add(beacon);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        busTracking = Busito.bus;

        if (!isGPSEnable()) {
            showInfoAlert();
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);

        if(getMyLocation() != null) {
            mLastLocation = getMyLocation();
            camera = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .zoom(18)
                    .bearing(0)
                    .tilt(30)
                    .build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
        }

        if(!selRegion && Busito.bus != null)
            setUbicacionBus(Busito.bus);

        requestDirection();

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, metros, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, metros, this);
    }

    private boolean isGPSEnable() {
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsSignal != 0) {
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showInfoAlert() {
        new AlertDialog.Builder(getContext())
                .setTitle("GPS Signal")
                .setMessage("You don't have GPS signal enabled. Would you like to enable the GPS signal now?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
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

    public void requestDirection() {

        LatLng origen = null;
        LatLng destino = null;
        if(busTracking != null && busTracking != null)
            origen = new LatLng(busTracking.getOrigen().getLatitud(), busTracking.getOrigen().getLongitud());
        if(busTracking != null && busTracking.getDestino() != null)
            destino = new LatLng(busTracking.getDestino().getLatitud(), busTracking.getDestino().getLongitud());
        if(origen != null && destino != null) {
            GoogleDirection.withServerKey("AIzaSyB28-pf1-kV1sVxs20krDbEeGWb00nUwLE")
                    .from(origen)
                    .to(destino)
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }else{
            Toast.makeText(getActivity(), "nulo", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if(Busito.onRegion && usuario.getRoll().equals("conductor")){
            Location myLocation = getMyLocation();
            if(myLocation.distanceTo(mLastLocation) >= metros ) {
                if(Busito.region_autobus != null) {
                    upLocation(getBus(Busito.region_autobus));
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private String getFecha(){
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        return formato.format(new Date());
    }

    private void upLocation(Bus bus){
        if(bus != null) {
            Location myLocation = getMyLocation();
            mLastLocation = myLocation;
            mDatabaseRuta.child(getFecha())
                    .child(bus.getIdentifier())
                    .child("tracking")
                    .setValue(new Coordenadas(myLocation.getLatitude(), myLocation.getLongitude()));
        }
    }

    private void setUbicacionBus(Bus bus){
        mDatabaseRuta.child(getFecha())
                .child(bus.getIdentifier())
                .child("tracking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("latitud").getValue() != null &&
                        dataSnapshot.child("longitud").getValue() != null) {
                    ubicacion = new LatLng(
                            (double) dataSnapshot.child("latitud").getValue(),
                            (double) dataSnapshot.child("longitud").getValue()
                    );
                    prepararmapa();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*public void drawRuta(){
        gMap.clear();
        BeaconID beacon = getBeacon(busTracking);
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(15);
        if(beacon.getColor().equals("blueberry"))
            lineOptions.color(Color.rgb(6, 79, 186));
        else if (beacon.getColor().equals("ice"))
            lineOptions.color(Color.rgb(165, 242, 243));
        else
            lineOptions.color(Color.RED);
        gMap.addPolyline(lineOptions);
    }*/

    public void prepararmapa(){
        gMap.clear();
        putMarkerBus();
        putMakerPasajeros();
        requestDirection();
    }

    private void initDatabaseRutaInfo(final Bus bus){
        if(bus != null) {
            mDatabasePasajeros = FirebaseDatabase.getInstance().getReference("ruta").child(getFecha())
                    .child(bus.getIdentifier()).child("pasajeros");
            mDatabasePasajeros.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listPasajeros.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uid = snapshot.getValue(String.class);
                        listPasajeros.add(uid);
                        initUsuario(uid);
                    }
                    Toast.makeText(getActivity(), "pasajeros: " + listPasajeros.size(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mDatabaseInfo = FirebaseDatabase.getInstance().getReference("ruta").child(getFecha())
                    .child(bus.getIdentifier()).child("info");
            mDatabaseInfo.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaPasajeros.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Pasajero p = new Pasajero();
                        String uid = snapshot.child("uid").getValue().toString();
                        Coordenadas subio = snapshot.child("subida").getValue(Coordenadas.class);
                        Coordenadas bajo = snapshot.child("bajada").getValue(Coordenadas.class);
                        p.setUid(uid);
                        p.setSubida(subio);
                        p.setBajada(bajo);
                        listaPasajeros.add(p);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void initUsuario(String uid){
        usuario_temp = null;
        mDatabaseUsuarioTemp.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario_temp = dataSnapshot.getValue(Usuario.class);
                if(usuario_temp != null)
                    listNombres.add(usuario_temp.getNombre());
                pasajerosDialog.setListPasajeros(listNombres);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

   /* public void getTracking(Bus bus){
        if(bus != null) {
            mDatabaseRuta.child(getFecha()).child(bus.getIdentifier()).child("tracking")
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    points.clear();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Coordenadas coordenadas = snapshot.getValue(Coordenadas.class);
                                        points.add(new LatLng(coordenadas.getLatitud(), coordenadas.getLongitud()));
                                    }
                                    drawRuta();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "ge:onCancelled", databaseError.toException());
                                }
                            });
        }
    }*/

   public void putMarkerBus(){
       if(ubicacion != null) {
           gMap.addMarker(new MarkerOptions().position(ubicacion).title("autobus"));
           putMakerPasajeros();
       }
   }

   public void putMakerPasajeros(){
       for(Pasajero p : listaPasajeros){
           gMap.addMarker(new MarkerOptions().position(p.subio()).title(p.getUsuario().getNombre() + " SUBIO")
           .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_uphdpi)));
           if(p.getBajada() != null)
               gMap.addMarker(new MarkerOptions().position(p.bajo()).title(p.getUsuario().getNombre() + " BAJO")
                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_downhdpi)));
       }
   }

    public Bus getBus(Region region){
        for(BeaconID beaconID : listBeacons){
            if(beaconID.toBeaconRegion().equals(region)) {
                for (Bus bus : listBuses) {
                    if (beaconID.getIdentifier().equals(bus.getIdentifier())) {
                        return bus;
                    }
                }
            }
        }
        return null;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getFragmentManager();
            switch (v.getId()) {
                case R.id.fab1:
                    dialogList.show(fragmentManager, "ListBusDialog");
                    if(dialogList.getSeleccionado() != -1 && dialogList.getSeleccionado() < listBuses.size()) {
                        selRegion = !selRegion;
                        busTracking = listBuses.get(dialogList.getSeleccionado());
                        setUbicacionBus(busTracking);
                        initDatabaseRutaInfo(busTracking);
                        prepararmapa();
                        Toast.makeText(getActivity(), "autobus: "+busTracking.getId(), Toast.LENGTH_SHORT).show();
                    }
                    menuRed.close(true);
                    break;
                case R.id.fab2:
                    selRegion = !selRegion;
                    if(Busito.onRegion){
                        busTracking = getBus(Busito.region_autobus);
                        busTracking = Busito.bus;
                        if(busTracking != null) {
                            Toast.makeText(getActivity(), busTracking.presenter(), Toast.LENGTH_SHORT).show();
                            setUbicacionBus(busTracking);
                            initDatabaseRutaInfo(busTracking);
                            prepararmapa();

                        }
                        else
                            Toast.makeText(getActivity(), "No hay autobus en seguimiento", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getActivity(), "No estas abordo", Toast.LENGTH_SHORT).show();
                    menuRed.close(true);
                    break;
                case R.id.fab3:
                    nombres();
                    pasajerosDialog.setListPasajeros(listNombres);
                    pasajerosDialog.show(fragmentManager, "ListPasajerosDialog");
                    prepararmapa();
                    menuRed.close(true);
                    break;
            }
        }
    };

    private void nombres(){
        listNombres.clear();
        for(Pasajero p : listaPasajeros){
            if(p.getBajada() == null)
                listNombres.add(p.getUsuario().getNombre());
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            ArrayList<LatLng> sectionPositionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
            for (LatLng position : sectionPositionList) {
                gMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.fromResource(R.drawable.band)));
            }

            List<Step> stepList = direction.getRouteList().get(0).getLegList().get(0).getStepList();
            ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getContext(), stepList, 5, Color.RED, 3, Color.BLUE);
            for (PolylineOptions polylineOption : polylineOptionList) {
                gMap.addPolyline(polylineOption);
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }
}
