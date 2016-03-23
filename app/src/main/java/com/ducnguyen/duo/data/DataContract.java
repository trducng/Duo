package com.ducnguyen.duo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created by ducnguyen on 3/7/16.
 * This class defines the contract between the application
 * and backend data resources, so that every component in
 * the application will have a consistent interface to interact
 * with the backend data.
 * More specifically, this DataContract class defines the field
 * and other relevant information for each of the table used
 * in the application
 */
public class DataContract {

    // Define the package name and base Uri, on which other Uris
    // will be built upon
    public static final String PACKAGE_NAME = "com.ducnguyen.duo";
    public static final Uri BASE_URI =
            Uri.parse("content://" + PACKAGE_NAME);

    // Define table names
    public static final String TAG = "tag";
    public static final String DETAILED = "detailed";
    public static final String PRODUCTS = "products";
    public static final String TESTIMONIALS = "testimonials";
    public static final String LOYALTY = "loyalty";
    public static final String SEARCH = "search";
    public static final String RECOMMENDATION = "recom";
    public static final String EVENTS = "events";


    // Define each column and each specific detail for each table
    public static final class bookmarkEntry implements BaseColumns {

        // Unique Uri for this table
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(TAG).build();

        // Unique MIME type for data from this table
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + TAG;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + TAG;

        // Constants for each columns
        public static final String COL_TAG = "tag";
        public static final String COL_BUSID = "busID";
        public static final String COL_BUSNAME = "busName";
        public static final String COL_BUSLOCATION = "busLocation";
        public static final String COL_BUSSERVICES = "busServices";
        public static final String COL_BUSCOVERIMAGE = "busCovImage";
        public static final String COL_LATITUDE = "lat";
        public static final String COL_LONGITUDE = "long";
        public static final String COL_TIMEADDED = "timeAdded";

        public static final String selecTags =
                "WHERE " + COL_TAG + " = ?";

        public static Uri buildGeneralBookmark() {

            // URI to call for list of all bookmark lists
            // content://com.ducnguyen.duo/tag
            return CONTENT_URI;
        }

        public static Uri buildSpecificBookmark(String[] listName) {

            // URI to call for a specific bookmark list
            // content://com.ducnguyen.duo/tag/<listName>

            String allTags = TextUtils.join(",", listName);
            return CONTENT_URI.buildUpon().appendPath(allTags).build();
        }

        public static Uri buildSingleBookmark(String tagName, String busID) {

            // URI to insert an entry to the Tag table
            // content://com.ducnguyen.duo/tag/<tagName>/<busID>
            return CONTENT_URI.buildUpon().appendPath(tagName)
                    .appendPath(busID).build();
        }


        public static String buildConditionalQuery(String[] allTags) {

            // Buid the query to retrieve items from SQLitedatabase
            // Based on the number of tags (allTags.length), construct
            // the query accordingly. Example: allTags = {"tag1", "tag2"}
            // then the outcome will be "TAG = ? OR TAG = ? "
            String baseString = COL_TAG + " = ? ";

            if (allTags.length == 1) {
                return baseString;
            } else {
                String finalString = baseString;
                for (int dummy_idx = 0;
                     dummy_idx < allTags.length - 1;
                     dummy_idx++) {

                    finalString += " OR ";
                    finalString += baseString;
                }
                return finalString;
            }
        }

        public static String[] getTagNames(Uri uri) {

            // This function returns the listName given a bookmark URI
            // content://com.ducnguyen.duo/bookmark/<listName>
            // will return <listName>
            // TODO: delete the variable, just return

            String[] segments = uri.getPath().split("/");
            String[] allTags = segments[segments.length - 1].split(",");

            return allTags;
        }

        /**
         * Takes an uri that is created by buildSingleBookmark(tagName, busID),
         * which will looks like content://com.ducnguyen.duo/tag/<tagName>/<busID>
         * and returns both tagName and busID.
         *
         * @param   uri     the URI created by buildSingleBookmark
         * @return          {tagName, busID}
         */
        public static String[] getTagNameAndBusID(Uri uri) {

            String[] segments = uri.getPath().split("/");
            return new String[] {   segments[segments.length-2],
                                    segments[segments.length-1] };
        }

    }

    public static final class detailedEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(DETAILED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + DETAILED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + DETAILED;

        public static final String COLUMN_SAVED = "colSaved";
        public static final String COLUMN_BUSID = "busID";
        public static final String COLUMN_BUSNAME = "busName";
        public static final String COLUMN_BUSPHONE = "busPhone";
        public static final String COLUMN_BUSLOCATION = "busLocation";
        public static final String COLUMN_BUSWEB = "busWeb";
        public static final String COLUMN_BUSEMAIL = "busEmail";
        public static final String COLUMN_BUSMESS = "busMess";
        public static final String COLUMN_BUSSERVICES = "busServices";
        public static final String COLUMN_BUSCOVERIMAGE = "busCovImage";
        public static final String COLUMN_BUSTEST = "busTestimonials";
        public static final String COLUMN_BUSNEWS = "busNews";

        public static Uri buildDetailedURI(String busID) {

            // This function creates URI to query specific business
            // content://com.ducnguyen.duo/detailed/<busID>

            return CONTENT_URI.buildUpon().appendPath(busID).build();
        }

        public static String getBusID(Uri uri) {

            // This function return the busID from URI

            String[] segments = uri.getPath().split("/");

            return segments[segments.length - 1];
        }
    }

    public static final class productsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(PRODUCTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + PRODUCTS;

        public static final String COLUMN_ITEMID = "itemID";
        public static final String COLUMN_ITEMNAME = "itemName";
        public static final String COLUMN_ITEMINFO = "itemInfo";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_ITEMPRICE = "itemPrice";
        public static final String COLUMN_ITEMIMAGE = "itemImg";

        public static Uri buildURI(String busID) {

            // This function builds the URI to query all products
            // of a given business
            // context://com.ducnguyen.duo/products/<busID>

            return CONTENT_URI.buildUpon().appendPath(busID).build();
        }

        public static String getBusID(Uri uri) {

            // This function returns the busID from a URI
            String[] segments = uri.getPath().split("/");
            return segments[segments.length - 1];
        }

        public static String getTable(String busID) {

            // This function returns the name of <busID>'s product table

            return PRODUCTS + "_" + busID;
        }
    }

    public static final class testimonialsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(TESTIMONIALS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + PACKAGE_NAME + "/" + TESTIMONIALS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + PACKAGE_NAME + "/" + TESTIMONIALS;

        public static final String COLUMN_COMTER = "commenter";
        public static final String COLUMN_COMTDETAIL = "comtDet";
        public static final String COLUMN_COMTDATE = "comtDate";
        public static final String COLUMN_REC = "rec";
        public static final String COLUMN_BUSID = "busID";

        public static Uri buildURI(String busID) {

            // This function returns the URI that point to the table
            // context://com.ducnguyen.duo/testimonials/<busID>

            return CONTENT_URI.buildUpon().appendPath(busID).build();
        }

        public static String getBusID(Uri uri) {

            // This function takes an URI and return the business ID
            // context://com.ducnguyen.duo/testimonials/<busID>
            // return <busID>

            String[] segments = uri.getPath().split("/");
            return segments[segments.length-1];
        }

        public static String getTable(String busID) {

            // This function returns the table for the specific busID
            return TESTIMONIALS + "_" + busID;
        }
    }

    public static final class loyaltyEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(PRODUCTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + LOYALTY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + LOYALTY;

        public static final String COLUMN_BUSID = "busID";
        public static final String COLUMN_BUSNAME = "busName";
        public static final String COLUMN_BUSCOVERIMGAGE = "busCovImg";
        public static final String COLUMN_CURPOINT = "curPoint";
        public static final String COLUMN_LOYALTYDETAIL = "loDet";

        public static Uri buildURI() {

            // This function returns the URI to query all loyalty
            // information
            // context://com.ducnguyen.duo/loyalty
            return CONTENT_URI;
        }
    }

    public static final class searchEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(SEARCH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + SEARCH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + SEARCH;

        public static final String COLUMN_BUSID = "busID";
        public static final String COLUMN_BUSNAME = "busName";
        public static final String COLUMN_BUSLOCATION = "busLocation";
        public static final String COLUMN_BUSSERVICES = "busServices";
        public static final String COLUMN_BUSCOVERIMAGE = "busCovImage";

        public static Uri buildURI() {

            // Return a URI to return all search results
            // content://com.ducnguyen.duo/search
            return CONTENT_URI;
        }
    }

    public static final class recommendEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(RECOMMENDATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + RECOMMENDATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + RECOMMENDATION;

        public static final String COL_BUSID = "busID";
        public static final String COL_BUSNAME = "busName";
        public static final String COL_BUSLOCATION = "busLocation";
        public static final String COL_BUSSERVICES = "busServices";
        public static final String COL_BUSCOVERIMAGE = "busCovImage";
        public static final String COL_DISTANCE = "dis";

        public static Uri buildURI() {

            // Return a URI to return all recommendation results
            // content://com.ducnguyen.duo/recom
            return CONTENT_URI;
        }
    }

    public static final class eventsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(EVENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + PACKAGE_NAME + "/" + EVENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + PACKAGE_NAME + "/" + EVENTS;

        public static final String COL_BUSID = "busID";
        public static final String COL_BUSNAME = "busName";
        public static final String COL_BUSEVENT = "busEvent";
        public static final String COL_BUSLOCATION = "busLoc";

        public static Uri buildURI() {

            // This function return URI to query all events
            // content://com.ducnguyen.duo/events

            return CONTENT_URI;
        }

    }
}
