package com.ducnguyen.duo.search;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.bus.BusActivity;
import com.ducnguyen.duo.data.DataContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ducprogram on 3/31/16.
 */
public class SearchFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public final String LOG_TAG = SearchFragment.class.getSimpleName();
    private SearchAdapter mCursorAdapter;

    private int LOADER_ID = 0;

    public static final String[] SEARCH_COLS = {
            DataContract.searchEntry._ID,
            DataContract.searchEntry.COL_BUSID,
            DataContract.searchEntry.COL_NAME,
            DataContract.searchEntry.COL_LOC,
            DataContract.searchEntry.COL_SERVS,
            DataContract.searchEntry.COL_CIMG,
            DataContract.searchEntry.COL_DISTANCE,
    };

    static final int COL_ID = 0;
    static final int COL_BUSID = 1;
    static final int COL_BUSNAME = 2;
    static final int COL_BUSLOCATION = 3;
    static final int COL_BUSSERVICES = 4;
    static final int COL_BUSCOVERIMAGE = 5;
    static final int COL_DISTANCE = 6;


    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 1. Create view
        View rootView = inflater.inflate(R.layout.fragment_search,
                container, false);

        // 2. Instantiate search adapter
        mCursorAdapter = new SearchAdapter(getActivity(), null, 0);

        // 3. Find list view and attach the adapter to this listview
        ListView mListView = (ListView) rootView.findViewById(R.id.listview_search);
        mListView.setAdapter(mCursorAdapter);
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
            }});

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                DataContract.searchEntry.buildURI(),
                SEARCH_COLS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.changeCursor(null);
    }
}
