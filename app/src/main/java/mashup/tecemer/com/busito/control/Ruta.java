package mashup.tecemer.com.busito.control;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
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
import mashup.tecemer.com.busito.modelo.Coordenadas;

/**
 * Created by Alejandro on 04/06/2017.
 */

public class Ruta implements ValueEventListener {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<LatLng> points = null;
    private GoogleMap gMap;

    public Ruta(GoogleMap map){
        points = new ArrayList<>();
        gMap = map;
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("pasajeros")
                .child(getFecha())
                .child("b9407f30-f5f8-466e-aff9-25556b57fe6d")
                .child("ruta");

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        points.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Coordenadas coordenadas = snapshot.getValue(Coordenadas.class);
            points.add( new LatLng( coordenadas.getLatitud(), coordenadas.getLongitud()) );
        }
        drawRuta();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    public void drawRuta(){
        gMap.clear();
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(5);
        lineOptions.color(R.color.colorPrimary);
        gMap.addPolyline(lineOptions);
    }

    private String getFecha(){
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        return formato.format(new Date());
    }
}
