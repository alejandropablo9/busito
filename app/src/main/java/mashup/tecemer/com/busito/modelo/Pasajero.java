package mashup.tecemer.com.busito.modelo;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alejandro on 03/06/2017.
 */

public class Pasajero {

    private String uid;
    private Coordenadas subida;
    private Coordenadas bajada;
    private DatabaseReference mDatabaseUsuario;
    private Usuario usuario;

    public Pasajero(String uid, Coordenadas subida, Coordenadas bajada) {
        this.uid = uid;
        this.subida = subida;
        this.bajada = bajada;
        setRefDatabase();
    }

    public Pasajero(String uid, double longituds, double latituds) {
        this.uid = uid;
        subida = new Coordenadas(latituds, longituds);
        setRefDatabase();
    }

    public Pasajero(String uid, double longituds, double latituds, double longitudb, double latitudb) {
        this.uid = uid;
        subida = new Coordenadas(latituds, longituds);
        bajada = new Coordenadas(latitudb, longitudb);
        setRefDatabase();
    }

    public Pasajero() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
        setRefDatabase();
    }

    public Coordenadas getSubida() {
        return subida;
    }

    public void setSubida(Coordenadas subida) {
        this.subida = subida;
    }

    public Coordenadas getBajada() {
        return bajada;
    }

    public void setBajada(Coordenadas bajada) {
        this.bajada = bajada;
    }

    public void setRefDatabase(){
        mDatabaseUsuario = FirebaseDatabase.getInstance().getReference("usuarios").child(this.uid);
        mDatabaseUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuario = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LatLng subio(){
        return new LatLng( subida.getLatitud(), subida.getLongitud());
    }

    public LatLng bajo(){
        return new LatLng( bajada.getLatitud(), bajada.getLongitud());
    }

    public Usuario getUsuario(){
        return usuario;
    }


}
