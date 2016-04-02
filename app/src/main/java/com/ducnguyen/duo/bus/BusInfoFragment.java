package com.ducnguyen.duo.bus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;

/**
 * Created by ducprogram on 3/19/16.
 */
public class BusInfoFragment extends Fragment {

    public final String LOG_TAG = BusInfoFragment.class.getSimpleName();
    String busId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busId = getArguments().getString(Utility.COL_BUSID);

        if (Utility.VERBOSITY >= 2) Log.v(LOG_TAG, "The busID is " + busId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_bus_info, container, false);


        return rootView;
    }


}
