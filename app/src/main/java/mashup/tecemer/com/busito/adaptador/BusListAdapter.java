package mashup.tecemer.com.busito.adaptador;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import mashup.tecemer.com.busito.R;
import mashup.tecemer.com.busito.modelo.Bus;

/**
 * Created by Alejandro on 31/05/2017.
 */

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {

    ArrayList<Bus> busItems = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_list_bus, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bus bus = busItems.get(position);

        holder.title.setText("Autobus: " + bus.getId());
        holder.origen.setText("Origen: " + bus.getOrigen());
        holder.destino.setText("Destino: " + bus.getDestino());
        holder.pasajeros.setText("Pasajeros: 10");
    }

    @Override
    public int getItemCount() {
        return busItems.size();
    }

    public void setList(ArrayList<Bus> list) {
        this.busItems = list;
    }

    public void replaceData(ArrayList<Bus> items) {
        setList(items);
        notifyDataSetChanged();
    }

    public void addItem(Bus busitem) {
        busItems.add(0, busitem);
        notifyItemInserted(0);
    }

    public void removeItem(int position) {
        busItems.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll(){
        busItems.clear();
        notifyDataSetChanged();
    }
    /*
        Clase interna
     */

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView origen;
        public TextView destino;
        public TextView pasajeros;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            origen = (TextView) itemView.findViewById(R.id.tv_origen);
            destino = (TextView) itemView.findViewById(R.id.tv_destino);
            pasajeros = (TextView) itemView.findViewById(R.id.tv_pasajeros);
        }
    }
}
