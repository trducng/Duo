package com.ducnguyen.duo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * This class (DataProvider) serves as an interface for
 * Android ecosystem to access our database. Basically,
 * if we want to CRUD this database, we contact Android
 * ecosystem (w/ ContentResolver), then Android ecosystem
 * will use this ContentProvider to access the data
 */
public class DataProvider extends ContentProvider {

    // These constants define the kind of data that
    // this ContentProvider interacts with the database
    static final int EACH_BUSINESS = 100;
    static final int EACH_LOYALTY = 102;

    static final int SEARCH_OR_RECOMMENDATION = 200;
    static final int ALL_BOOKMARKS = 210;
    static final int EACH_BOOKMARK = 211;
    static final int ALL_LOYALTY = 220;
    static final int ALL_EVENTS = 230;
    static final int EACH_BUSINESS_PRODUCTS = 240;
    static final int EACH_BUSINESS_TESTIMONIALS = 241;


    // Helper to open and manage database
    private DatabaseOpener mDatabaseOpener;

    // Matcher helper to match URI to appropriate query
    private static final UriMatcher mUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mDatabaseOpener = new DatabaseOpener(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        final int match = mUriMatcher.match(uri);

        switch (match) {
            case EACH_BUSINESS:
                return DataContract.detailedEntry.CONTENT_ITEM_TYPE;
            case EACH_LOYALTY:
                return DataContract.loyaltyEntry.CONTENT_ITEM_TYPE;
            case SEARCH_OR_RECOMMENDATION:
                return DataContract.searchEntry.CONTENT_TYPE;
            case ALL_BOOKMARKS:
                return DataContract.bookmarkEntry.CONTENT_TYPE;
            case EACH_BOOKMARK:
                return DataContract.bookmarkEntry.CONTENT_TYPE;
            case ALL_LOYALTY:
                return DataContract.loyaltyEntry.CONTENT_TYPE;
            case ALL_EVENTS:
                return DataContract.eventsEntry.CONTENT_TYPE;
            case EACH_BUSINESS_PRODUCTS:
                return DataContract.productsEntry.CONTENT_TYPE;
            case EACH_BUSINESS_TESTIMONIALS:
                return DataContract.testimonialsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException(
                        "Unknown Uri: " + uri
                );
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor reCursor;

        switch(mUriMatcher.match(uri)) {
            case ALL_BOOKMARKS: {
                reCursor = mDatabaseOpener.getReadableDatabase()
                        .query(DataContract.BOOKMARK,
                                projection,
                                selection,
                                selectionArgs,
                                null, null,
                                sortOrder);
                break;
            }
            case ALL_EVENTS: {
                reCursor = mDatabaseOpener.getReadableDatabase()
                        .query(DataContract.EVENTS,
                                projection,
                                selection,
                                selectionArgs,
                                null, null,
                                sortOrder);
                break;
            }
            case ALL_LOYALTY: {
                reCursor = mDatabaseOpener.getReadableDatabase()
                        .query(DataContract.LOYALTY,
                                projection,
                                selection,
                                selectionArgs,
                                null, null,
                                sortOrder);
                break;
            }
            case EACH_BOOKMARK: {
                reCursor = returnEachBookmarkList(uri, projection, sortOrder);
                break;
            }
            case SEARCH_OR_RECOMMENDATION: {
                reCursor = mDatabaseOpener.getReadableDatabase()
                        .query(DataContract.SEARCH,
                                projection,
                                selection,
                                selectionArgs,
                                null, null,
                                sortOrder);
                break;
            }
            case EACH_BUSINESS: {
                reCursor = returnEachBusiness(uri);
                break;
            }
            case EACH_BUSINESS_PRODUCTS: {
                reCursor = returnBusProducts(uri);
                break;
            }
            case EACH_BUSINESS_TESTIMONIALS: {
                reCursor = returnBusTes(uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        reCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return reCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {

        // This buildUriMatcher function will be used to
        // create an UriMatcher object, which then be used
        // to recognize the type of Uri sent by the application

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.PACKAGE_NAME;

        // We match each possible Uri to a corresponding MIME type
        matcher.addURI(authority, DataContract.SEARCH, SEARCH_OR_RECOMMENDATION);
        matcher.addURI(authority, DataContract.BOOKMARK, ALL_BOOKMARKS);
        matcher.addURI(authority, DataContract.BOOKMARK + "/*", EACH_BOOKMARK);
        matcher.addURI(authority, DataContract.LOYALTY, ALL_LOYALTY);
        matcher.addURI(authority, DataContract.EVENTS, ALL_EVENTS);
        matcher.addURI(authority, DataContract.PRODUCTS + "/*", EACH_BUSINESS_PRODUCTS);
        matcher.addURI(authority, DataContract.TESTIMONIALS + "/*", EACH_BUSINESS_TESTIMONIALS);

        matcher.addURI(authority, DataContract.DETAILED + "/*", EACH_BUSINESS);
        matcher.addURI(authority, DataContract.BOOKMARK + "/*/*", EACH_BUSINESS);
        matcher.addURI(authority, DataContract.EVENTS + "/*", EACH_BUSINESS);
        matcher.addURI(authority, DataContract.LOYALTY + "/*", EACH_LOYALTY);

        return matcher;
    }

    private Cursor returnEachBookmarkList(Uri uri, String[] projection, String sortOrder) {

        // This function gets the URI and return the list of businesses bookmarked in a list
        // with a common listName
        String listName = DataContract.bookmarkEntry.getListName(uri);
        String condition = DataContract.bookmarkEntry.COL_LISTNAME + " = ?";
        Log.v("Provider", "Condition: " + condition);
        return mDatabaseOpener.getReadableDatabase().query(DataContract.BOOKMARK,
                            projection,
                            condition,
                            new String[]{listName},
                            null, null,
                            sortOrder);

    }

    private Cursor returnEachBusiness(Uri uri) {

        // This function gets the URI and return the specific business
        String busID = DataContract.detailedEntry.getBusID(uri);
        String condition = DataContract.detailedEntry.COLUMN_BUSID + " = ?";

        return mDatabaseOpener.getReadableDatabase()
                .query(DataContract.DETAILED,
                        null,
                        condition,
                        new String[]{busID},
                        null, null, null);
    }


    private Cursor returnBusProducts(Uri uri) {

        // This function gets the URI and returns all the products that
        // business has

        String busID = DataContract.productsEntry.getBusID(uri);
        String tableName = DataContract.productsEntry.getTable(busID);

        return mDatabaseOpener.getReadableDatabase()
                .query(tableName, null, null, null, null, null, null);
    }

    private Cursor returnBusTes(Uri uri) {

        String busID = DataContract.testimonialsEntry.getBusID(uri);
        String tableName = DataContract.testimonialsEntry.getTable(busID);

        return mDatabaseOpener.getReadableDatabase()
                .query(tableName, null, null, null, null, null, null);
    }
}
