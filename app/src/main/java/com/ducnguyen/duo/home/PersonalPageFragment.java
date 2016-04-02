package com.ducnguyen.duo.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.ducnguyen.duo.data.DataContract.bookmarkEntry;
import com.ducnguyen.duo.data.DatabaseOpener;

import java.util.HashMap;
import java.util.Map;


// This is a simpler implementation of PersonalPageFragment. This
    // implement does not make use of CursorLoader because: (1) the
    // number of items is probably not large and does not require
    // syncing, and (2) there are an unforeseable kinds of query so
    // it is not possible to change the query for CursorLoader
public class PersonalPageFragment extends Fragment {

    public static final String LOG_TAG = PersonalPageFragment
            .class.getSimpleName();

    public static final String[] TAG_COLS = {
        bookmarkEntry._ID,
        bookmarkEntry.COL_BUSID,
        bookmarkEntry.COL_NAME,
        bookmarkEntry.COL_LOC,
        bookmarkEntry.COL_SERVS,
        bookmarkEntry.COL_CIMG,
        bookmarkEntry.COL_LAT,
        bookmarkEntry.COL_LONG
    };

    static final int COL_ID = 0;
    static final int COL_BUSID = 1;
    static final int COL_BUSNAME = 2;
    static final int COL_BUSLOCATION = 3;
    static final int COL_BUSSERVICES = 4;
    static final int COL_BUSCOVERIMAGE = 5;
    static final int COL_LATITUDE = 6;
    static final int COL_LONGITUDE = 7;

    private ListView mListView;
    private PersonalPageAdapter mCursorAdapter;
    static LocationManager lm;
    public static boolean hasLocation = false;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {


        // Initialize call location:
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getActivity(),
                                              Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            hasLocation = true;
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                      2000, 100, Utility.updateLocation);
        }

        // 1. Create view
        View rootView = inflater.inflate(R.layout.fragment_home_personal,
                container,
                false);

        // 2. Create cursor and link it with mCursorAdapter
        DatabaseOpener mDatabaseOpener = new DatabaseOpener(getActivity());
        SQLiteDatabase db = mDatabaseOpener.getReadableDatabase();
        Cursor query = db.query(DataContract.TAG,
                                TAG_COLS, null, null, null, null, null);
        mCursorAdapter = new PersonalPageAdapter(getActivity(), query, 0);

        // 3. Find ListView and set up the Adapter
        mListView = (ListView) rootView.findViewById(
                R.id.listview_home_personal);
        mListView.setAdapter(mCursorAdapter);

        // 4. Create header for listview and it's Ok
        View tempView = inflater.inflate(R.layout.item_home_personal_header,
                                        null);
        mListView.addHeaderView(tempView);

        // 5. Set click handler (direct to business page when user clicks on item)
        // This function requires busID to the server and the services for ViewPager creation
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.v(LOG_TAG, "This happen");

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String busID = cursor.getString(COL_BUSID);
                String services = cursor.getString(COL_BUSSERVICES);

                final Map<String, String> map = new HashMap<String, String>();
                map.put(bookmarkEntry.COL_BUSID, busID);
                map.put(bookmarkEntry.COL_SERVS, services);

                Uri uri = Utility.buildUri(Utility.URI_BUS, map);

                if (Utility.VERBOSITY > 2) {
                    Log.v(LOG_TAG, "Click URI: " + uri.toString());
                }

                Intent intent = new Intent(getActivity(), BusActivity.class)
                        .putExtra(Intent.EXTRA_ORIGINATING_URI, uri.toString());

                startActivity(intent);
            }
        });

        Log.v(LOG_TAG, "Fragment successfully created");
        return rootView;
    }
}