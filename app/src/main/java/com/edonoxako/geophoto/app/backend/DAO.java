package com.edonoxako.geophoto.app.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DAO {

    public static final String LATITUDE_EXTRA = "Latitude";
    public static final String LONGITUDE_EXTRA = "Longitude";
    public static final String TEXT_EXTRA = "Text";
    public static final String LAST_VISITED_EXTRA = "LastVisited";

    public static final String PHOTO_PATH_EXTRA = "Path";
    public static final String PLACE_ID_EXTRA = "PlaceID";

    private DataBase dataBase;
    private static DAO singleton;

    private DAO(Context context) {
        dataBase = new DataBase(context);
    }

    public static DAO getInstance(Context context) {
        if (singleton == null) {
            singleton = new DAO(context);
        }
        return singleton;
    }

    //Queries for obtaining place data
    public Cursor loadPlace(int id) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(DataBase.PLACES_TABLE, null, DataBase.PLACES_ID_COLUMN + " = ?", selectionArgs, null, null, null);
        return cursor;
    }

    public Cursor loadPlacesWhithPhotoThumbnail() {

        String sqlQuery = "SELECT "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN  +", "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_LATITUDE_COLUMN + ", "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_LONGITUDE_COLUMN + ", "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_TEXT_COLUMN + ", "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_LAST_VISITED_COLUMN + ", "
                        + DataBase.PHOTOS_TABLE + "." + DataBase.PHOTOS_PATH_COLUMN
                        + " FROM "
                        + DataBase.PLACES_TABLE
                        + " LEFT JOIN "
                        + DataBase.PHOTOS_TABLE
                        + " ON "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN
                        + " = "
                        + DataBase.PHOTOS_TABLE + "." + DataBase.PHOTOS_PLACE_ID_COLUMN
                        + " GROUP BY "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN;

        SQLiteDatabase db = dataBase.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        return cursor;
    }

    public Cursor loadSearchResultPlaces(String placeName) {
        SQLiteDatabase db = dataBase.getWritableDatabase();

        String query = "SELECT "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN + ", "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_LATITUDE_COLUMN + ", "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_LONGITUDE_COLUMN + ", "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_TEXT_COLUMN + ", "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_LAST_VISITED_COLUMN + ", "
                + DataBase.PHOTOS_TABLE + "." + DataBase.PHOTOS_PATH_COLUMN
                + " FROM "
                + DataBase.PLACES_TABLE
                + " LEFT JOIN "
                + DataBase.PHOTOS_TABLE
                + " ON "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN
                + " = "
                + DataBase.PHOTOS_TABLE + "." + DataBase.PHOTOS_PLACE_ID_COLUMN
                + " WHERE "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_TEXT_COLUMN
                + " LIKE "
                + "'%" + placeName + "%'"
                + " GROUP BY "
                + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN;

        Cursor c = db.rawQuery(query, null);
        return c;
    }

    public Cursor loadPhotos(int placeId) {
        SQLiteDatabase db = dataBase.getWritableDatabase();

        String[] columns = new String[]{DataBase.PHOTOS_ID_COLUMN, DataBase.PHOTOS_PATH_COLUMN};
        String[] selectionArgs = new String[]{String.valueOf(placeId)};

        Cursor cursor = db.query(DataBase.PHOTOS_TABLE, columns, DataBase.PHOTOS_PLACE_ID_COLUMN + " = ?", selectionArgs, null, null, null);
        return cursor;
    }

    //Releasing resources
    public void close() {
        dataBase.close();
        singleton = null;
    }

    //Queries for modifying place data
    public int save(PlaceData place) {
        ContentValues placesCV = new ContentValues();
        placesCV.put(LATITUDE_EXTRA, place.getLatitude());
        placesCV.put(LONGITUDE_EXTRA, place.getLongitude());
        placesCV.put(TEXT_EXTRA, place.getText());
        placesCV.put(LAST_VISITED_EXTRA, place.getLastVisited());

        SQLiteDatabase db = dataBase.getWritableDatabase();
        long rowId = db.insert(DataBase.PLACES_TABLE, null, placesCV);

        if (!place.getAllPhotos().isEmpty()) {
            ContentValues photosCV = new ContentValues();
            for (String path : place.getAllPhotos()) {
                photosCV.put(PLACE_ID_EXTRA, rowId);
                photosCV.put(PHOTO_PATH_EXTRA, path);
                db.insert(DataBase.PHOTOS_TABLE, null, photosCV);
            }
        }

        return (int) rowId;
    }

    public void delete(int id) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        db.delete(DataBase.PLACES_TABLE, DataBase.PLACES_ID_COLUMN + " = ?", new String[] {String.valueOf(id)});
        db.delete(DataBase.PHOTOS_TABLE, DataBase.PHOTOS_PLACE_ID_COLUMN + " = ?", new String[] {String.valueOf(id)});
    }

    public void changePlaceCoords(int placeId, double newLatitude, double newLongitude) {
        ContentValues cv = new ContentValues();
        cv.put(DataBase.PLACES_LONGITUDE_COLUMN, newLongitude);
        cv.put(DataBase.PLACES_LATITUDE_COLUMN, newLatitude);

        String[] whereArgs = new String[]{String.valueOf(placeId)};
        SQLiteDatabase db = dataBase.getWritableDatabase();
        db.update(DataBase.PLACES_TABLE, cv, DataBase.PLACES_ID_COLUMN + " = ?", whereArgs);
    }

    public void changePlace(PlaceData changedPlace) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(LATITUDE_EXTRA, changedPlace.getLatitude());
        cv.put(LONGITUDE_EXTRA, changedPlace.getLongitude());
        cv.put(TEXT_EXTRA, changedPlace.getText());
        cv.put(LAST_VISITED_EXTRA, changedPlace.getLastVisited());

        db.update(DataBase.PLACES_TABLE, cv, DataBase.PLACES_ID_COLUMN + " = ?", new String[] {String.valueOf(changedPlace.getId())});
        cv.clear();

        db.delete(DataBase.PHOTOS_TABLE, DataBase.PHOTOS_PLACE_ID_COLUMN + " = ?", new String[] {String.valueOf(changedPlace.getId())});
        for (String path : changedPlace.getAllPhotos()) {
            cv.put(PLACE_ID_EXTRA, changedPlace.getId());
            cv.put(PHOTO_PATH_EXTRA, path);
            db.insert(DataBase.PHOTOS_TABLE, null, cv);
        }
    }

}
