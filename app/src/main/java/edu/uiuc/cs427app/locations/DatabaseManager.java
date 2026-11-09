package edu.uiuc.cs427app.locations;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;

public class DatabaseManager extends ContentProvider{
    public static final String PROVIDER_NAME = "edu.uiuc.cs427app.provider";
    // defining content URI
    public static final String URL = "content://" + PROVIDER_NAME + "/locations";
    // parsing the content URI
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static final String DATABASE_NAME = "cs427_cities.db";
    private static final int    DATABASE_VERSION = 1;

    // Column names
    public static final String USERNAME  = "username";
    public static final String TABLE_NAME   = "cities";
    public static final String ID        = "_id";
    public static final String LOCATION = "location_name";


    // Using SQLITE to manage the storage of the cities
    public static class DatabaseHelper extends SQLiteOpenHelper {
        // constructor that represents the version of database schema
        DatabaseHelper(android.content.Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        // when no database, it will create a database for the app and point to the newly-created database so we can populate it
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY, " + USERNAME  + " TEXT NOT NULL, " + LOCATION + " TEXT NOT NULL" + ")");
        }

        // only called when schema version does not match the schema version of database. convert the database from old schema to new
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    private DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return dbHelper.getReadableDatabase().query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    // Insert operation: inserts new user location pair ex: (username, "Chicago, IL, US")
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id = dbHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
        if (id == -1){
            return null;
        }
        else {
            return ContentUris.withAppendedId(uri, id);
        }
    }

    // deletes a user location pair ex: (username, "Chicago, IL, US") is removed where these items match
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return dbHelper.getWritableDatabase().delete(TABLE_NAME, selection, selectionArgs);
    }

    // not needed but required to compile for content provider
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    // ot needed but required to compile for content provider
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }







}
