package com.edonoxako.geophoto.app.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


public class DAO {

    public interface DAOListener {
        void onPlacesLoaded(List<PlaceData> places);
    }

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

        //db.close();

        return (int) rowId;
    }

    public void delete(int id) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        db.delete(DataBase.PLACES_TABLE, DataBase.PLACES_ID_COLUMN + " = ?", new String[] {String.valueOf(id)});
        db.delete(DataBase.PHOTOS_TABLE, DataBase.PHOTOS_PLACE_ID_COLUMN + " = ?", new String[] {String.valueOf(id)});
        //db.close();
    }

    public void clearDataBase() {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        db.delete(DataBase.PLACES_TABLE, null, null);
        db.delete(DataBase.PHOTOS_TABLE, null, null);
        //db.close();
    }

    public Cursor loadPlace(int id) {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(DataBase.PLACES_TABLE, null, DataBase.PLACES_ID_COLUMN + " = ?", selectionArgs, null, null, null);
        return cursor;
    }

    public Cursor loadPlacesWhithPhotoThumbnail() {

        String sqlQuery = "SELECT "
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
                        + " GROUP BY "
                        + DataBase.PLACES_TABLE + "." + DataBase.PLACES_ID_COLUMN;

        SQLiteDatabase db = dataBase.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        return cursor;
    }

    public Cursor loadPlaces() {
        SQLiteDatabase db = dataBase.getWritableDatabase();
        Cursor c = db.query(DataBase.PLACES_TABLE, null, null, null, null, null, null);
        return c;
    }

    //TODO: Этот метод надо будет удалить
    public void loadPlaces(final DAOListener listener) {
        final List<PlaceData> places = new ArrayList<PlaceData>();

        //Data loading is asynchronous
        AsyncTask<Void, Void, List<PlaceData>> loader = new AsyncTask<Void, Void, List<PlaceData>>() {

            @Override
            protected List<PlaceData> doInBackground(Void... params) {
                SQLiteDatabase db = dataBase.getWritableDatabase();
                Cursor c = db.query(DataBase.PLACES_TABLE, null, null, null, null, null, null);

                if (c.moveToFirst()) {
                    places.add(createPlace(c));
                    while (c.moveToNext()) {
                        places.add(createPlace(c));
                    }
                }
                c.close();

                if (!places.isEmpty()) {
                    for (int i = 0; i < places.size(); i++) {
                        List<String> photos = gatherPhotos(places.get(i).getId(), db);
                        places.get(i).setPhotos(photos);
                    }
                }

                //db.close();
                return places;
            }

            @Override
            protected void onPostExecute(List<PlaceData> places) {
                super.onPostExecute(places);
                if (listener != null) listener.onPlacesLoaded(places);
            }
        };

        loader.execute();
    }

    public Cursor loadPhotos(int placeId) {
        SQLiteDatabase db = dataBase.getWritableDatabase();

        String[] columns = new String[]{DataBase.PHOTOS_ID_COLUMN, DataBase.PHOTOS_PATH_COLUMN};
        String[] selectionArgs = new String[]{String.valueOf(placeId)};

        Cursor cursor = db.query(DataBase.PHOTOS_TABLE, columns, DataBase.PHOTOS_PLACE_ID_COLUMN + " = ?", selectionArgs, null, null, null);
        return cursor;
    }

    public void close() {
        dataBase.close();
        singleton = null;
    }

    public void changePlaceCoords(int placeId, double newLatitude, double newLongitude) {
        ContentValues cv = new ContentValues();
        cv.put(DataBase.PLACES_LONGITUDE_COLUMN, newLongitude);
        cv.put(DataBase.PLACES_LATITUDE_COLUMN, newLatitude);

        String[] whereArgs = new String[]{String.valueOf(placeId)};
        SQLiteDatabase db = dataBase.getWritableDatabase();
        db.update(DataBase.PLACES_TABLE, cv, DataBase.PLACES_ID_COLUMN + " = ?", whereArgs);
    }

    private PlaceData createPlace(Cursor c) {
        PlaceData place = new PlaceData();

        place.setId(c.getInt(c.getColumnIndex(DataBase.PLACES_ID_COLUMN)));
        place.setLatitude(c.getDouble(c.getColumnIndex(DataBase.PLACES_LATITUDE_COLUMN)));
        place.setLongitude(c.getDouble(c.getColumnIndex(DataBase.PLACES_LONGITUDE_COLUMN)));
        place.setText(c.getString(c.getColumnIndex(DataBase.PLACES_TEXT_COLUMN)));
        place.setLastVisited(c.getString(c.getColumnIndex(DataBase.PLACES_LAST_VISITED_COLUMN)));

        return place;
    }

    private List<String> gatherPhotos(int placeId, SQLiteDatabase db) {
        List<String> photos = new ArrayList<String>();

        Cursor c = db.query(DataBase.PHOTOS_TABLE, null, DataBase.PHOTOS_PLACE_ID_COLUMN + " = ?", new String[] {String.valueOf(placeId)}, null, null, null);
        if (c.moveToFirst()) {
            String path = c.getString(c.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN));
            photos.add(path);
            while (c.moveToNext()) {
                path = c.getString(c.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN));
                photos.add(path);
            }
        }
        //c.close();

        return photos;
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

        //db.close();
    }

}
