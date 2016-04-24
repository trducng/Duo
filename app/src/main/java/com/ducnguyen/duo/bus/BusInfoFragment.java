package com.ducnguyen.duo.bus;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

import com.ducnguyen.duo.CustomViews;
import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;
import com.ducnguyen.duo.data.DataContract.detailedEntry;

/**
 * Created by ducprogram on 3/19/16.
 */
public class BusInfoFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    public final String LOG_TAG = BusInfoFragment.class.getSimpleName();
    private int LOADER_ID = 10;
    String busId;
    CustomViews.BusMainInfo info;

    private static final String[] INFO_COLS = {
            detailedEntry.COL_BUSID,
            detailedEntry.COL_NAME,
            detailedEntry.COL_SHORTLOC,
            detailedEntry.COL_OPEN,
            detailedEntry.COL_LOC,
            detailedEntry.COL_CONTACT,
            detailedEntry.COL_IMG,
            detailedEntry.COL_HOURS,
            detailedEntry.COL_NEWS,
            detailedEntry.COL_LOY
    };

    static final int COL_BUSID = 0;
    static final int COL_NAME = 1;
    static final int COL_SHORTLOC = 2;


    public BusInfoFragment() {}

    public static class DSV extends ScrollView {
        public DSV(Context context) {
            super(context);
            this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    2000
            ));
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            Log.v("Scroll", "Action: " + ev.getOrientation());
            Log.v("Scroll", "getY(): " + ev.getY());

            ViewParent abc = this.getParent().getParent().getParent();
            Log.v("Scroll", "Parent: " + abc.toString());

            return super.onTouchEvent(ev);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busId = getArguments().getString(Utility.COL_BUSID);

        if (Utility.VERBOSITY >= 2) Log.v(LOG_TAG, "The busID is " + busId);
        if (Utility.IMPORTANCE) Log.v(LOG_TAG, "The busID is " + busId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 1 Create root view for the fragment
        ScrollView rootView = (ScrollView) inflater.inflate(
                R.layout.fragment_bus_info, container, false);
//        rootView.requestDisallowInterceptTouchEvent(true);
        DSV test = new DSV(getActivity());

        // 2 Dynamically create info
        info = new CustomViews.BusMainInfo(getActivity(), null);

        test.addView(info);
        return test;

        // 2.b Test
//        LinearLayout ab = new LinearLayout(getActivity());
//        ab.setOrientation(LinearLayout.VERTICAL);
//        ab.setLayoutParams(new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        TextView loc = new TextView(getActivity());
//        loc.setText(R.string.test_loc);
//        loc.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        TextView contact = new TextView(getActivity());
//        contact.setText(R.string.test_contact);
//        contact.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        TextView img = new TextView(getActivity());
//        img.setText(R.string.test_img);
//        img.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        TextView hours = new TextView(getActivity());
//        hours.setText(R.string.test_hours);
//        hours.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        TextView news = new TextView(getActivity());
//        news.setText(R.string.test_news);
//        news.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        TextView loy = new TextView(getActivity());
//        loy.setText(R.string.test_loy);
//        loy.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));

        // 2bi Add all those views:
//        ab.addView(loc);
//        ab.addView(contact);
//        ab.addView(img);
//        ab.addView(hours);
//        ab.addView(news);
//        ab.addView(loy);



        // 2c Manually add view to info
//        info.addView(loc);
//        info.addView(contact);
//        info.addView(img);
//        info.addView(hours);
//        info.addView(news);
//        info.addView(loy);

        // 3 Add the created view to the root view
//        rootView.addView(ab);
//        rootView.addView(info);
//        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        info.removeAllViews();
        info.removeAllViewsInLayout();
        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                detailedEntry.buildDetailedURI(busId),
                INFO_COLS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        info.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        info.changeCursor(null);
    }
}
