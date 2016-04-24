package com.ducnguyen.duo.home;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ducnguyen.duo.R;
import com.ducnguyen.duo.Utility;

/**
 * Created by ducprogram on 3/18/16.
 */
public class RecommendationPageAdapter extends CursorAdapter {

    public final String LOG_TAG = RecommendationPageAdapter.class.getSimpleName();

    public RecommendationPageAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        if (Utility.VERBOSITY >= 1) {
            Log.v(LOG_TAG, LOG_TAG + " is created with");
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_home_recommendation,
                parent,
                false);

        view.setTag(new AllViews(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Get tag
        AllViews allViews = (AllViews) view.getTag();
        // Get the busID
        String busId = cursor.getString(RecommendationPageFragment.COL_BUSID);

        // Set bus name, bus location, image, bus services, and distance
        String busName = cursor.getString(RecommendationPageFragment.COL_BUSNAME);
        allViews.busName.setText(busName);

        String busLocation = cursor.getString(RecommendationPageFragment.COL_BUSLOCATION);
        allViews.busAdd.setText(busLocation);

        String busImage = cursor.getString(RecommendationPageFragment.COL_BUSCOVERIMAGE);
        new Utility.ImageViewURL(mContext, allViews.busImage, busId,
                Utility.URI_RECOMMEND, Utility.getFileType(busImage))
                .execute(busImage);

        String[] busServices = cursor
                .getString(RecommendationPageFragment.COL_BUSSERVICES)
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

        double distance = cursor.getDouble(RecommendationPageFragment.COL_DISTANCE);
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
                    R.id.imageview_home_recommendation_ava);
            busName = (TextView) view.findViewById(
                    R.id.textview_home_recommendation_busname);
            busAdd = (TextView) view.findViewById(
                    R.id.textview_home_recommendation_busaddress);
            messImage = (ImageView) view.findViewById(
                    R.id.imageview_home_recommendation_message);
            delImage = (ImageView) view.findViewById(
                    R.id.imageview_home_recommendation_delivery);
            scheImage = (ImageView) view.findViewById(
                    R.id.imageview_home_recommendation_schedule);
            distance = (TextView) view.findViewById(
                    R.id.textview_home_recommendation_distance);
        }
    }
}