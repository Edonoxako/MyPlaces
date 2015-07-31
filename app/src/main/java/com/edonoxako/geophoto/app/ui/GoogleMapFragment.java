package com.edonoxako.geophoto.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.edonoxako.geophoto.app.ui.interfaces.DetailedDescriptionInterface;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.PlaceData;
import com.edonoxako.geophoto.app.ui.interfaces.PresenterActivityListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class GoogleMapFragment extends SupportMapFragment implements PresenterActivityListener {

    public interface GoogleMapListener extends DetailedDescriptionInterface {

        void setMapFragment(GoogleMapFragment fragment);
        void onAddNewPlace(double latitude, double longitude);
        void onViewList();
        void onMarkPositionChanged(int placeId, double latitude, double longitude);
    }
    private GoogleMap map;

    private GoogleMapListener listener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (GoogleMapListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement GoogleMapListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setHasOptionsMenu(true);
        listener.setMapFragment(this);

        map = getMap();
        if (map == null) {
            Toast.makeText(getActivity(), R.string.error_msg, Toast.LENGTH_SHORT).show();
            return;
        }

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                listener.onAddNewPlace(latLng.latitude, latLng.longitude);
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String strId = marker.getSnippet();
                int id = Integer.valueOf(strId);
                listener.onShowPlaceInfo(id);
                return true;
            }
        });

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng pos = marker.getPosition();
                int id = Integer.valueOf(marker.getSnippet());
                listener.onMarkPositionChanged(id, pos.latitude, pos.longitude);
            }
        });

        onPlacesLoaded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_list) {
            listener.onViewList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addMarker(double latitude, double longitude, int id) {
        LatLng latLng = new LatLng(latitude, longitude);
        Marker marker = map.addMarker(new MarkerOptions().position(latLng));
        marker.setSnippet(String.valueOf(id));
        marker.setDraggable(true);
    }

    @Override
    public void onPlacesLoaded() {
        map.clear();
        List<PlaceData> places = RepoApp.getInstance().getPlaces();
        for (int i = 0; i < places.size(); i++) {
            PlaceData place = places.get(i);
            addMarker(place.getLatitude(), place.getLongitude(), i);
        }
    }
}
