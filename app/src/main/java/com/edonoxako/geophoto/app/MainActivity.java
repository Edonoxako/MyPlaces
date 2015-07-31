package com.edonoxako.geophoto.app;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.edonoxako.geophoto.app.backend.DataWorkerService;
import com.edonoxako.geophoto.app.backend.PlaceData;
import com.edonoxako.geophoto.app.ui.*;
import com.edonoxako.geophoto.app.ui.interfaces.BackNavigateListener;
import com.edonoxako.geophoto.app.ui.interfaces.DateSetterListener;
import com.edonoxako.geophoto.app.ui.interfaces.PhotoFetcherListener;
import com.edonoxako.geophoto.app.ui.interfaces.PresenterActivityListener;


public class MainActivity extends AppCompatActivity implements
        EditPlaceFragment.EditPlaceListener,
        GeoListFragment.GeoListListener,
        GoogleMapFragment.GoogleMapListener,
        PlaceDescriptionFragment.PlaceDescriptionListener,
        BackNavigateListener,
        DatePickerFragment.DatePickerFragmentListener
{

    private PresenterActivityListener listener;
    private PhotoFetcherListener photoFetcherListener;
    private DateSetterListener dateSetterListener;

    private DataWorkerService dataWorkerService;
    private Intent serviceIntent;
    private boolean bound = false;
    private DataWorkerServiceConnection serviceConnection;

    private static final String EDIT_PLACE_FRAGMENT_TAG = "EditPlace";
    private static final String GEO_LIST_FRAGMENT_TAG = "GeoList";
    private static final String GOOGLE_MAP_FRAGMENT_TAG = "GoogleMap";
    private static final String PLACE_DESCRIPTION_FRAGMENT_TAG = "PlaceDescription";

    public static final String PHOTO_GRID_POSITION = "photoGridPosition";
    public static final String CURRENT_PLACE_ID = "currentPlaceId";

    public static final int PENDING_INTENT_REQUEST_CODE = 1;
    public static final int SYSTEM_GALLERY_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceIntent = new Intent(this, DataWorkerService.class);
        startService(serviceIntent);

        serviceConnection = new DataWorkerServiceConnection();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GoogleMapFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PendingIntent pi = createPendingResult(PENDING_INTENT_REQUEST_CODE, new Intent(), 0);
        serviceIntent.putExtra(RepoApp.PENDING_INTENT_EXTRA, pi);
        bindService(serviceIntent, serviceConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        bound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            listener = null;
            stopService(serviceIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PENDING_INTENT_REQUEST_CODE) {
            switch (resultCode) {
                case DataWorkerService.LOADING_DATA_SUCCEED:
                    if (listener != null) listener.onPlacesLoaded();
                    break;

                case DataWorkerService.LOADING_DATA_CRASHED:
                    Toast.makeText(this, getResources().getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                    break;

                case DataWorkerService.NO_INTERNET_CONNECTION:
                    Toast.makeText(this, getResources().getString(R.string.no_connection_msg), Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (requestCode == SYSTEM_GALLERY_REQUEST && resultCode == RESULT_OK) {
            String selectedImagePath = data.getData().toString();
            RepoApp.getInstance().setNewPhotoPath(selectedImagePath);
            if (photoFetcherListener != null) photoFetcherListener.onPhotoFetched();
        }
    }

    //Helpers methods
    private void changeActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private void changeActivity(Class<?> cls, int placeId, int pos) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(CURRENT_PLACE_ID, placeId);
        intent.putExtra(PHOTO_GRID_POSITION, pos);
        startActivity(intent);
    }

    private void changeFragment(Fragment fragment, String tag, int animation) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(animation)
                .replace(R.id.container, fragment)
                .addToBackStack(tag)
                .commit();
    }

    private void changeFragmentWithoutBackStack(Fragment fragment, int animation) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(animation)
                .replace(R.id.container, fragment)
                .commit();
    }

    private void sendSystemGalleryImageRequest() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SYSTEM_GALLERY_REQUEST);
    }


    //Methods set currently displaying fragments. Invoked by fragments
    @Override
    public void setMapFragment(GoogleMapFragment fragment) {
        listener = fragment;
    }

    @Override
    public void setPlaceDescriptionFragment(PlaceDescriptionFragment fragment) {
        listener = fragment;
    }

    @Override
    public void setEditPlaceFragment(EditPlaceFragment fragment) {
        photoFetcherListener = fragment;
        listener = fragment;
        dateSetterListener = fragment;
    }

    @Override
    public void setListFragment(GeoListFragment fragment) {
        listener = fragment;
    }


    //Fragment's onClick actions
    @Override
    public void onViewMap() {
        changeFragmentWithoutBackStack(new GoogleMapFragment(), FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    @Override
    public void onViewList() {
        changeFragmentWithoutBackStack(new GeoListFragment(), FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    }

    public void onEditGeoClick(int id) {
        changeFragment(EditPlaceFragment.newInstance(id), EDIT_PLACE_FRAGMENT_TAG, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

    @Override
    public void onShowPhotoClick(int placeId, int index) {
        changeActivity(PhotoGalleryActivity.class, placeId, index);
    }

    @Override
    public void onEditPhotoClick() {
        sendSystemGalleryImageRequest();
    }

    @Override
    public void onAddNewPlace(double latitude, double longitude) {
        changeFragment(EditPlaceFragment.newInstance(latitude, longitude), EDIT_PLACE_FRAGMENT_TAG, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

    @Override
    public void onShowPlaceInfo(int id) {
        changeFragment(PlaceDescriptionFragment.newInstance(id), PLACE_DESCRIPTION_FRAGMENT_TAG, FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

    @Override
    public void onMarkPositionChanged(int id, double latitude, double longitude) {
        dataWorkerService.changePlacePosition(id, latitude, longitude);
    }

    @Override
    public void onPlaceRemove(int placeId) {
        dataWorkerService.removePlace(placeId);
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this, getResources().getString(R.string.place_removed_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSavePlaceChanges(int id, PlaceData changedPlace) {
        dataWorkerService.changePlace(id, changedPlace);
        getSupportFragmentManager().popBackStack();
        if (listener != null) listener.onPlacesLoaded();
        Toast.makeText(this, getResources().getString(R.string.data_changed_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveNewPlace(PlaceData newPlace){
        dataWorkerService.createPlace(newPlace);
        getSupportFragmentManager().popBackStack();
        if (listener != null) listener.onPlacesLoaded();
        Toast.makeText(this, getResources().getString(R.string.place_created_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChooseDate(String oldDate) {
        DatePickerFragment picker = DatePickerFragment.getInstance(oldDate);
        picker.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override
    public void onDatePicked(String newDate) {
        if (dateSetterListener != null) dateSetterListener.onNewDate(newDate);
    }

    @Override
    public void onNavigateBack() {
        getSupportFragmentManager().popBackStack();
    }

    class DataWorkerServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dataWorkerService = ((DataWorkerService.DataWorkerBinder) service).getService();
            bound = true;
            dataWorkerService.getData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    }

}
