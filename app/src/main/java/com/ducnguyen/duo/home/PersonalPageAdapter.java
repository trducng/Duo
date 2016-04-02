package com.ducnguyen.duo.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
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
public class PersonalPageAdapter extends CursorAdapter {

    public static double CUR_LATITUDE;
    public static double CUR_LONGITUDE;

    public final String LOG_TAG = PersonalPageAdapter.class.getSimpleName();

    public PersonalPageAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        if (PersonalPageFragment.hasLocation == true) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                Location location = PersonalPageFragment.lm
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    CUR_LATITUDE = Utility.toRadians(location.getLatitude());
                    CUR_LONGITUDE = Utility.toRadians(location.getLongitude());
                }
            }
        }

        if (Utility.VERBOSITY >= 1) {
            Log.v(LOG_TAG, LOG_TAG + " is created with "
                    + String.valueOf(cursor.getCount()) + " rows");
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_home_personal,
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
        String busId = cursor.getString(PersonalPageFragment.COL_BUSID);

        // Set bus name, bus location, bus services and image
        String busName = cursor.getString(PersonalPageFragment.COL_BUSNAME);
        allViews.busName.setText(busName);

        String busLocation = cursor.getString(PersonalPageFragment.COL_BUSLOCATION);
        allViews.busAdd.setText(busLocation);

        String busImage = cursor.getString(PersonalPageFragment.COL_BUSCOVERIMAGE);
        new Utility.ImageViewURL(mContext, allViews.busImage, busId,
                                Utility.URI_TAG, Utility.getFileType(busImage))
                .execute(busImage);

        String[] busServices = cursor
                .getString(PersonalPageFragment.COL_BUSSERVICES)
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

        if (PersonalPageFragment.hasLocation == true) {
            double latitude = Utility.toRadians(
                    cursor.getDouble(PersonalPageFragment.COL_LATITUDE));
            double longitude = Utility.toRadians(
                    cursor.getDouble(PersonalPageFragment.COL_LONGITUDE));
            double distance = Utility.getDistance(
                    latitude, longitude,
                    CUR_LATITUDE, CUR_LONGITUDE);
            allViews.distance.setText(Utility.formatKM(distance));
        }


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
                    R.id.imageview_home_personal_ava);
            busName = (TextView) view.findViewById(
                    R.id.textview_home_personal_busname);
            busAdd = (TextView) view.findViewById(
                    R.id.textview_home_personal_busaddress);
            messImage = (ImageView) view.findViewById(
                    R.id.imageview_home_personal_message);
            delImage = (ImageView) view.findViewById(
                    R.id.imageview_home_personal_delivery);
            scheImage = (ImageView) view.findViewById(
                    R.id.imageview_home_personal_schedule);
            distance = (TextView) view.findViewById(
                    R.id.textview_home_personal_distance);
        }
    }
}