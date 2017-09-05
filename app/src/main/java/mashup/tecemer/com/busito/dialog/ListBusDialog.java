package mashup.tecemer.com.busito.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mashup.tecemer.com.busito.Busito;
import mashup.tecemer.com.busito.modelo.Bus;

/**
 * Created by Alejandro on 07/06/2017.
 */

public class ListBusDialog extends DialogFragment {

    private List<Bus> listBus;
    private int seleccionado = -1;

    @SuppressLint("ValidFragment")
    public ListBusDialog(List<Bus> listBus) {
        this.listBus = listBus;
    }
    public ListBusDialog() {
        this.listBus = new ArrayList<>();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createListBusDialog();
    }

    public AlertDialog createListBusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final CharSequence[] items = new CharSequence[listBus.size()];

        for(int i = 0; i < items.length; i++)
            items[i] = listBus.get(i).presenter();

        builder.setTitle("Autobuses")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        seleccionado = which;
                        //Toast.makeText(getActivity(), ""+seleccionado, Toast.LENGTH_SHORT).show();
                        Busito.bus = listBus.get(seleccionado);
                    }
                });

        return builder.create();
    }

    public int getSeleccionado(){
        return this.seleccionado;
    }

    public List<Bus> getListBus() {
        return listBus;
    }

    public void setListBus(List<Bus> listBus) {
        this.listBus = listBus;
    }
}
