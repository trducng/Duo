package com.ducnguyen.duo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.ducnguyen.duo.data.DataContract;
import com.ducnguyen.duo.data.DatabaseOpener;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    // this number is used to adjust program verbosity (the higher the value, the more log outputs)
    // Verbosity <= 0: does not print anything
    // Verbosity == 1: print check-mark log (to see whether execution reaches that line of code)
    // Verbosity == 2: print example output (such as uri, string result)
    // Verbosity == 3: print periodical updates (such as location update)
    public static int VERBOSITY = -1;
    public static final boolean IMPORTANCE = true;

    // LOG_TAG will be as category name in Log output
    public static final String LOG_TAG = Utility.class.getSimpleName();

    // these store the basic path of URL and specify the type of request to Android SQLite
    public static final String URI_BASE = "http://www.google.com";
    public static final String URI_RECOMMEND = "recommend";
    public static final String URI_SEARCH = "search";
    public static final String URI_BUS = "bus";
    public static final String URI_TAG = "tag";
    public static final String URI_LATITUDE = "lat";
    public static final String URI_LONGITUDE = "long";
    public static final String URI_TIME = "time";
    // business url query parameters
    public static final String URI_BUSID = "busID";
    public static final String URI_BUS_KEY = "type";
    public static final String URI_INFO = "info";
    public static final String URI_PRODUCTS = "prods";
    public static final String URI_SCHEDULE = "sche";
    public static final String URI_DELIVERY = "del";
    public static final String URI_LOYALTY = "loy";
    public static final String URI_LOYALTY_MORE = "loyM";
    // search url query parameters
    public static final String URI_SEARCH_QUERY = "query";

    // these are types of extra questions in bus loyalty
    public static final String BUS_LOYAL_EXTRA_TEXT = "textbox";
    public static final String BUS_LOYAL_EXTRA_RADIO = "radiobox";
    public static final String BUS_LOYAL_EXTRA_CHECK = "checkbox";

    // these will be the code stored in JSON delivered from server
    public static final String CODE_MESSAGE = "message";
    public static final String CODE_PRODUCTS = "products";
    public static final String CODE_DELIVERY = "delivery";
    public static final String CODE_SCHEDULE = "calendar";
    public static final String CODE_LOYALTY = "loyalty";

    // these are the name of tabs
    public static final String TAB_BASIC_INFO = "Information";
    public static final String TAB_PRODUCTS = "Products";
    public static final String TAB_DELIVERY = "Delivery";
    public static final String TAB_SCHEDULE = "Reservation";
    public static final String TAB_LOYALTY = "Loyalty";

    // these are the name for database columns that have the same name
    public static final String COL_BUSID = URI_BUSID;
    public static final String COL_BUSNAME = "busName";
    public static final String COL_BUSLOCATION = "loc";
    public static final String COL_BUSSERVICES = "ser";
    public static final String COL_BUSCOVIMG = "covImg";
    public static final String COL_TYPE = "type";

    // these are the keys in SharedPreferences
    public static final String SAVED_BUSID = "saved";
    public static final String TEMP_BUSID_INFO = URI_INFO;
    public static final String TEMP_BUSID_PRODUCTS = URI_PRODUCTS;
    public static final String TEMP_BUSID_DELIVERY = URI_DELIVERY;
    public static final String TEMP_BUSID_SCHEDULE = URI_SCHEDULE;
    public static final String TEMP_BUSID_LOYALTY = URI_LOYALTY;
    public static final String TEMP_LAT = URI_LATITUDE;
    public static final String TEMP_LONG = URI_LONGITUDE;

    // these are the keys in BusInfo news' JSONObjects
    public static final String BUS_NEWS_TITLE = "title";
    public static final String BUS_NEWS_IMG = "img";
    public static final String BUS_NEWS_TEXT = "text";


    // this HashMap maps code with the appropriate name
    public static final HashMap<String, String> CODE_TO_NAME = new HashMap<String, String>();
    static {
                        CODE_TO_NAME.put(CODE_PRODUCTS, TAB_PRODUCTS);
                        CODE_TO_NAME.put(CODE_LOYALTY, TAB_LOYALTY);
                        CODE_TO_NAME.put(CODE_DELIVERY, TAB_DELIVERY);
                        CODE_TO_NAME.put(CODE_SCHEDULE, TAB_SCHEDULE);
    };

    // this HashMap maps tab item with the appropriate fragment -- this might not needed
    public static final Map<String, String> NAME_TO_DOWNLOAD = new HashMap<>();
    static {
                        NAME_TO_DOWNLOAD.put(TAB_BASIC_INFO, TEMP_BUSID_INFO);
                        NAME_TO_DOWNLOAD.put(TAB_PRODUCTS, TEMP_BUSID_PRODUCTS);
                        NAME_TO_DOWNLOAD.put(TAB_DELIVERY, TEMP_BUSID_DELIVERY);
                        NAME_TO_DOWNLOAD.put(TAB_SCHEDULE, TEMP_BUSID_SCHEDULE);
    };

    /**
     * This function builds the appropriate URI to request update to the server
     * considering the type of request:
     *      - recommendation: <URL_BASE>/<URL_RECOMMEND>?lat=lat&loc=loc&time=time
     *      - search: <URL_BASE>/<URL_SEARCH>?query=query?lat=lat&loc=loc
     *      - view business: <URL_BASE>/<URL_BUS>?busID=busID&busServices=a,b,c
     *      - download business info: <URL_BASE>/<URL_BUS>?busID=busID&type=<URL_INFO>
     *      - download business product: <URL_BASE>/<URL_BUS>?busID=busID&type=<URL_PRODUCTS>
     *      - download business delivery: <URL_BASE>/<URL_BUS>?busID=busID&type=<URL_DELIVERY>
     *      - download business schedule: <URL_BASE>/<URL_BUS>?busID=busID&type=<URL_SCHEDULE>
     * @param data      pair of key-value to be included in the URI (?key=value)
     * @return
     */
    public static Uri buildUri(String type, Map<String, String> data) {

        Uri.Builder query = Uri.parse(URI_BASE).buildUpon();
        Iterator iterator = data.entrySet().iterator();

        if (type.equals(URI_RECOMMEND) || type.equals(URI_SEARCH)) {
            query.appendPath(type);
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                query.appendQueryParameter((String) pair.getKey(), (String) pair.getValue());
            }
        } else if (type.equals(URI_BUS)) {
            query.appendPath(URI_BUS);
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                if (pair.getKey().equals(DataContract.bookmarkEntry.COL_BUSID)) {
                    query.appendQueryParameter((String) pair.getKey(), (String) pair.getValue());
                } else {
                    String value = pair.getValue().toString();
                    // string="calendar, delivery, message"
                    // to calendar-delivery-message
                    String[] values = value.split(", ");
                    value = TextUtils.join("-", values);
                    query.appendQueryParameter((String) pair.getKey(), value);
                }
            }
        }

        return query.build();
    }


    /**
     * This function gets the filetype of a particular file in a particular link.
     * For example if the links is www.abc.com/this/is/an/image.pdf, then it will
     * return filetype as pdf
     * @param url   the link
     * @return      the filetype
     */
    public static String getFileType(String url) {
        String[] paths = url.split("\\.");
        return paths[paths.length - 1];
    }

    public static LocationListener locationUri = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Map<String, String> userLoc = new HashMap<String, String>();
            userLoc.put(URI_LATITUDE, String.valueOf(location.getLatitude()));
            userLoc.put(URI_LONGITUDE, String.valueOf(location.getLongitude()));
            userLoc.put(URI_TIME, String.valueOf(location.getTime()));

            if (VERBOSITY >= 2) {
                Log.v("LOCATION Uri", String.valueOf(buildUri(URI_RECOMMEND, userLoc)));
            }
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

    public static LocationListener updateLocation = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            if (VERBOSITY >= 2) {
                Log.v("updateLocation", "The device's current position is: "
                        + String.valueOf(location.getLatitude())
                        + " latitude and "
                        + String.valueOf(location.getLongitude())
                        + " longitude.");
            }



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


    /**
     * This class handler performs updating locations, then sending <type>
     * requests to the server, receiving back the result and then populating
     * the appropriate fragment with that new result.
     *      For recommendation, The recommended intervals of updates is 5 minute
     * (30000 milliseconds) and 100 meters. This means after 5 minutes and 100 meters,
     * this LocationListener.onLocationChanged() will be call again to update
     * the Recommendation database.
     *
     * context       the context on which this LocationListener will be
     *                      initialized
     * type          the type of function that this LocationListener will
     * TODO: fill out onStatusChanged, onProviderEnabled, onProviderDisabled
     */
    public static class ProcessLocation implements  LocationListener {

        Context mContext;
        String mType;

        public ProcessLocation(Context context, String type) {
            mContext = context;
            mType = type;
        }
        @Override
        public void onLocationChanged(Location location) {
            if (mType.equals(URI_RECOMMEND)) {
                Map<String, String> userLoc = new HashMap<String, String>();
                userLoc.put(URI_LATITUDE, String.valueOf(location.getLatitude()));
                userLoc.put(URI_LONGITUDE, String.valueOf(location.getLongitude()));
                userLoc.put(URI_TIME, String.valueOf(location.getTime()));

                Uri query = buildUri(URI_RECOMMEND, userLoc);

//                updateDatabase(mContext, mType, query);

                new UpdateDatabase(mContext, query, mType).execute();
            }
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
    }


    /**
     * This function converts degrees to radians (which will be used to plug
     * into distance equation to get the distance between two pair lat-longs)
     * @param degree    the lat, long angle in degree
     * @return          the lat, long angle in radian
     */
    public static double toRadians(double degree) {
        return degree * (Math.PI) / 180;
    }


    /**
     * This function calculates the distance between two points, based on their
     * respective latitudes and longitudes. The latitudes and longitudes used
     * in this function are radians
     * @param lat1      the radians latitude of first location
     * @param long1     the radians longitude of first location
     * @param lat2      the radians latitude of second location
     * @param long2     the radians longitude of second location
     * @return          the distance between two locations in km
     */
    public static double getDistance(double lat1, double long1,
                                     double lat2, double long2) {

        double deltaLat = lat2 - lat1;
        double deltaLong = long2 - long1;

        double a = Math.pow((Math.sin(deltaLat/2)), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow((Math.sin(deltaLong/2)), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return c * 6371;
    }

    /**
     * This function formats the distance from getDistance(double, double,
     * double, double) with appropriate notation. More specifically, if
     * the distance is below < 10km, it will have format a.b km, if the
     * distance is below < 100km, it will have format ab.c km, if the distance
     * is larger than 100km, it will have format abc km
     * @param distance  the distance between two locations (from getDistance)
     * @return          the human friendly representation of the distance in km
     */
    public static String formatKM(double distance) {
        if (distance < 10) {
            String phrase = String.valueOf(distance);
            if (phrase.length() > 3) {
                return phrase.substring(0, 3) + " km";
            } else {
                return phrase + " km";
            }
        } else if (distance < 100) {
            String phrase = String.valueOf(distance);
            if (phrase.length() > 4) {
                return phrase.substring(0, 4) + " km";
            } else {
                return phrase + " km";
            }
        } else {
            String phrase = String.valueOf(distance).split("\\.")[0];
            return phrase + " km";
        }
    }

    // This funciton will view an image in an ImageView with a URL and download that
    // image to internal storgage (required for smooth scrolling)
    public static class ImageViewURL extends AsyncTask<String, Void, Bitmap> {

        // ImageView to immediately download and view the image
        ImageView bmImage;

        // These variables help save the image to internal storage
        String fileName;
        String filePath;
        Context mContext;

        // This variable check if the file already exists
        boolean hasFile;

        public ImageViewURL(Context context, ImageView bmImage,
                            String businessID, String type,
                            String fileType) {

            this.bmImage = bmImage;
            this.mContext = context;
            this.fileName = type + "-" + businessID + "." + fileType;
            this.filePath = mContext.getFilesDir() + "/" + fileName;

            if (fileExist()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
                bmImage.setImageBitmap(myBitmap);
            }

        }

        protected Bitmap doInBackground(String... urls) {

            if (hasFile) {
                // if the internal storage already has this file,
                // then the image is already set
                return null;
            }

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("ImageViewURL", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            // Break early when hasFile, it means that the imageview is already set
            if (hasFile) {
                return;
            }

            bmImage.setImageBitmap(result);

            // And then save that image to file so that the next time
            // you don't need to download the image back again
            // TODO file ways to delete files after use
            FileOutputStream fos = null;
            try {
                fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                result.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            } catch (FileNotFoundException e) {
                Log.e("ImageViewURL", "File not found exception: " + e.getMessage());
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e("ImageViewURL", "Error closing stream: " + e.getMessage());
                    }
                }
            }
        }

        private boolean fileExist() {

            File file = new File(filePath);
            if (file.exists()) {
                hasFile = true;
                return true;
            } else {
                hasFile = false;
                return false;
            }
        }
    }

    // This function will download an image to the internal storage system
    public static class DownloadImage extends AsyncTask<String, Void, InputStream> {

        String busID;
        String funcType;
        Context mContext;

        public DownloadImage(Context context, String businessID, String type) {
            super();
            mContext = context;
            busID = businessID;
            funcType = type;
        }

        protected InputStream doInBackground(String... urls) {
            String urldisplay = urls[0];
            InputStream input = null;
            try {
                input = new java.net.URL(urldisplay).openStream();
            } catch (Exception e) {
                Log.e("DownloadImage", e.getMessage());
                e.printStackTrace();
            }
            return input;
        }

        protected void onPostExecute(InputStream input)  {

            FileOutputStream output = null;

            try {
                String outputName = funcType + " - " + busID + ".png";
                output = mContext.openFileOutput(outputName, Context.MODE_PRIVATE);

                int read;
                byte[] data = new byte[1024];
                while ((read = input.read(data)) != -1) {
                    output.write(data, 0, read);
                }

            } catch (FileNotFoundException e) {
                Log.e("U.DownloadImage", "FileNotFoundException: " + e.getMessage());
            } catch (IOException e) {
                Log.e("U.DownloadImage", "IOException: " + e.getMessage());
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    Log.e("U.DownloadImage", "IOException: " + e.getMessage());
                }
            }
        }
    }

    // This function takes a uri (which will then be transformed into a URL
    // and connect to the Internet, and then download the data as a string
    public static class UpdateDatabase extends AsyncTask<String, Void, Void> {

        Uri uri;
        String queryType;
        Context mContext;

        public UpdateDatabase(Context context, Uri uri, String type) {

            // uri to query server:
            // for bookmark: http://app.duo.io/rec?lat=<lat>&loc=<loc>&userID=<userID>
            // for search: http://app.duo.io/search?query=<query>&loc=<loc>
            // to view each business: http://app.duo.io/bus?busID=<busID>
            // The query type is either "bookmark", "search" or "business"
            this.uri = uri;
            this.queryType = type;
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            updateDatabase(mContext, queryType, uri);
            return null;
        }
    }

    /**
     * This function takes an URI to fetch data and then automatically insert
     * that data to the right SQLiteDatabase table.
     * TODO: when working with products and schedule tables.., consider reduce this function code
     * @param context   the context of the application when this
     * @param type      the type of request: URI_SEARCH, URI_RECOMMEND or URI_BUS
     * @param uri       the uri to connect to server
     */
    public static void updateDatabase(Context context, String type, Uri uri) {

        if (type.equals(URI_RECOMMEND)) {
            uri = Uri.parse("https://www.dropbox.com/s/6hmu5ekt4ntx46t/rec.json?dl=1");

            String result = sendHTTPRequest(uri);
            Vector<ContentValues> data;
            try {
                data = getDataFromJSON(result, null);
                if (data.size() > 0) {
                    ContentValues[] insertData = new ContentValues[data.size()];
                    data.toArray(insertData);

                    // Delete the old data here to avoid building up data
                    context.getContentResolver().delete(
                            DataContract.recommendEntry.buildURI(),
                            null, null
                    );

                    // Bulk insert the new data
                    context.getContentResolver().bulkInsert(
                            DataContract.recommendEntry.buildURI(),
                            insertData
                    );
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
            }
        } else if (type.equals(URI_SEARCH)) {
            uri = Uri.parse("https://www.dropbox.com/s/qf4dr5ywlhp76ke/search.json?dl=1");
            String result = sendHTTPRequest(uri);
            Vector<ContentValues> data;
            try {
                data = getDataFromJSON(result, null);
                if (data.size() > 0) {
                    ContentValues[] insertData = new ContentValues[data.size()];
                    data.toArray(insertData);

                    // Delete the old data here to avoid building up data
                    context.getContentResolver().delete(
                            DataContract.searchEntry.buildURI(),
                            null, null
                    );

                    // Bulk insert the new data
                    context.getContentResolver().bulkInsert(
                            DataContract.searchEntry.buildURI(),
                            insertData
                    );

                }

            } catch (JSONException e) {
                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
            }
        } else if (type.equals(URI_INFO)) {
            uri = Uri.parse("https://www.dropbox.com/s/z4ptoirxjaueyvz/busD103.json?dl=1");
            uri = Uri.parse("https://www.dropbox.com/s/haa4vrqc0u9o07w/busD103-right.json?dl=1");
            Log.v(LOG_TAG + ".updateDatabase", "URI_INFO for D103 is downloaded");
            Log.v(LOG_TAG, "Url: " + uri.toString());
            String busID = uri.getQueryParameter(URI_BUSID);
            busID = "RecD103";
            String result = sendHTTPRequest(uri);
            Vector<ContentValues> data;
            try {
                data = getDataFromJSON(result, null);

                // since it retrieves specific business, data length should always be 1
                if (data.size() == 1) {
                    ContentValues insertData = data.get(0);
                    // for testing purpose only, to avoid building up data
                    int i = context.getContentResolver().delete(
                            DataContract.detailedEntry.buildDetailedURI(busID),
                            null, null);
                    context.getContentResolver().delete(
                            DataContract.detailedEntry.buildDetailedURI("D103"),
                            null, null);
                    Log.v(LOG_TAG, "Deleted: " + i);
                    context.getContentResolver().insert(
                            DataContract.detailedEntry.buildDetailedURI(busID),
                            insertData);

                }
            } catch (JSONException e) {
                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
            }
        } else if (type.equals(URI_LOYALTY)) {
            uri = Uri.parse("https://www.dropbox.com/s/g2p6q1y9d2y7y2p/loy.json?dl=1");
            Log.v(LOG_TAG + ".updateDatabase", "URI_LOYALTY for D103 is downloaded");
            Log.v(LOG_TAG, "Url: " + uri.toString());
            String busID = uri.getQueryParameter(URI_BUSID);
            busID = "RecD103";
            String result = sendHTTPRequest(uri);
            Vector<ContentValues> data;
            try {
                data = getDataFromJSON(result, new String[] {COL_BUSID});

                if (data.size() > 0) {
                    ContentValues[] insertData = new ContentValues[data.size()];
                    data.toArray(insertData);

                    // for testing purpose only, to avoid building up data
                    // TODO: implement the queue system to delete old data
                    int i = context.getContentResolver().delete(
                            DataContract.loyaltyDetailEntry.buildURI(busID),
                            null, null);
                    Log.v(LOG_TAG, "Deleted: " + i);
                    context.getContentResolver().bulkInsert(
                            DataContract.loyaltyDetailEntry.buildURI(busID),
                            insertData);

                }

            } catch (JSONException e) {
                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
            }
        } else if (type.equals(URI_LOYALTY_MORE)) {
            uri = Uri.parse("https://www.dropbox.com/s/b2zxqnxci0js5rz/loyMoreD103.json?dl=1");
            Log.v(LOG_TAG + ".updateDatabase", "URI_LOYALTY_MORE for D103 is downloaded");
            Log.v(LOG_TAG, "Url: " + uri.toString());
            String busID = uri.getQueryParameter(URI_BUSID);
            busID = "RecD103";
            String result = sendHTTPRequest(uri);
            Vector<ContentValues> data;
            try {
                data = getDataFromJSON(result, new String[] {COL_BUSID});

                // since it retrieves specific business, data length should always be 1
                if (data.size() > 0) {
                    ContentValues[] insertData = new ContentValues[data.size()];
                    data.toArray(insertData);


                    // This deletion is to avoid building up large amount of data
                    // TODO: implement queue system
                    int i = context.getContentResolver().delete(
                            DataContract.loyaltyDetailEntry.buildURI(busID),
                            COL_BUSID + " = ? AND " + COL_TYPE + " = ?",
                            new String[] {busID, "5"}
                    );

                    Log.v(LOG_TAG, "Deleted button: " + i);

                    context.getContentResolver().bulkInsert(
                            DataContract.loyaltyDetailEntry.buildURI(busID),
                            insertData);


                }
            } catch (JSONException e) {
                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
            }
        }
//        } else if (type.equals(URI_PRODUCTS)) {
//            String busID = uri.getQueryParameter(URI_BUSID);
//            String result = sendHTTPRequest(uri);
//            Vector<ContentValues> data;
//            try {
//                data = getDataFromJSON(result);
//
//                // since it retrieves specific business, data length should always be 1
//                if (data.size() > 0) {
//                    ContentValues insertData = data.get(0);
//                    context.getContentResolver().insert(
//                            DataContract.productsEntry.buildURI(busID),
//                            insertData
//                    );
//                }
//            } catch (JSONException e) {
//                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
//            }
//        } else if (type.equals(URI_DELIVERY)) {
//            String busID = uri.getQueryParameter(URI_BUSID);
//            String result = sendHTTPRequest(uri);
//            Vector<ContentValues> data;
//            try {
//                data = getDataFromJSON(result);
//
//                // since it retrieves specific business, data length should always be 1
//                if (data.size() > 0) {
//                    ContentValues insertData = data.get(0);
//                    context.getContentResolver().insert(
//                            DataContract.productsEntry.buildURI(busID),
//                            insertData
//                    );
//                }
//            } catch (JSONException e) {
//                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
//            }
//        } else if (type.equals(URI_SCHEDULE)) {
//            String busID = uri.getQueryParameter(URI_BUSID);
//            String result = sendHTTPRequest(uri);
//            Vector<ContentValues> data;
//            try {
//                data = getDataFromJSON(result);
//
//                // since it retrieves specific business, data length should always be 1
//                if (data.size() > 0) {
//                    ContentValues insertData = data.get(0);
//                    context.getContentResolver().insert(
//                            DataContract.productsEntry.buildURI(busID),
//                            insertData
//                    );
//                }
//            } catch (JSONException e) {
//                Log.e(LOG_TAG + ".updateDatabase", "JSONException: " + e.getMessage());
//            }
//        }
    }


    /**
     * This helper function helps delete file that has the name <fileName> in
     * the internal storage.
     * @param context   the context from which this function is called
     * @param fileName  the name of the file to be deleted (with proper extension)
     * @return          true if the file is deleted and false otherwise
     */
    public static boolean deleteFile(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        boolean deleted = file.delete();
        return deleted;
    }


    /**
     * This function takes an URI and returns a string version of a JSONObject
     * (the stringed JSONObject will then be put into getDataFromJSON to extract
     * ContentValues object to store in the database).
     * @param uri       the uri request to server
     * @return          a stringed JSONObject that wil be used to fetch into
     *                  getDataFromJSON
     */
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
            Log.e(LOG_TAG, "Error with url.openConnection(): " + e.getMessage());
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
    public static Vector<ContentValues> getDataFromJSON(String rawString,
                                                        String[] extraKeys)
        throws JSONException{

        try {

            JSONObject originalFile = new JSONObject(rawString);
            // The JSON data has the form of {"item": [json1, json2...]}
            JSONArray jsonFile = originalFile.getJSONArray("item");

//            Log.v("getDataFromJson", jsonFile.toString());
//            Log.v("getDataFromJson", "jsonFile length: " + String.valueOf(jsonFile.length()));
//            Log.v("getDataFromJson", "First item: " +  jsonFile.getJSONObject(0).toString());

            Vector<ContentValues> cvFiles = new Vector<>(jsonFile.length());

            for (int i = 0; i < jsonFile.length(); i++) {

                JSONObject eachRow = jsonFile.getJSONObject(i);
                Iterator<?> keys = eachRow.keys();
                ContentValues eachResult = new ContentValues();

                // Get the value from extraKeys (outside of "item")
                if (extraKeys != null) {
                    for (String extraKey: extraKeys) {
                        eachResult.put(extraKey, originalFile.getString(extraKey));
                    }
                }

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    eachResult.put(key, eachRow.getString(key));
                }
                cvFiles.add(eachResult);
            }
            return cvFiles;

        } catch (JSONException e) {
            Log.e("getDataFromJSON", "JSONException: " + e.getMessage());
        }

        return new Vector<ContentValues>();
    }


    /**
     * This class serves as a simple Queue to keep track of current
     * temporarily viewed business.
     * When instantialized, remember to pass in the maximum numbers
     * of data will be stored. This instance can be serialized into
     * String in order to save to SharedPreferences
     */
    public static class Name implements Serializable {

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

        /**
         * Add a string element to the queue and will automatically delete
         * the oldest element. Remember to check whether this t element is
         * already in the Queue using Name.has(String t)
         * @param t     the element to be added
         * @return      the deleted element, or null if no element is added
         *              or if total number of elements does not reach max
         */
        public String add(String t) {
            try {
                if (isFull()) {
                    String deleted = tempArray[head1];
                    tempArray[head1] = t;
                    head1 = (head1 + 1) % max_size;
                    head2 = (head2 + 1) % max_size;
                    return deleted;
                } else {
                    tempArray[head1] = t;
                    head1 = (head1 + 1) % max_size;
                }

            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

        /**
         * This helper function prints all element in a Name instance, the
         * printed result looks like [element1, element2, element3]
         */
        public String print() {
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
                return result;
            } else {
                System.out.println("This Name is empty");
                return "This Name is empty";
            }
        }

        /**
         * Check if a Name instance contains element t.
         * @param t     the element to check
         * @return      if t is in the instance, return the index, otherwise
         *              return -1. Note that the index is not in chronological
         *              order of element t, but instead it is a spacial order
         *              of t in the Name instance
         */
        public int has(String t) {

            for (int dum_idx=0; dum_idx < max_size; dum_idx++) {
                if (t == tempArray[dum_idx]) {
                    return dum_idx;
                }
            }
            return -1;
        }

        /**
         * Get the element by index
         * @param idx   the index of element, this index should comes from
         *              Name.has(String t) to guarantee correct result. This
         *              is stupid, if you know String t then you don't need
         *              this method (has to fix this in some way)
         * @return      the value that has index String t
         */
        public String get(int idx) {

            if ((0 <= idx) && (idx < max_size)) {
                return tempArray[idx];
            }

            return "";
        }

        /**
         * Check if the Name instance is full. In otherword, check if
         * it contains the maximum amount of possible elements
         * @return      true if it is full, false otherwise
         */
        private boolean isFull() {
            if (head1 == head2) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * This function reads a string and reconstruct the data
         * @param input                     a string which holds information to reconstruct data
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
            tempArray = (String[]) input.readObject();
            max_size = input.readInt();
            head1 = input.readInt();
            head2 = input.readInt();
        }

        /**
         * This function turns an instance into a string
         * @param output
         * @throws IOException
         */
        private void writeObject(ObjectOutputStream output) throws IOException {
            output.writeObject(tempArray);
            output.writeInt(max_size);
            output.writeInt(head1);
            output.writeInt(head2);
        }
    }

    /**
     * This function serializes an object to string
     * @param object    an object that has Serializable implemented
     * @return          a string representation of that object
     */
    public static String serializeToString(Object object) {

        ByteArrayOutputStream byteInput;
        ObjectOutputStream output = null;

        try {
            byteInput = new ByteArrayOutputStream();
            output = new ObjectOutputStream(byteInput);
            output.writeObject(object);
            return Base64.encodeToString(byteInput.toByteArray(),
                    Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(LOG_TAG+".serializeToString",
                  "IOException during serialization: " + e.getMessage());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG + ".serializeToString",
                          "IOException during closing: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * This function deserializes an object from string
     * @param ser       a serialized string that will be decoded
     * @return          an object that represented by original string
     */
    public static Object deserializeFromString(String ser) {

        byte[] data = Base64.decode(ser, Base64.DEFAULT);
        ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
        ObjectInputStream input = null;

        try {
            input = new ObjectInputStream(byteInput);
            Object des = input.readObject();
            return des;
        } catch (IOException e) {
            Log.e(LOG_TAG + ".deserializeFromString",
                    "IOException during deserialize: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e(LOG_TAG + ".deserializeFromString",
                  "ClassNotFoundException during deserialization: " +
                  e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG + ".deserializeFromString",
                          "IOException during closing stream: " +
                          e.getMessage());
                }
            }
        }
        return null;
    }



    // The below functions and classes are for testing and development purposes

    // This class automates inserting fake data for development and testing purpose
    public static class FakeData {

        public static void addTagData(Context mContext) {

            DatabaseOpener mDatabaseOpener = new DatabaseOpener(mContext);
            SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();
            Uri link = Uri.parse("https://www.dropbox.com/s/wggy2gnorva5hur/bookmark.json?dl=1");

            try {
                String result = sendHTTPRequest(link);
                Vector<ContentValues> data = getDataFromJSON(result, null);
                for (ContentValues eachRow: data) {
                    long row = db.insert(DataContract.TAG, null, eachRow);
                    if (VERBOSITY >= 2) Log.v("addTagData", "Row: " + String.valueOf(row));
                }

            } catch (JSONException error) {
                Log.e("addTagData", "JSONException from addTagData");
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
                Vector<ContentValues> data = getDataFromJSON(result, null);
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
                Vector<ContentValues> data = getDataFromJSON(result, null);
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
                Vector<ContentValues> data = getDataFromJSON(result, null);
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

    public class userTest {

        private int birthYear;
        private String fullName;
        public userTest() {}
        public userTest(String name, int age) {
            this.birthYear = age;
            this.fullName = name;
        }
        public long getOlala() {
            return birthYear;
        }
        public String getajinomoto() {
            return fullName;
        }
    }

    public static void testFirebase(Context mContext, boolean createUser) {

        // Firebase
        Firebase.setAndroidContext(mContext);
        Firebase myFirebase = new Firebase("https://dazzling-inferno-9231.firebaseio.com/");


        if (createUser) {

            myFirebase.child("message").setValue("Do you have data? You'll love Firebase.");

            myFirebase.createUser("abcd1234@gmail.com", "1122", new Firebase.ValueResultHandler<Map<String, Object>>() {
                public void onError(FirebaseError firebaseError) {
                    Log.v("Firebase", "Problem creating user abcd1234@gmail.com");
                }
                public void onSuccess(Map<String, Object> auth) {
                    Log.v("Firebase", "Created user with uid: " + auth.get("uid"));
                }
            });
        } else {
            myFirebase.authWithPassword("abcd1234@gmail.com", "1122", new Firebase.AuthResultHandler(){
                public void onAuthenticated(AuthData authData) {
                    Log.v("Firebase", "Logged in with uid: " + authData.getUid()
                            + " and Provider: " + authData.getProvider());
                }
                public void onAuthenticationError(FirebaseError error) {
                    Log.v("Firebase", "Authentication error: " + String.valueOf(error));
                }
            });

            Firebase edit = myFirebase.child("user9");
            Utility utility = new Utility();
            Utility.userTest test = utility.new userTest("MaiDuc", 44);
            edit.setValue(test, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Log.v("Firebase", "Data could not be saved. " + firebaseError.getMessage());
                    } else {
                        Log.v("Firebase", "Data saved successfully.");
                    }
                }
            });
        }
    }

    public static void testData(Context mContext, boolean addData) {

        if (addData) {

            // Pick 1 depending on the type of table
//            Utility.FakeData.addTagData(mContext);
//            Utility.FakeData.addSearchData(mContext);
//            Utility.FakeData.addDetailedData(mContext);
            Utility.FakeData.addProductData(mContext);
        } else {
            Cursor test = mContext.getContentResolver().query(
                    DataContract.productsEntry.buildURI("harrypotter"),
                    null, null, null, null);

            while (test.moveToNext()) {
                Log.v("Test ContentProvider", "itemID: " + test.getString(test.getColumnIndex("itemID")));
            }
            test.close();
        }
    }

}
