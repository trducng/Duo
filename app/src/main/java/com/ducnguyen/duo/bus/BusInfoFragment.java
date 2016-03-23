package com.ducnguyen.duo.bus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ducnguyen.duo.R;

/**
 * Created by ducprogram on 3/19/16.
 */
public class BusInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_bus_info, container, false);



        return rootView;
    }
}
