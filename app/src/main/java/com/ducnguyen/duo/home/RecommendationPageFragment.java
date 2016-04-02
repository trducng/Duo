package com.ducnguyen.duo.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.bus.BusActivity;
import com.ducnguyen.duo.data.DataContract;
import com.ducnguyen.duo.data.DatabaseOpener;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by ducprogram on 3/20/16.
 */

public class RecommendationPageFragment extends Fragment {

    public static final String LOG_TAG = RecommendationPageFragment.class
                                        .getSimpleName();

    private ListView mListView;
    static LocationManager lm;
    private RecommendationPageAdapter mCursorAdapter;

    public static final String[] TAG_COLS = {
            DataContract.recommendEntry._ID,
            DataContract.recommendEntry.COL_BUSID,
            DataContract.recommendEntry.COL_NAME,
            DataContract.recommendEntry.COL_LOC,
            DataContract.recommendEntry.COL_SERVS,
            DataContract.recommendEntry.COL_CIMG,
            DataContract.recommendEntry.COL_DISTANCE,
    };

    static final int COL_ID = 0;
    static final int COL_BUSID = 1;
    static final int COL_BUSNAME = 2;
    static final int COL_BUSLOCATION = 3;
    static final int COL_BUSSERVICES = 4;
    static final int COL_BUSCOVERIMAGE = 5;
    static final int COL_DISTANCE = 6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 1. Create view
        View rootView = inflater.inflate(R.layout.fragment_home_recommendation,
                                        container, false);

        // 2. Construct URI with location and download data to database
        // This block of code should be changed in sometimes to the new Google Play Service API
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener recommendListener = new Utility.ProcessLocation(getActivity(),
                                                    Utility.URI_RECOMMEND);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                      30000, 100, recommendListener);
        }

        // 3. Create cursor and link it with mCursorAdapter
        // TODO: one possible problem, Recommend table might be empty
        DatabaseOpener mDatabaseOpener = new DatabaseOpener(getActivity());
        Cursor query = mDatabaseOpener.getReadableDatabase()
                            .query(DataContract.RECOMMENDATION, TAG_COLS,
                                    null, null, null, null, null);
        mCursorAdapter = new RecommendationPageAdapter(getActivity(), query, 0);

        // 4. Find ListView and set up the Adapter
        mListView = (ListView) rootView.findViewById(R.id.listview_home_recommendation);
        mListView.setAdapter(mCursorAdapter);

        // 5. Set click listener for each item in the listView
        // TODO: finish setOnItemClickListener after creating page for store
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String busID = cursor.getString(COL_BUSID);
                String services = cursor.getString(COL_BUSSERVICES);

                final Map<String, String> map = new HashMap<String, String>();
                map.put(DataContract.bookmarkEntry.COL_BUSID, busID);
                map.put(DataContract.bookmarkEntry.COL_SERVS, services);

                Uri uri = Utility.buildUri(Utility.URI_BUS, map);

                Intent intent = new Intent(getActivity(), BusActivity.class)
                        .putExtra(Intent.EXTRA_ORIGINATING_URI, uri.toString());
                startActivity(intent);
            }
        });


        Log.v(LOG_TAG, LOG_TAG + " is succesfully created");
        return rootView;
    }
}
