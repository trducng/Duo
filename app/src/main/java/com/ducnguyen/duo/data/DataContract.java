package com.ducnguyen.duo.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

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
    public static final String BOOKMARK = "bookmark";
    public static final String DETAILED = "detailed";
    public static final String PRODUCTS = "products";
    public static final String TESTIMONIALS = "testimonials";
    public static final String LOYALTY = "loyalty";
    public static final String SEARCH = "search";


    // Define each column and each specific detail for each table
    public static final class bookmarkEntry implements BaseColumns {

        // Unique Uri for this table
        public static final Uri CONTENT_URI =
                BASE_URI.buildUpon().appendPath(BOOKMARK).build();

        // Unique MIME type for data from this table
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + BOOKMARK;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + PACKAGE_NAME + "/" + BOOKMARK;

        // Constants for each columns
        public static final String COLUMN_LISTNAME = "listName";
        public static final String COLUMN_BUSID = "busID";
        public static final String COLUMN_BUSNAME = "busName";
        public static final String COLUMN_BUSPHONE = "busPhone";
        public static final String COLUMN_BUSLOCATION = "busLocation";
        public static final String COLUMN_BUSSERVICES = "busServices";
        public static final String COLUMN_BUSCOVERIMAGE = "busCovImage";
        public static final String COLUMN_TIMEADDED = "timeAdded";

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
        public static final String COLUMN_BUSSERVICES = "busService";
        public static final String COLUMN_BUSCOVERIMAGE = "busCovImage";
        public static final String COLUMN_BUSTEST = "busTestimonials";
        public static final String COLUMN_BUSNEWS = "busNews";
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
        public static final String COLUMN_BUSID = "busID";
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
        public static final String COLUMN_BUSLOCATION = "busLocation";        public static final String COLUMN_BUSSERVICES = "busServices";
        public static final String COLUMN_BUSCOVERIMAGE = "busCovImage";
    }
}
