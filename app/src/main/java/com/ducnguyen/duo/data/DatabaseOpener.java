package com.ducnguyen.duo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ducnguyen.duo.data.DataContract.bookmarkEntry;
import com.ducnguyen.duo.data.DataContract.detailedEntry;
import com.ducnguyen.duo.data.DataContract.eventsEntry;
import com.ducnguyen.duo.data.DataContract.productsEntry;
import com.ducnguyen.duo.data.DataContract.searchEntry;
import com.ducnguyen.duo.data.DataContract.testimonialsEntry;
import com.ducnguyen.duo.data.DataContract.loyaltyEntry;
import com.ducnguyen.duo.data.DataContract.recommendEntry;
import com.ducnguyen.duo.data.DataContract.loyaltyDetailEntry;


/**
 * This class provides a simple interface to open and manage
 * different versions of database
 */
public class DatabaseOpener extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8;

    static final String DATABASE_NAME = "duo.db";

    public DatabaseOpener(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create each of the table with the appropriate
        // column in DataContract

        Log.v("DatabaseOpener", "In onCreate beginning to create table");

        // Create tag table

        final String SQL_CREATE_TAG_TABLE = "CREATE TABLE "
                + DataContract.TAG + " ("
                + bookmarkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + bookmarkEntry.COL_TAG + " TEXT NOT NULL, "
                + bookmarkEntry.COL_BUSID + " TEXT NOT NULL, "
                + bookmarkEntry.COL_NAME + " TEXT NOT NULL, "
                + bookmarkEntry.COL_LOC + " TEXT NOT NULL, "
                + bookmarkEntry.COL_SERVS + " TEXT, "
                + bookmarkEntry.COL_CIMG + " TEXT, "
                // TODO: COL_TIMEADDED should be " INTEGER NOT NULL "
                // change to TEXT NOT NULL just for testing
                + bookmarkEntry.COL_LAT + " REAL NOT NULL, "
                + bookmarkEntry.COL_LONG + " REAL NOT NULL, "
                + bookmarkEntry.COL_TIMEADDED + " TEXT NOT NULL "
                + " );";

        // Create business table
        final String SQL_CREATE_DETAILED_TABLE = "CREATE TABLE "
                + DataContract.DETAILED + " ("
                + detailedEntry.COL_SAVED + " INTEGER NOT NULL, "
                + detailedEntry.COL_BUSID + " TEXT UNIQUE NOT NULL, "
                + detailedEntry.COL_NAME + " TEXT NOT NULL, "
                + detailedEntry.COL_SHORTLOC + " TEXT NOT NULL, "
                + detailedEntry.COL_LOC + " TEXT NOT NULL, "
                + detailedEntry.COL_OPEN + " TEXT NOT NULL, "
                + detailedEntry.COL_CONTACT + " TEXT NOT NULL, "
                + detailedEntry.COL_IMG + " TEXT NOT NULL, "
                + detailedEntry.COL_HOURS + " TEXT NOT NULL, "
                + detailedEntry.COL_NEWS + " TEXT, "
                + detailedEntry.COL_LOY + " TEXT "
                + " );";

        // Create loyalty table
        final String SQL_CREATE_LOYALTY_TABLE = "CREATE TABLE "
                + loyaltyEntry.COL_CIMG + " TEXT, "
                + loyaltyEntry.COL_BUSID + " TEXT NOT NULL, "
                + loyaltyEntry.COL_NAME + " TEXT NOT NULL, "
                + loyaltyEntry.COL_CURPOINT + " INTEGER NOT NULL, "
                + loyaltyEntry.COL_FAVOURITE + " TEXT NOT NULL "
                + ");";

        // Create loyalty specific table
        final String SQL_CREATE_LOYALTY_DET_TABLE = "CREATE TABLE "
                + DataContract.LOYALTY_DETAIL + " ("
                + loyaltyDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + loyaltyDetailEntry.COL_BUSID + " TEXT NOT NULL, "
                + loyaltyDetailEntry.COL_GREETING + " TEXT, "
                + loyaltyDetailEntry.COL_MESSAGE + " TEXT, "
                + loyaltyDetailEntry.COL_IMG + " TEXT, "
                + loyaltyDetailEntry.COL_ITEM + " TEXT, "
                + loyaltyDetailEntry.COL_ITEM_DESC + " TEXT, "
                + loyaltyDetailEntry.COL_PTS + " INTEGER, "
                + loyaltyDetailEntry.COL_TYPE + " INTEGER "
                + " );";

        // Create search table
        final String SQL_CREATE_SEARCH_TABLE = "CREATE TABLE "
                + DataContract.SEARCH + " ("
                + searchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + searchEntry.COL_BUSID + " TEXT NOT NULL, "
                + searchEntry.COL_NAME + " TEXT NOT NULL, "
                + searchEntry.COL_LOC + " TEXT NOT NULL, "
                + searchEntry.COL_SERVS + " TEXT NOT NULL, "
                + searchEntry.COL_CIMG + " TEXT NOT NULL, "
                + searchEntry.COL_DISTANCE + " REAL NOT NULL "
                + ");";

        // Create recommendation table
        final String SQL_CREATE_REC_TABLE = "CREATE TABLE "
                + DataContract.RECOMMENDATION + " ("
                + recommendEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + recommendEntry.COL_BUSID + " TEXT NOT NULL, "
                + recommendEntry.COL_NAME + " TEXT NOT NULL, "
                + recommendEntry.COL_LOC + " TEXT NOT NULL, "
                + recommendEntry.COL_SERVS + " TEXT NOT NULL, "
                + recommendEntry.COL_CIMG + " TEXT NOT NULL, "
                + recommendEntry.COL_DISTANCE + " REAL NOT NULL "
                + ");";

        // Create events table
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE "
                + DataContract.EVENTS + " ("
                + eventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + eventsEntry.COL_BUSID + " TEXT NOT NULL, "
                + eventsEntry.COL_NAME + " TEXT NOT NULL, "
                + eventsEntry.COL_BUSEVENT + " TEXT NOT NULL, "
                + eventsEntry.COL_LOC + " TEXT NOT NULL "
                + ");";

//        // Create the tables
        db.execSQL(SQL_CREATE_SEARCH_TABLE);
        db.execSQL(SQL_CREATE_DETAILED_TABLE);
//        db.execSQL(SQL_CREATE_LOYALTY_TABLE);
//        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
//        db.execSQL(SQL_CREATE_TESTIMONIALS_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_TAG_TABLE);
        db.execSQL(SQL_CREATE_REC_TABLE);
        db.execSQL(SQL_CREATE_LOYALTY_DET_TABLE);
        Log.v("DatabaseOpener", "Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Delete the old tables
        // TODO: must have some way to retain DETAILED, PRODUCTS
        // TESTIMONIALS and LOYALTY data
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TAG);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.DETAILED);
//        db.execSQL("DROP TABLE IF EXISTS " + DataContract.PRODUCTS);
//        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TESTIMONIALS);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.LOYALTY);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.SEARCH);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.RECOMMENDATION);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.LOYALTY_DETAIL);

        // Create the new tables
        onCreate(db);
    }


    // For business product table
    public void createProdTable(String busID) {
        String tableName = productsEntry.getTable(busID);

        // Create products table
        final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + tableName + " ("
                + productsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + productsEntry.COL_ITEMID + " TEXT NOT NULL, "
                + productsEntry.COL_ITEMNAME + " TEXT NOT NULL, "
                + productsEntry.COL_ITEMINFO + " TEXT, "
                + productsEntry.COL_CURRENCY + " TEXT, "
                + productsEntry.COL_ITEMPRICE + " REAL, "
                + productsEntry.COL_ITEMIMG + " TEXT "
                + ");";

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);

    }

    public void removeProdTable(String busID) {
        String tableName = productsEntry.getTable(busID);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    // For business testimonial table
    public void createTesTable(String busID) {
        String tableName = testimonialsEntry.getTable(busID);

        // Create testimonials table
        final String SQL_CREATE_TESTIMONIALS_TABLE = "CREATE TABLE IF NOT EXISTS "
                + tableName + " ("
                + testimonialsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + testimonialsEntry.COL_COMTER + " TEXT NOT NULL, "
                + testimonialsEntry.COL_COMTDETAIL + " TEXT NOT NULL, "
                + testimonialsEntry.COL_COMTDATE + " INTEGER NOT NULL, "
                + testimonialsEntry.COL_REC + " INTEGER NOT NULL, "
                + testimonialsEntry.COL_BUSID + " TEXT NOT NULL "
                + ");";

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(SQL_CREATE_TESTIMONIALS_TABLE);

    }

    public void removeTesTable(String busID) {
        String tableName = testimonialsEntry.getTable(busID);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
}
