package com.edonoxako.geophoto.app.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBase extends SQLiteOpenHelper {

    public static final String DB_NAME = "GeoDB";

    public static final String PLACES_TABLE = "Places";
    public static final String PHOTOS_TABLE = "Photos";

    public static final String PLACES_ID_COLUMN = "ID";
    public static final String PLACES_LONGITUDE_COLUMN = "Longitude";
    public static final String PLACES_LATITUDE_COLUMN = "Latitude";
    public static final String PLACES_TEXT_COLUMN = "Text";
    public static final String PLACES_LAST_VISITED_COLUMN = "LastVisited";

    public static final String PHOTOS_ID_COLUMN = "ID";
    public static final String PHOTOS_PATH_COLUMN = "Path";
    public static final String PHOTOS_PLACE_ID_COLUMN = "PlaceID";

    public DataBase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PLACES_TABLE + "("
                + PLACES_ID_COLUMN + " integer primary key autoincrement,"
                + PLACES_LATITUDE_COLUMN + " real,"
                + PLACES_LONGITUDE_COLUMN + " real,"
                + PLACES_TEXT_COLUMN + " text,"
                + PLACES_LAST_VISITED_COLUMN + " text);");

        db.execSQL("create table " + PHOTOS_TABLE + "("
                + PHOTOS_ID_COLUMN + " integer primary key autoincrement,"
                + PHOTOS_PATH_COLUMN + " text,"
                + PHOTOS_PLACE_ID_COLUMN + " integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
