package mashup.tecemer.com.busito.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mashup.tecemer.com.busito.R;
import mashup.tecemer.com.busito.modelo.Usuario;

/**
 * Fragmento con un diálogo personalizado
 */
public class FormDialog extends DialogFragment  implements ValueEventListener {
    private static final String TAG = FormDialog.class.getSimpleName();

    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private Usuario usuario;
    private EditText nombreT;
    private EditText phoneT;

    public FormDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createLoginDialogo();
    }

    /**
     * Crea un diálogo con personalizado para comportarse
     * como formulario de login
     *
     * @return Diálogo
     */
    public AlertDialog createLoginDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_datos_user, null);

        nombreT = (EditText) v.findViewById(R.id.nombre_input);
        phoneT = (EditText) v.findViewById(R.id.phone_input);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(user.getUid());
        mDatabase.addValueEventListener(this);

        builder.setView(v);

        Button close = (Button) v.findViewById(R.id.cerrar_boton);
        Button save = (Button) v.findViewById(R.id.guardar_boton);

        close.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );

        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guardarUsuario();
                        dismiss();
                    }
                }

        );
        return builder.create();
    }

    private void guardarUsuario(){
        if(usuario != null){
            usuario.setTutor(nombreT.getText().toString());
            usuario.setNumero_emergencia(phoneT.getText().toString());
            mDatabase.setValue(usuario);
        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        usuario = dataSnapshot.getValue(Usuario.class);
        if(usuario != null){
            if(usuario.getTutor() != null){
                nombreT.setText(usuario.getTutor());
            }
            if(usuario.getNumero_emergencia() != null){
                phoneT.setText(usuario.getNumero_emergencia());
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, "Error!", databaseError.toException());
    }
}

