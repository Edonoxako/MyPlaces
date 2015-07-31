package com.edonoxako.geophoto.app;

import android.app.Application;
import com.edonoxako.geophoto.app.backend.PlaceData;

import java.util.ArrayList;
import java.util.List;


public class RepoApp extends Application {

    private static RepoApp singleton;

    public static final String PENDING_INTENT_EXTRA = "pendingIntentExtra";

    private List<PlaceData> places = new ArrayList<PlaceData>();
    private String newPhotoPath;


    public static RepoApp getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    public void savePlaces(List<PlaceData> data) {
        places = data;
    }

    public List<PlaceData> getPlaces() {
        return places;
    }

    public void setNewPhotoPath(String newPhotoPath) {
        this.newPhotoPath = newPhotoPath;
    }

    public String getNewPhotoPath() {
        return newPhotoPath;
    }
}
