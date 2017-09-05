package mashup.tecemer.com.busito.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mashup.tecemer.com.busito.R;
import mashup.tecemer.com.busito.adaptador.BusListAdapter;
import mashup.tecemer.com.busito.modelo.Bus;

public class BusFragment extends Fragment implements ValueEventListener{

    private BusListAdapter busListAdapter;
    private View rootView;
    private RecyclerView mRecyclerView;
    private LinearLayout mNoMessagesView;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private static final String TAGLOG = "firebase-db";

    public BusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        busListAdapter = new BusListAdapter();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_bus_list);
        mNoMessagesView = (LinearLayout) rootView.findViewById(R.id.noMessages);
        mRecyclerView.setAdapter(busListAdapter);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("buses");
        mFirebaseDatabase.addValueEventListener(this);

    }

    public void showEmptyState(boolean empty) {
        mRecyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        mNoMessagesView.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        busListAdapter.removeAll();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            busListAdapter.addItem(snapshot.getValue(Bus.class));
        }

        if(busListAdapter.getItemCount() < 0){
            showEmptyState(true);
        }else{
            showEmptyState(false);
        }

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAGLOG, "Error!", databaseError.toException());
    }


}
