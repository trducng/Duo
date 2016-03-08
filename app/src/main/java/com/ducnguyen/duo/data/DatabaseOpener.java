package com.ducnguyen.duo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ducnguyen.duo.data.DataContract.bookmarkEntry;
import com.ducnguyen.duo.data.DataContract.detailedEntry;
import com.ducnguyen.duo.data.DataContract.loyaltyEntry;
import com.ducnguyen.duo.data.DataContract.productsEntry;
import com.ducnguyen.duo.data.DataContract.testimonialsEntry;
import com.ducnguyen.duo.data.DataContract.searchEntry;


/**
 * Created by ducprogram on 3/8/16.
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

        // Create bookmark table
        final String SQL_CREATE_BOOKMARK_TABLE = "CREATE TABLE "
                + DataContract.BOOKMARK + " ("
                + bookmarkEntry.COLUMN_LISTNAME + " TEXT NOT NULL, "
                + bookmarkEntry.COLUMN_BUSID + " TEXT NOT NULL, "
                + bookmarkEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + bookmarkEntry.COLUMN_BUSPHONE + " TEXT, "
                + bookmarkEntry.COLUMN_BUSLOCATION + " TEXT NOT NULL, "
                + bookmarkEntry.COLUMN_BUSSERVICES + " TEXT, "
                + bookmarkEntry.COLUMN_BUSCOVERIMAGE + " TEXT, "
                + bookmarkEntry.COLUMN_TIMEADDED + " INTEGER NOT NULL "
                + " );";

        // Create business table
        final String SQL_CREATE_DETAILED_TABLE = "CREATE TABLE "
                + DataContract.DETAILED + " ("
                + detailedEntry.COLUMN_SAVED + " INTEGER NOT NULL, "
                + detailedEntry.COLUMN_BUSID + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSPHONE + " TEXT, "
                + detailedEntry.COLUMN_BUSWEB + " TEXT, "
                + detailedEntry.COLUMN_BUSLOCATION + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSEMAIL + " TEXT, "
                + detailedEntry.COLUMN_BUSMESS + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSSERVICES + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSCOVERIMAGE + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSTEST + " TEXT NOT NULL, "
                + detailedEntry.COLUMN_BUSNEWS + " TEXT, "
                + " );";

        // Create products table
        final String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE "
                + DataContract.PRODUCTS + " ("
                + productsEntry.COLUMN_ITEMID + " TEXT NOT NULL, "
                + productsEntry.COLUMN_ITEMNAME + " TEXT NOT NULL, "
                + productsEntry.COLUMN_ITEMINFO + " TEXT, "
                + productsEntry.COLUMN_CURRENCY + " TEXT, "
                + productsEntry.COLUMN_ITEMPRICE + " REAL, "
                + productsEntry.COLUMN_BUSID + " TEXT NOT NULL "
                + ");";

        // Create testimonials table
        final String SQL_CREATE_TESTIMONIALS_TABLE = "CREATE TABLE "
                + testimonialsEntry.COLUMN_COMTER + " TEXT NOT NULL, "
                + testimonialsEntry.COLUMN_COMTDETAIL + " TEXT NOT NULL, "
                + testimonialsEntry.COLUMN_COMTDATE + " INTEGER NOT NULL, "
                + testimonialsEntry.COLUMN_REC + " INTEGER NOT NULL, "
                + testimonialsEntry.COLUMN_BUSID + " TEXT NOT NULL "
                + ");";

        // Create loyalty table
        final String SQL_CREATE_LOYALTY_TABLE = "CREATE TABLE "
                + loyaltyEntry.COLUMN_BUSCOVERIMGAGE + " TEXT, "
                + loyaltyEntry.COLUMN_BUSID + " TEXT NOT NULL, "
                + loyaltyEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + loyaltyEntry.COLUMN_CURPOINT + " INTEGER NOT NULL, "
                + loyaltyEntry.COLUMN_LOYALTYDETAIL + " TEXT NOT NULL, "
                + ");";

        // Create search table
        final String SQL_CREATE_SEARCH_TABLE = "CREATE TABLE "
                + searchEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + searchEntry.COLUMN_BUSID + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSNAME + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSLOCATION + " TEXT NOT NULL, "
                + searchEntry.COLUMN_BUSCOVERIMAGE + " TEXT NOT NULL, "
                + ");";

        // Create the tables
        db.execSQL(SQL_CREATE_SEARCH_TABLE);
        db.execSQL(SQL_CREATE_DETAILED_TABLE);
        db.execSQL(SQL_CREATE_LOYALTY_TABLE);
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_TESTIMONIALS_TABLE);
        db.execSQL(SQL_CREATE_BOOKMARK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Delete the old tables
        // TODO: must have some way to retain DETAILED, PRODUCTS
        // TESTIMONIALS and LOYALTY data
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.BOOKMARK);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.DETAILED);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.TESTIMONIALS);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.LOYALTY);
        db.execSQL("DROP TABLE IF EXISTS " + DataContract.SEARCH);

        // Create the new tables
        onCreate(db);
    }
}
