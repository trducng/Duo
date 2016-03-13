package com.ducnguyen.duo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ducnguyen.duo.data.DataContract;
import com.ducnguyen.duo.data.DatabaseOpener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Vector;

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

    // This function takes a uri (which will then be transformed into a URL
    // and connect to the Internet, and then download the data as a string
    public static String sendHTTPRequest(Uri uri) {

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

            if (inputStream == null) {return "";}
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {return "";}
            String result =  buffer.toString();

            return result;

            // TODO: (Utility) a better way to query data is to download the data to
            // SQLite and then query the data from SQLite, which means that the code
            // from this block should be put into another class, or this code should
            // be written in the way that it can be referenced from other classes
            // TODO: (Utility) to load and query data, create a SyncAdapter, with
            // Authenticator service

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Malformed URL: " + uri.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with url.openConnection()");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return "";
    }

    // This function takes a string, which is a serialized (JSON format), convert
    // it to JSON, and then return a list of ContentValues, which will be used to
    // update into the database
    public static Vector<ContentValues> getDataFromJSON(String rawString)
        throws JSONException{

        try {
            // The JSON data has the form of {"item": [json1, json2...]}
            JSONArray jsonFile = new JSONObject(rawString).getJSONArray("item");

//            Log.v("getDataFromJson", jsonFile.toString());
//            Log.v("getDataFromJson", "jsonFile length: " + String.valueOf(jsonFile.length()));
//            Log.v("getDataFromJson", "First item: " +  jsonFile.getJSONObject(0).toString());

            Vector<ContentValues> cvFiles = new Vector<ContentValues>(jsonFile.length());

            for (int i = 0; i < jsonFile.length(); i++) {

                JSONObject eachRow = jsonFile.getJSONObject(i);
                Iterator<?> keys = eachRow.keys();
                ContentValues eachResult = new ContentValues();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    eachResult.put(key, eachRow.getString(key));
                }
                cvFiles.add(eachResult);
            }
            return cvFiles;

        } catch (JSONException e) {
            Log.e("Utility", "JSONException");
        }

        return new Vector<ContentValues>();
    }

    private static class Name {

        // This class serves as a simple Queue to keep track
        // of the current temporarily viewed business

        private String[] tempArray;
        private int max_size;
        private int head1;
        private int head2;

        public Name(int size) {
            this.tempArray = new String[size];
            this.max_size = size;
            this.head1 = 0;
            this.head2 = 0;
        }

        public boolean add(String t) {
            try {
                if (isFull()) {
                    tempArray[head1] = t;
                    head1 = (head1 + 1) % max_size;
                    head2 = (head2 + 1) % max_size;
                } else {
                    tempArray[head1] = t;
                    head1 = (head1 + 1) % max_size;
                }
                return true;
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }
        }

        public void print() {
            String result = "[";

            if (!isFull()) {
                while (head1 != head2) {
                    result = result + tempArray[head2] + ", ";
                    head2 = (head2 + 1) % max_size;
                }
            } else {
                result = result + tempArray[head2] + ", ";
                head2 = (head2 + 1) % max_size;

                while (head1 != (head2 % this.max_size)) {
                    result = result + tempArray[head2] + ", ";
                    head2 = (head2 + 1) % max_size;
                }
            }

            if (result.length() > 1) {
                result = result.substring(0, result.length() - 2) + "]";
                System.out.println(result);
            } else {
                System.out.println("This Name is empty");
            }
        }

        public int has(String t) {

            // This method check if <t> is already in Name. If it
            // is in, then return the index of <t> in Name, otherwise
            // returns -1

            for (int dum_idx=0; dum_idx < max_size; dum_idx++) {
                if (t == tempArray[dum_idx]) {
                    return dum_idx;
                }
            }
            return -1;
        }

        public String get(int idx) {

            if ((0 <= idx) && (idx < max_size)) {
                return tempArray[idx];
            }

            return "";
        }


        private boolean isFull() {
            if (head1 == head2) {
                return true;
            } else {
                return false;
            }
        }
    }

    // This class automates inserting fake data for development and testing purpose
    public static class FakeData {

        public static void addBookmarkData(Context mContext) {

            DatabaseOpener mDatabaseOpener = new DatabaseOpener(mContext);
            SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();
            Uri link = Uri.parse("https://www.dropbox.com/s/nfvgps68p2597d1/bookmark1.json?dl=1");

            try {
                String result = sendHTTPRequest(link);
                Vector<ContentValues> data = getDataFromJSON(result);
                for (ContentValues eachRow: data) {
                    long row = db.insert(DataContract.BOOKMARK, null, eachRow);
                    Log.v("addBookmarkData", "Row: " + String.valueOf(row));
                }

            } catch (JSONException error) {
                Log.e("addBookmarData", "JSONException from addBookmarkData");
            } finally {
                db.close();
            }

            return;
        }

        public static void addSearchData(Context mContext) {

            DatabaseOpener mDatabaseOpener = new DatabaseOpener(mContext);
            SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();
            Uri link = Uri.parse("https://www.dropbox.com/s/qf4dr5ywlhp76ke/search.json?dl=1");

            try {
                String result = sendHTTPRequest(link);
                Vector<ContentValues> data = getDataFromJSON(result);
                for (ContentValues eachRow: data) {
                    long row = db.insert(DataContract.SEARCH, null, eachRow);
                    Log.v("addSearchData", "Row: " + String.valueOf(row));
                }

            } catch (JSONException error) {
                Log.e("addSearchData", "JSONException from addSearchData");
            } finally {
                db.close();
            }

            return;
        }

        public static void addDetailedData(Context mContext) {

            DatabaseOpener mDatabaseOpener = new DatabaseOpener(mContext);
            SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();
            Uri link = Uri.parse("https://www.dropbox.com/s/y8t7vax2cazquya/detailed.json?dl=1");

            try {
                String result = sendHTTPRequest(link);
                Vector<ContentValues> data = getDataFromJSON(result);
                for (ContentValues eachRow: data) {
                    long row = db.insert(DataContract.DETAILED, null, eachRow);
                    Log.v("addDetailedData", "Row: " + String.valueOf(row));
                }

            } catch (JSONException error) {
                Log.e("addDetailedData", "JSONException from addDetailedData");
            } finally {
                db.close();
            }

            return;
        }

        public static void addProductData(Context mContext) {

            DatabaseOpener mDatabaseOpener = new DatabaseOpener(mContext);
            mDatabaseOpener.createProdTable("harrypotter");
            SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();
            Uri link = Uri.parse("https://www.dropbox.com/s/1mup7x752s7x6ai/products.json?dl=1");

            try {
                String result = sendHTTPRequest(link);
                Vector<ContentValues> data = getDataFromJSON(result);
                for (ContentValues eachRow: data) {
                    long row = db.insert(DataContract.productsEntry.getTable("harrypotter"),
                            null, eachRow);
                    Log.v("addProductData", "Row: " + String.valueOf(row));
                }

            } catch (JSONException error) {
                Log.e("addProductData", "JSONException from addProductData");
            } finally {
                mDatabaseOpener.close();
            }

            return;
        }
    }
}
