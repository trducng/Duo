package com.ducnguyen.duo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
    static final int SINGLE_BOOKMARK = 101;

    static final int SEARCH = 200;
    static final int RECOMMENDATION = 201;
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
            case SEARCH:
                return DataContract.searchEntry.CONTENT_TYPE;
            case RECOMMENDATION:
                return DataContract.recommendEntry.CONTENT_TYPE;
            case ALL_BOOKMARKS:
                return DataContract.bookmarkEntry.CONTENT_TYPE;
            case EACH_BOOKMARK:
                return DataContract.bookmarkEntry.CONTENT_TYPE;
            case SINGLE_BOOKMARK:
                return DataContract.bookmarkEntry.CONTENT_ITEM_TYPE;
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
                        .query(DataContract.TAG,
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
            case SEARCH: {
                reCursor = mDatabaseOpener.getReadableDatabase()
                        .query(DataContract.SEARCH,
                                projection,
                                selection,
                                selectionArgs,
                                null, null,
                                sortOrder);
                break;
            }
            case RECOMMENDATION: {
                reCursor = mDatabaseOpener.getReadableDatabase()
                        .query(DataContract.RECOMMENDATION,
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

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri returnUri;

        switch (mUriMatcher.match(uri)) {
            case SINGLE_BOOKMARK: {
                long _id = mDatabaseOpener.getWritableDatabase().insert(
                                DataContract.TAG, null, values
                );
                if (_id > 0) {
                    returnUri = uri;
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case EACH_BUSINESS: {
                long _id = mDatabaseOpener.getWritableDatabase().insert(
                                DataContract.DETAILED, null, values
                );
                if (_id > 0) {
                    returnUri = uri;
                } else {
                    throw new SQLException("Failed to insert row int " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();

        switch(mUriMatcher.match(uri)) {
            case RECOMMENDATION: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(DataContract.RECOMMENDATION, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();;
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case SEARCH: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value: values) {
                        long _id = db.insert(DataContract.SEARCH, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();;
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }


    /**
     * This method will analyze which table should be affected from the uri, and then delete
     * rows that pass the selection and selectionArgs. To delete all rows in the table, pass
     * in null for both selection and selectionArgs.
     * @param uri               the uri that matches appropriate table
     * @param selection         the WHERE = ? condition to find the rows
     * @param selectionArgs     the range of values that match with selection
     * @return                  the number of rows deleted
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mDatabaseOpener.getWritableDatabase();
        int rowsDeleted;
        if (selection == null) selection = "1";

        switch (mUriMatcher.match(uri)) {
            case RECOMMENDATION: {
                rowsDeleted = db.delete(DataContract.RECOMMENDATION, selection, selectionArgs);
                break;
            }

            case SEARCH: {
                rowsDeleted = db.delete(DataContract.SEARCH, selection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    static UriMatcher buildUriMatcher() {

        // This buildUriMatcher function will be used to
        // create an UriMatcher object, which then be used
        // to recognize the type of Uri sent by the application

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DataContract.PACKAGE_NAME;

        // We match each possible Uri to a corresponding MIME type
        matcher.addURI(authority, DataContract.SEARCH, SEARCH);
        matcher.addURI(authority, DataContract.RECOMMENDATION, RECOMMENDATION);
        matcher.addURI(authority, DataContract.TAG, ALL_BOOKMARKS);
        matcher.addURI(authority, DataContract.TAG + "/*", EACH_BOOKMARK);
        matcher.addURI(authority, DataContract.TAG + "/*/*", SINGLE_BOOKMARK);
        matcher.addURI(authority, DataContract.LOYALTY, ALL_LOYALTY);
        matcher.addURI(authority, DataContract.EVENTS, ALL_EVENTS);
        matcher.addURI(authority, DataContract.PRODUCTS + "/*", EACH_BUSINESS_PRODUCTS);
        matcher.addURI(authority, DataContract.TESTIMONIALS + "/*", EACH_BUSINESS_TESTIMONIALS);

        matcher.addURI(authority, DataContract.DETAILED + "/*", EACH_BUSINESS);
        matcher.addURI(authority, DataContract.TAG + "/*/*", EACH_BUSINESS);
        matcher.addURI(authority, DataContract.EVENTS + "/*", EACH_BUSINESS);
        matcher.addURI(authority, DataContract.LOYALTY + "/*", EACH_LOYALTY);

        return matcher;
    }

    private Cursor returnEachBookmarkList(Uri uri, String[] projection, String sortOrder) {

        // This function gets the URI and return the list of businesses bookmarked in a list
        // with a common listName
        String[] allTags = DataContract.bookmarkEntry.getTagNames(uri);
        String condition = DataContract.bookmarkEntry.buildConditionalQuery(allTags);

        Log.v("Provider", "Condition: " + condition);

        return mDatabaseOpener.getReadableDatabase().query(DataContract.TAG,
                            projection,
                            condition,
                            allTags,
                            null, null,
                            sortOrder);

    }

    private Cursor returnEachBusiness(Uri uri) {

        // This function gets the URI and return the specific business
        String busID = DataContract.detailedEntry.getBusID(uri);
        String condition = DataContract.detailedEntry.COL_BUSID + " = ?";

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
