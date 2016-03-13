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


/**
 * This class provides a simple interface to open and manage
 * different versions of database
 */
public class DatabaseOpener extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "duo.db";

    public DatabaseOpener(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create each of the table with the appropriate
        // column in DataContract

        Log.v("DatabaseOpener", "In onCreate beginning to create table");

        // Create bookmark table
        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE "
                + DataContract.BOOKMARK + " ("
                + bookmarkEntry.COL_LISTNAME + " TEXT NOT NULL, "
                + bookmarkEntry.COL_BUSID + " TEXT NOT NULL, "
                + bookmarkEntry.COL_BUSNAME + " TEXT NOT NULL, "
                + bookmarkEntry.COL_BUSPHONE + " TEXT, "
                + bookmarkEntry.COL_BUSLOCATION + " TEXT NOT NULL, "
                + bookmarkEntry.COL_BUSSERVICES + " TEXT, "
                + bookmarkEntry.COL_BUSCOVERIMAGE + " TEXT, "
                // TODO: COL_TIMEADDED should be " INTEGER NOT NULL "
                // change to TEXT NOT NULL just for testing
                + bookmarkEntry.COL_TIMEADDED + " TEXT NOT NULL "
                + " );";

        // Create business table
        final String SQL_CREATE_DETAILED_TABLE = "CREATE TABLE "
                + DataContract.DETAILED + " ("
                + detailedEntry.COLUMN_SAVED + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSID + " TEXT UNIQUE NOT NULL, "
                + detailedEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSPHONE + " TEXT, "
                + detailedEntry.COLUMN_BUSWEB + " TEXT, "
                + detailedEntry.COLUMN_BUSLOCATION + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSEMAIL + " TEXT, "
                + detailedEntry.COLUMN_BUSMESS + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSSERVICES + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSCOVERIMAGE + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSTEST + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSNEWS + " TEXT "
                + " );";

        // Create loyalty table
        final String SQL_CREATE_LOYALTY_TABLE = "CREATE TABLE "
                + loyaltyEntry.COLUMN_BUSCOVERIMGAGE + " TEXT, "
                + loyaltyEntry.COLUMN_BUSID + " TEXT NOT NULL, "
                + loyaltyEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + loyaltyEntry.COLUMN_CURPOINT + " INTEGER NOT NULL, "
                + loyaltyEntry.COLUMN_LOYALTYDETAIL + " TEXT NOT NULL "
                + ");";

//        // Create search table
        final String SQL_CREATE_SEARCH_TABLE = "CREATE TABLE "
                + DataContract.SEARCH + " ("
                + searchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + searchEntry.COLUMN_BUSID + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSLOCATION + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSSERVICES + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSCOVERIMAGE + " TEXT NOT NULL "
                + ");";

        // Create events table
        final String SQL_CREATE_EVENTS_TABLE = "CREATE TABLE "
                + DataContract.EVENTS + " ("
                + eventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + eventsEntry.COL_BUSID + " TEXT NOT NULL, "
                + eventsEntry.COL_BUSNAME + " TEXT NOT NULL, "
                + eventsEntry.COL_BUSEVENT + " TEXT NOT NULL, "
                + eventsEntry.COL_BUSLOCATION + " TEXT NOT NULL "
                + ");";

//        // Create the tables
        db.execSQL(SQL_CREATE_SEARCH_TABLE);
        db.execSQL(SQL_CREATE_DETAILED_TABLE);
//        db.execSQL(SQL_CREATE_LOYALTY_TABLE);
//        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
//        db.execSQL(SQL_CREATE_TESTIMONIALS_TABLE);
        db.execSQL(SQL_CREATE_EVENTS_TABLE);
        db.execSQL(SQL_CREATE_BOOKMARK_TABLE);
        Log.v("DatabaseOpener", "Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Delete the old tables
        // TODO: must have some way to retain DETAILED, PRODUCTS
        // TESTIMONIALS and LOYALTY data
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.DETAILED);
//        db.execSQL("DROP TABLE IF EXISTS " + DataContract.PRODUCTS);
//        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TESTIMONIALS);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.LOYALTY);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.SEARCH);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.EVENTS);

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
                + productsEntry.COLUMN_ITEMID + " TEXT NOT NULL, "
                + productsEntry.COLUMN_ITEMNAME + " TEXT NOT NULL, "
                + productsEntry.COLUMN_ITEMINFO + " TEXT, "
                + productsEntry.COLUMN_CURRENCY + " TEXT, "
                + productsEntry.COLUMN_ITEMPRICE + " REAL, "
                + productsEntry.COLUMN_ITEMIMAGE + " TEXT "
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
                + testimonialsEntry.COLUMN_COMTER + " TEXT NOT NULL, "
                + testimonialsEntry.COLUMN_COMTDETAIL + " TEXT NOT NULL, "
                + testimonialsEntry.COLUMN_COMTDATE + " INTEGER NOT NULL, "
                + testimonialsEntry.COLUMN_REC + " INTEGER NOT NULL, "
                + testimonialsEntry.COLUMN_BUSID + " TEXT NOT NULL "
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
