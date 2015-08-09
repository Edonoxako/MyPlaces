package com.edonoxako.geophoto.app;

import android.app.Application;
import android.database.Cursor;
import com.edonoxako.geophoto.app.backend.PlaceData;

import java.util.ArrayList;
import java.util.List;


public class RepoApp extends Application {

    private static RepoApp singleton;

    public static final String PENDING_INTENT_EXTRA = "pendingIntentExtra";
    public static final String PLACE_ID_EXTRA = "placeIdExtra";

    private Cursor placesCursor;
    private List<PlaceData> places = new ArrayList<PlaceData>();
    private List<String> photos = new ArrayList<String>();
    private String newPhotoPath;


    public static RepoApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public void savePhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<String> obtainPhotos() {
        return photos;
    }

    public void savePlaces(List<PlaceData> data) {
        places = data;
    }

    public void savePlaces(Cursor cursor) {
        placesCursor = cursor;
    }

    public List<PlaceData> getPlaces() {
        return places;
    }

    public Cursor getPlacesCursor() {
        return placesCursor;
    }

    public void setNewPhotoPath(String newPhotoPath) {
        this.newPhotoPath = newPhotoPath;
    }

    public String getNewPhotoPath() {
        return newPhotoPath;
    }

    public void flush() {
        placesCursor = null;
        photos.clear();
        newPhotoPath = null;
    }
}
