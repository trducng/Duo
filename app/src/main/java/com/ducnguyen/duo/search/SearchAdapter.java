package com.ducnguyen.duo.search;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;

/**
 * Created by ducprogram on 4/1/16.
 */
public class SearchAdapter extends CursorAdapter {

    private final String LOG_TAG = SearchAdapter.class.getSimpleName();

    public SearchAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);

        if (Utility.VERBOSITY >= 1) {
            Log.v(LOG_TAG, LOG_TAG + " is created");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_search,
                parent,
                false);

        Log.v(LOG_TAG, "newView created");

        view.setTag(new AllViews(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(LOG_TAG, "bindView created");

        // Get tag
        AllViews allViews = (AllViews) view.getTag();
        // Get the busID
        String busId = cursor.getString(SearchFragment.COL_BUSID);

        // Set bus name, bus location, image, bus services, and distance
        String busName = cursor.getString(SearchFragment.COL_BUSNAME);
        allViews.busName.setText(busName);

        String busLocation = cursor.getString(SearchFragment.COL_BUSLOCATION);
        allViews.busAdd.setText(busLocation);

        String busImage = cursor.getString(SearchFragment.COL_BUSCOVERIMAGE);
        new Utility.ImageViewURL(context, allViews.busImage, busId,
                Utility.URI_SEARCH, Utility.getFileType(busImage))
                .execute(busImage);

        String[] busServices = cursor
                .getString(SearchFragment.COL_BUSSERVICES)
                .split(",");
        for (String eachService : busServices) {
            if (eachService.equals(Utility.CODE_MESSAGE)) {
                allViews.messImage.setImageResource(R.drawable.message);
            } else if (eachService.equals(Utility.CODE_DELIVERY)) {
                allViews.delImage.setImageResource(R.drawable.delivery);
            } else if (eachService.equals(Utility.CODE_SCHEDULE)) {
                allViews.scheImage.setImageResource(R.drawable.calendar);
            }
        }

        double distance = cursor.getDouble(SearchFragment.COL_DISTANCE);
        allViews.distance.setText(Utility.formatKM(distance));

    }

    // Views holder to shorten findViewById time
    public static class AllViews {

        public final ImageView busImage;
        public final TextView busName;
        public final TextView busAdd;
        public final ImageView messImage;
        public final ImageView delImage;
        public final ImageView scheImage;
        public final TextView distance;


        public AllViews(View view) {

            busImage = (ImageView) view.findViewById(
                    R.id.imageview_search_ava);
            busName = (TextView) view.findViewById(
                    R.id.textview_search_busname);
            busAdd = (TextView) view.findViewById(
                    R.id.textview_search_busaddress);
            messImage = (ImageView) view.findViewById(
                    R.id.imageview_search_message);
            delImage = (ImageView) view.findViewById(
                    R.id.imageview_search_delivery);
            scheImage = (ImageView) view.findViewById(
                    R.id.imageview_search_schedule);
            distance = (TextView) view.findViewById(
                    R.id.textview_search_distance);
        }
    }
}
