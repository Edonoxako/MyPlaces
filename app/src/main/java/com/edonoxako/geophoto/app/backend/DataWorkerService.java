package com.edonoxako.geophoto.app.backend;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.edonoxako.geophoto.app.MainActivity;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.dbtasks.*;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class DataWorkerService extends Service {

    private static final String TAG = "DataWorkerService";

    private PendingIntent listenerPendingIntent;
    private WorkerBinder binder = new WorkerBinder();

    public static final int DATA_CHANGED_SUCCESFULLY = 1;
    public static final int LOADING_DATA_CRASHED = 2;
    public static final int NO_INTERNET_CONNECTION = 3;

    public static final String PREFS = "prefs";
    public static final String FIRST_LAUNCH_PREFS = "firstLaunch";

    private static final String URL = "http://interesnee.ru/files/android-middle-level-data.json";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        listenerPendingIntent = intent.getParcelableExtra(RepoApp.PENDING_INTENT_EXTRA);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void getData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, new PlaceDataLoadResponseListener(), new PlaceDataLoadResponseErrorListener());
        NetworkDataFetcherSingleton.getInstance(this).AddToRequestQueue(request);
    }

    private void informListener(int code) {
        try {
            listenerPendingIntent.send(code);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    //Database data editing
    public void changePlaceCoords(int id, double latitude, double longitude) {
        PlacePositionChangedTask task = new PlacePositionChangedTask(this, id, longitude, latitude);
        changeDB(task);
    }

    public void saveNewPlace(PlaceData newPlace) {
        CreatePlaceTask task = new CreatePlaceTask(this, newPlace);
        changeDB(task);
    }

    public void changePlace(PlaceData changedPlace) {
        ChangePlaceTask task = new ChangePlaceTask(this, changedPlace);
        changeDB(task);
    }

    public void removePlace(int id) {
        DeletePlaceTask task = new DeletePlaceTask(this, id);
        changeDB(task);
    }

    private void changeDB(final DBTask task) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                task.runTask();
                Log.d(TAG, "Editing in background");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                informListener(DATA_CHANGED_SUCCESFULLY);
            }
        }.execute();
    }


    //Callbacks for network operations
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
                    DAO.getInstance(DataWorkerService.this).save(place);
                }

                SharedPreferences.Editor prefs = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                prefs.putBoolean(FIRST_LAUNCH_PREFS, false);
                prefs.apply();
                informListener(DATA_CHANGED_SUCCESFULLY);

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

    //Binder for binding with MainActivity
    public class WorkerBinder extends Binder {
        public DataWorkerService getService() {
            return DataWorkerService.this;
        }
    }

}
