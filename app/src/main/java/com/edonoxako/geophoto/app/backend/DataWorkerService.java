package com.edonoxako.geophoto.app.backend;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edonoxako.geophoto.app.RepoApp;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class DataWorkerService extends Service {

    private static final String TAG = "DataWorkerService";

    private DataWorkerBinder binder = new DataWorkerBinder();
    private PendingIntent listenerPendingIntent;

    public static final int LOADING_DATA_SUCCEED = 1;
    public static final int LOADING_DATA_CRASHED = 2;
    public static final int NO_INTERNET_CONNECTION = 3;

    public static final String PREFS = "prefs";
    public static final String FIRST_LAUNCH_PREFS = "firstLaunch";

    private static final String URL = "http://interesnee.ru/files/android-middle-level-data.json";

    private DAO dao;
    private boolean isFirstLaunch;

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new DAO(this);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        isFirstLaunch = prefs.getBoolean(FIRST_LAUNCH_PREFS, true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        listenerPendingIntent = intent.getParcelableExtra(RepoApp.PENDING_INTENT_EXTRA);
        return binder;
    }

    public void getData() {

        if (isFirstLaunch) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new PlaceDataLoadResponseListener(), new PlaceDataLoadResponseErrorListener());
            NetworkDataFetcherSingleton.getInstance(this).AddToRequestQueue(request);
        } else {

            if (RepoApp.getInstance().getPlaces().isEmpty()) {
                dao.loadPlaces(new DAO.DAOListener() {
                    @Override
                    public void onPlacesLoaded(List<PlaceData> places) {
                        RepoApp.getInstance().savePlaces(places);
                        informListener(LOADING_DATA_SUCCEED);
                    }
                });
            } else {
                informListener(LOADING_DATA_SUCCEED);
            }
        }
    }

    public void changePlace(int id, PlaceData changedPlace) {
        RepoApp.getInstance().getPlaces().get(id).setText(changedPlace.getText());
        RepoApp.getInstance().getPlaces().get(id).setLatitude(changedPlace.getLatitude());
        RepoApp.getInstance().getPlaces().get(id).setLongitude(changedPlace.getLongitude());
        RepoApp.getInstance().getPlaces().get(id).setPhotos(changedPlace.getAllPhotos());
        RepoApp.getInstance().getPlaces().get(id).setLastVisited(changedPlace.getLastVisited());

        dao.changePlace(RepoApp.getInstance().getPlaces().get(id));
    }

    public void createPlace(PlaceData newPlace) {
        dao.save(newPlace);
        dao.loadPlaces(new DAO.DAOListener() {
            @Override
            public void onPlacesLoaded(List<PlaceData> places) {
                RepoApp.getInstance().savePlaces(places);
                informListener(LOADING_DATA_SUCCEED);
            }
        });
    }

    private void informListener(int code) {
        try {
            listenerPendingIntent.send(code);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public void changePlacePosition(int id, double newLatitude, double newLongitude) {
        RepoApp.getInstance().getPlaces().get(id).setLatitude(newLatitude);
        RepoApp.getInstance().getPlaces().get(id).setLongitude(newLongitude);
        dao.changePlace(RepoApp.getInstance().getPlaces().get(id));
        informListener(LOADING_DATA_SUCCEED);
    }

    public void removePhoto(int placeId, int photoId) {
        RepoApp.getInstance().getPlaces().get(placeId).removePhoto(photoId);
        informListener(LOADING_DATA_SUCCEED);
    }

    public void removePlace(int placeId) {
        RepoApp.getInstance().getPlaces().get(placeId).removeAllPhotos();
        dao.delete(RepoApp.getInstance().getPlaces().get(placeId).getId());
        RepoApp.getInstance().getPlaces().remove(placeId);
    }


    public class DataWorkerBinder extends Binder {
        public DataWorkerService getService() {
            return DataWorkerService.this;
        }
    }

    private class PlaceDataLoadResponseListener implements Response.Listener<JSONObject> {

        @Override
        public void onResponse(JSONObject response) {
            List<PlaceData> places = PlaceDataMapper.mapFromJSON(response);
            if (places != null) {

                for (PlaceData place : places) {

                    File file = new File(getFilesDir(), place.getText());
                    ImageLoadResponseListener imageListener = new ImageLoadResponseListener(file);
                    ImageRequest request = new ImageRequest(place.getPhoto(0), imageListener, 0, 0, null, null, new PlaceDataLoadResponseErrorListener());
                    NetworkDataFetcherSingleton.getInstance(DataWorkerService.this).AddToRequestQueue(request);

                    place.removePhoto(0);
                    place.addPhoto("file:" + file.getAbsolutePath());
                    dao.save(place);
                }

                dao.loadPlaces(new DAO.DAOListener() {
                    @Override
                    public void onPlacesLoaded(List<PlaceData> places) {
                        RepoApp.getInstance().savePlaces(places);

                        SharedPreferences.Editor prefs = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                        prefs.putBoolean(FIRST_LAUNCH_PREFS, false);
                        prefs.apply();
                        isFirstLaunch = false;

                        informListener(LOADING_DATA_SUCCEED);
                    }
                });

            } else {
                informListener(LOADING_DATA_CRASHED);
            }
        }
    }

    private class ImageLoadResponseListener implements Response.Listener<Bitmap> {

        private File imageFile;

        public ImageLoadResponseListener(File imageFile) {
            this.imageFile = imageFile;
        }

        @Override
        public void onResponse(Bitmap response) {

            FileOutputStream fos = null;
            try {

                fos = new FileOutputStream(imageFile);
                response.compress(Bitmap.CompressFormat.PNG, 100, fos);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {

                if (fos != null) try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private class PlaceDataLoadResponseErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NoConnectionError) {
                informListener(NO_INTERNET_CONNECTION);
            } else {
                informListener(LOADING_DATA_CRASHED);
            }
        }
    }

}
