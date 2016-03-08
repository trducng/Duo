package com.ducnguyen.duo;

import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ducprogram on 3/7/16.
 */
public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static final String BASE_URL = "http://www.google.com";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";
    public static final String TIME = "time";

    public static Uri buildUri(Map<String, String> data) {

        Uri.Builder query = Uri.parse(BASE_URL).buildUpon();

        Iterator iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            query.appendQueryParameter((String) pair.getKey(), (String) pair.getValue());
        }
        return query.build();
    }

    public static LocationListener ll = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Map<String, String> userLoc = new HashMap<String, String>();
            userLoc.put(LATITUDE, String.valueOf(location.getLatitude()));
            userLoc.put(LONGITUDE, String.valueOf(location.getLongitude()));
            userLoc.put(TIME, String.valueOf(location.getTime()));

            Log.v("LOCATION Uri", String.valueOf(buildUri(userLoc)));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public static void sendHTTPRequest(Uri uri) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct URL from the URI
            URL url = new URL(uri.toString());

            // Create connection to the Internet, send request type and connect
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {return;}
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {return;}

            // TODO: (Utility) a better way to query data is to download the data to
            // SQLite and then query the data from SQLite, which means that the code
            // from this block should be put into another class
            // TODO: (Utility) to load and query data, create a SyncAdapter, with
            // Authenticator service

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed URL: " + uri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with url.openConnection()");
        }
    }

    public static boolean updated = false;
}
