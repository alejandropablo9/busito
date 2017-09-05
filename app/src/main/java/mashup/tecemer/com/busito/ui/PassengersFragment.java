package mashup.tecemer.com.busito.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mashup.tecemer.com.busito.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PassengersFragment extends Fragment {


    public PassengersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passengers, container, false);
    }

}
