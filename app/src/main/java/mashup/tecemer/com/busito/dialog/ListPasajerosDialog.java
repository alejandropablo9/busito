package mashup.tecemer.com.busito.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.estimote.sdk.cloud.internal.User;
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

import mashup.tecemer.com.busito.modelo.Bus;
import mashup.tecemer.com.busito.modelo.Usuario;

/**
 * Created by Alejandro on 07/06/2017.
 */

public class ListPasajerosDialog extends DialogFragment {

    private Bus bus;
    private DatabaseReference mDatabaseRutaInfo;
    private DatabaseReference mDatabaseUsuarios;
    private List<Usuario> listUsuarios;
    private List<String> listPasajeros;
    private Usuario user;

    @SuppressLint("ValidFragment")
    public ListPasajerosDialog(List<String> listPasajeros) {
        this.listPasajeros = listPasajeros;
        initDatabase();

    }
    public ListPasajerosDialog() {
        this.listPasajeros = new ArrayList<>();
        initDatabase();

    }

    private void initDatabase(){
        mDatabaseUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createListBusDialog();
    }

    public AlertDialog createListBusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] items = new CharSequence[listPasajeros.size()];

        for(int i = 0; i < items.length; i++){
            /*final Usuario[] usuario = new Usuario[1];
            mDatabaseUsuarios.child(listPasajeros.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usuario[0] = dataSnapshot.getValue(Usuario.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if (usuario[0] != null){
                Toast.makeText(getActivity(), usuario[0].getNombre(), Toast.LENGTH_SHORT).show();
            }*/
            items[i] = listPasajeros.get(i);
        }

        builder.setTitle("Pasajeros: " + items.length)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        return builder.create();
    }
    public List<String> getListPasajeros() {
        return listPasajeros;
    }
    public void setListPasajeros(List<String> listPasajeros) {
        this.listPasajeros = listPasajeros;
    }
    private String getFecha(){
        DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        return formato.format(new Date());
    }


}
