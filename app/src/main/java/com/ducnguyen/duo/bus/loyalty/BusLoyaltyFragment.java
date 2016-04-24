package com.ducnguyen.duo.bus.loyalty;

import android.database.Cursor;
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
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.data.DataContract.loyaltyDetailEntry;

/**
 * This loyalty fragment will be shown in business-specific page
 * and it is called by the view pager
 */
public class BusLoyaltyFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = BusLoyaltyFragment.class.getSimpleName();
    private static final int LOY_LOADER = 0;
    private BusLoyaltyAdapter mLoyalAdapter;
    String busId;
    int cursorLength;
    ListView listView;

    private static final String[] LOYAL_COLS = {
            loyaltyDetailEntry.COL_BUSID,
            loyaltyDetailEntry.COL_GREETING,
            loyaltyDetailEntry.COL_MESSAGE,
            loyaltyDetailEntry.COL_IMG,
            loyaltyDetailEntry.COL_ITEM,
            loyaltyDetailEntry.COL_ITEM_DESC,
            loyaltyDetailEntry.COL_PTS,
            loyaltyDetailEntry.COL_TYPE,
            loyaltyDetailEntry._ID
    };

    int COL_TYPE = 7;

    public BusLoyaltyFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busId = getArguments().getString(Utility.COL_BUSID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FrameLayout rootView = (FrameLayout) inflater.inflate(
                                            R.layout.fragment_bus_loyalty,
                                            null, false);

        listView = (ListView) rootView.findViewById(
                                            R.id.listview_bus_loyalty);
        mLoyalAdapter = new BusLoyaltyAdapter(getActivity(), null, 0);
        listView.setAdapter(mLoyalAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                loyaltyDetailEntry.buildURI(busId),
                LOYAL_COLS,
                null, null,
                loyaltyDetailEntry.sortQueryReturn());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLoyalAdapter.swapCursor(data);

        if (!data.moveToPosition(data.getCount() - 1)) {
            return;
        }

        if ((data.getInt(COL_TYPE) == 7) && (cursorLength > 0)) {
            listView.smoothScrollToPosition(cursorLength - 1);
            cursorLength = data.getCount();
        } else {
            cursorLength = data.getCount();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLoyalAdapter.swapCursor(null);
    }
}
