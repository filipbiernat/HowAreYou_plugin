package com.aware.plugin.howareyou;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.util.HashMap;

public class Provider extends ContentProvider {

    public static String AUTHORITY = "com.aware.plugin.howareyou.provider.howareyou"; //change to package.provider.your_plugin_name

    public static final int DATABASE_VERSION = 6; //increase this if you make changes to the database structure, i.e., rename columns, etc.
    public static final String DATABASE_NAME = "plugin_howareyou.db"; //the database filename, use plugin_xxx for plugins.

    //Add here your database table names, as many as you need
    public static final String DB_TBL_HOWAREYOU_PHOTO = "photo_data";
    public static final String DB_TBL_HOWAREYOU_COLOR = "color_data";

    //For each table, add two indexes: DIR and ITEM. The index needs to always increment. Next one is 3, and so on.
    private static final int PHOTO_DATA_DIR = 1;
    private static final int PHOTO_DATA_ITEM = 2;
    private static final int COLOR_DATA_DIR = 3;
    private static final int COLOR_DATA_ITEM = 4;

    //Put tables names in this array so AWARE knows what you have on the database
    public static final String[] DATABASE_TABLES = {
        DB_TBL_HOWAREYOU_PHOTO, DB_TBL_HOWAREYOU_COLOR
    };

    //These are columns that we need to sync data, don't change this!
    public interface AWAREColumns extends BaseColumns {
        String _ID = "_id";
        String TIMESTAMP = "timestamp";
        String DEVICE_ID = "device_id";
    }

    /**
     * Create one of these per database table
     * In this example, we are adding example columns
     */
    public static final class Table_Photo_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TBL_HOWAREYOU_PHOTO);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.howareyou.provider.photo_data"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.howareyou.provider.photo_data"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String ANGER     = "double_anger";
        public static final String CONTEMPT  = "double_contempt";
        public static final String DISGUST   = "double_disgust";
        public static final String FEAR      = "double_fear";
        public static final String HAPPINESS = "double_happiness";
        public static final String NEUTRAL   = "double_neutral";
        public static final String SADNESS   = "double_sadness";
        public static final String SURPRISE  = "double_surprise";
    }
    public static final class Table_Color_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TBL_HOWAREYOU_COLOR);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.howareyou.provider.color_data"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.howareyou.provider.color_data"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String COLOR_RED   = "color_red";
        public static final String COLOR_GREEN = "color_green";
        public static final String COLOR_BLUE  = "color_blue";
    }

    //Define each database table fields
    private static final String DB_TBL_HOWAREYOU_PHOTO_FIELDS =
            Table_Photo_Data._ID + " integer primary key autoincrement," +
                    Table_Photo_Data.TIMESTAMP + " real default 0," +
                    Table_Photo_Data.DEVICE_ID + " text default ''," +
                    Table_Photo_Data.ANGER + " real default 0," +
                    Table_Photo_Data.CONTEMPT + " real default 0," +
                    Table_Photo_Data.DISGUST + " real default 0," +
                    Table_Photo_Data.FEAR + " real default 0," +
                    Table_Photo_Data.HAPPINESS + " real default 0," +
                    Table_Photo_Data.NEUTRAL + " real default 0," +
                    Table_Photo_Data.SADNESS + " real default 0," +
                    Table_Photo_Data.SURPRISE + " real default 0";
    private static final String DB_TBL_HOWAREYOU_COLOR_FIELDS =
            Table_Color_Data._ID + " integer primary key autoincrement," +
                    Table_Color_Data.TIMESTAMP + " real default 0," +
                    Table_Color_Data.DEVICE_ID + " text default ''," +
                    Table_Color_Data.COLOR_RED + " integer default 0," +
                    Table_Color_Data.COLOR_GREEN + " integer default 0," +
                    Table_Color_Data.COLOR_BLUE + " integer default 0";

    /**
     * Share the fields with AWARE so we can replicate the table schema on the server
     */
    public static final String[] TABLES_FIELDS = {
            DB_TBL_HOWAREYOU_PHOTO_FIELDS, DB_TBL_HOWAREYOU_COLOR_FIELDS
    };

    //Helper variables for ContentProvider - DO NOT CHANGE
    private UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;
    private static SQLiteDatabase database;
    private void initialiseDatabase() {
        if (dbHelper == null)
            dbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if (database == null)
            database = dbHelper.getWritableDatabase();
    }
    //--

    //For each table, create a hashmap needed for database queries
    private HashMap<String, String> tablePhotoHash, tableColorHash;

    /**
     * Returns the provider authority that is dynamic
     * @return
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider.howareyou";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        //This is a hack to allow providers to be reusable in any application/plugin by making the authority dynamic using the package name of the parent app
        AUTHORITY = getAuthority(getContext());

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //For each table, add indexes DIR and ITEM
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], PHOTO_DATA_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0] + "/#", PHOTO_DATA_ITEM);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], COLOR_DATA_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1] + "/#", COLOR_DATA_ITEM);

        //Create each table hashmap so Android knows how to insert data to the database. Put ALL table fields.
        tablePhotoHash = new HashMap<>();
        tablePhotoHash.put(Table_Photo_Data._ID, Table_Photo_Data._ID);
        tablePhotoHash.put(Table_Photo_Data.TIMESTAMP, Table_Photo_Data.TIMESTAMP);
        tablePhotoHash.put(Table_Photo_Data.DEVICE_ID, Table_Photo_Data.DEVICE_ID);
        tablePhotoHash.put(Table_Photo_Data.ANGER, Table_Photo_Data.ANGER);
        tablePhotoHash.put(Table_Photo_Data.CONTEMPT, Table_Photo_Data.CONTEMPT);
        tablePhotoHash.put(Table_Photo_Data.DISGUST, Table_Photo_Data.DISGUST);
        tablePhotoHash.put(Table_Photo_Data.FEAR, Table_Photo_Data.FEAR);
        tablePhotoHash.put(Table_Photo_Data.HAPPINESS, Table_Photo_Data.HAPPINESS);
        tablePhotoHash.put(Table_Photo_Data.NEUTRAL, Table_Photo_Data.NEUTRAL);
        tablePhotoHash.put(Table_Photo_Data.SADNESS, Table_Photo_Data.SADNESS);
        tablePhotoHash.put(Table_Photo_Data.SURPRISE, Table_Photo_Data.SURPRISE);

        tableColorHash = new HashMap<>();
        tableColorHash.put(Table_Color_Data._ID, Table_Color_Data._ID);
        tableColorHash.put(Table_Color_Data.TIMESTAMP, Table_Color_Data.TIMESTAMP);
        tableColorHash.put(Table_Color_Data.DEVICE_ID, Table_Color_Data.DEVICE_ID);
        tableColorHash.put(Table_Color_Data.COLOR_RED, Table_Color_Data.COLOR_RED);
        tableColorHash.put(Table_Color_Data.COLOR_GREEN, Table_Color_Data.COLOR_GREEN);
        tableColorHash.put(Table_Color_Data.COLOR_BLUE, Table_Color_Data.COLOR_BLUE);

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        initialiseDatabase();

        database.beginTransaction();

        int count;
        switch (sUriMatcher.match(uri)) {

            //Add each table DIR case, increasing the index accordingly
            case PHOTO_DATA_DIR:
                count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
                break;

            case COLOR_DATA_DIR:
                count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
                break;

            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        initialiseDatabase();

        ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

        database.beginTransaction();

        switch (sUriMatcher.match(uri)) {

            //Add each table DIR case
            case PHOTO_DATA_DIR: {
                long _id = database.insert(DATABASE_TABLES[0], Table_Photo_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Table_Photo_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            }
            case COLOR_DATA_DIR: {
                long _id = database.insert(DATABASE_TABLES[1], Table_Color_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Table_Color_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            }
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        initialiseDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {

            //Add all tables' DIR entries, with the right table index
            case PHOTO_DATA_DIR:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(tablePhotoHash); //the hashmap of the table
                break;

            case COLOR_DATA_DIR:
                qb.setTables(DATABASE_TABLES[1]);
                qb.setProjectionMap(tableColorHash); //the hashmap of the table
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //Don't change me
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            if (Aware.DEBUG)
                Log.e(Aware.TAG, e.getMessage());
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {

            //Add each table indexes DIR and ITEM
            case PHOTO_DATA_DIR:
                return Table_Photo_Data.CONTENT_TYPE;
            case PHOTO_DATA_ITEM:
                return Table_Photo_Data.CONTENT_ITEM_TYPE;
            case COLOR_DATA_DIR:
                return Table_Color_Data.CONTENT_TYPE;
            case COLOR_DATA_ITEM:
                return Table_Color_Data.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        initialiseDatabase();

        database.beginTransaction();

        int count;
        switch (sUriMatcher.match(uri)) {

            //Add each table DIR case
            case PHOTO_DATA_DIR:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;
            case COLOR_DATA_DIR:
                count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
                break;

            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
