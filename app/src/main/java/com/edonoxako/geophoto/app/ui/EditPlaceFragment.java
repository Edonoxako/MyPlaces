package com.edonoxako.geophoto.app.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.edonoxako.geophoto.app.InputValidator;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.adapters.PhotoEditGridAdapter;
import com.edonoxako.geophoto.app.backend.PlaceData;
import com.edonoxako.geophoto.app.ui.interfaces.BackNavigateListener;
import com.edonoxako.geophoto.app.ui.interfaces.DateSetterListener;
import com.edonoxako.geophoto.app.ui.interfaces.PhotoFetcherListener;
import com.edonoxako.geophoto.app.ui.interfaces.PresenterActivityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EditPlaceFragment extends Fragment implements PhotoFetcherListener, PresenterActivityListener, DateSetterListener {

    public interface EditPlaceListener {
        void setEditPlaceFragment(EditPlaceFragment fragment);
        void onEditPhotoClick();
        void onSavePlaceChanges(int id, PlaceData changedPlace);
        void onSaveNewPlace(PlaceData newPlace);
        void onChooseDate(String oldDate);
    }

    private EditPlaceListener listener;
    private BackNavigateListener backNavigateListener;

    private PhotoEditGridAdapter adapter;
    private int mPlaceId;

    private double mLongitude;
    private double mLatitude;
    private boolean creatingNewPlace = false;
    private EditText editDescriptionText;

    private EditText editLatitudeText;
    private EditText editLongitudeText;
    private GridView editPhotoGridView;
    private EditText dateEditText;

    public static final String PLACE_ID_ARGUMENT = "placeId";

    public static final String LATITUDE_ARGUMENT = "latitude";
    public static final String LONGITUDE_ARGUMENT = "longitude";
    private List<String> photoPaths;

    public EditPlaceFragment() {
    }

    public static EditPlaceFragment newInstance(int placeId) {
        EditPlaceFragment fragment = new EditPlaceFragment();
        Bundle args = new Bundle();
        args.putInt(PLACE_ID_ARGUMENT, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    public static EditPlaceFragment newInstance(double latitude, double longitude) {
        EditPlaceFragment fragment = new EditPlaceFragment();
        Bundle args = new Bundle();
        args.putDouble(LATITUDE_ARGUMENT, latitude);
        args.putDouble(LONGITUDE_ARGUMENT, longitude);
        args.putInt(PLACE_ID_ARGUMENT, -1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (EditPlaceListener) activity;
            backNavigateListener = (BackNavigateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EditPlaceListener and BackNavigateListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPlaceId = getArguments().getInt(PLACE_ID_ARGUMENT);

        if (mPlaceId == -1) {
            creatingNewPlace = true;
            mLatitude = getArguments().getDouble(LATITUDE_ARGUMENT);
            mLongitude = getArguments().getDouble(LONGITUDE_ARGUMENT);
            photoPaths = new ArrayList<String>();
        } else {
            photoPaths = new ArrayList<String>(RepoApp.getInstance().getPlaces().get(mPlaceId).getAllPhotos());
        }

        adapter = new PhotoEditGridAdapter(getActivity(), photoPaths, listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_geo_fragment_layout, container, false);

        editDescriptionText = (EditText) view.findViewById(R.id.editDescriptionText);
        editLatitudeText = (EditText) view.findViewById(R.id.latitudeEditText);
        editLongitudeText = (EditText) view.findViewById(R.id.longitudeEditText);
        dateEditText = (EditText) view.findViewById(R.id.dateEditText);

        view.findViewById(R.id.calendarBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChooseDate(dateEditText.getText().toString());
            }
        });

        editPhotoGridView = (GridView) view.findViewById(R.id.editPhotoGridView);
        registerForContextMenu(editPhotoGridView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listener.setEditPlaceFragment(this);
        editPhotoGridView.setAdapter(adapter);

        if (creatingNewPlace) {
            editLatitudeText.setText(String.valueOf(mLatitude));
            editLongitudeText.setText(String.valueOf(mLongitude));
            dateEditText.setText(DateFormat.format("dd.MM.yyyy", new Date()).toString());

        } else {
            PlaceData place = RepoApp.getInstance().getPlaces().get(mPlaceId);
            editDescriptionText.setText("Type description here");
            editDescriptionText.setText(place.getText());
            editLatitudeText.setText(String.valueOf(place.getLatitude()));
            editLongitudeText.setText(String.valueOf(place.getLongitude()));
            dateEditText.setText(place.getLastVisited());
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.grid_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        photoPaths.remove(pos);
        adapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_place_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_place_action) {
            hideKeyboard();
            if (creatingNewPlace) {
                PlaceData newPlace = gatherData();
                if (newPlace != null)listener.onSaveNewPlace(newPlace);
            } else {
                PlaceData changedPlace = gatherData();
                if (changedPlace != null)listener.onSavePlaceChanges(mPlaceId, changedPlace);
            }
        } else if (item.getItemId() == android.R.id.home) {
            hideKeyboard();
            backNavigateListener.onNavigateBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private PlaceData gatherData() {
        PlaceData place = new PlaceData();

        InputValidator validator = new InputValidator(getActivity());
        validator.addTexts(editDescriptionText);
        validator.addNumbers(editLongitudeText, -180, 180);
        validator.addNumbers(editLatitudeText, -90, 90);
        validator.addDates(dateEditText);

        if (validator.validate()) {
            place.setText(editDescriptionText.getText().toString());
            place.setLatitude(Double.valueOf(editLatitudeText.getText().toString()));
            place.setLongitude(Double.valueOf(editLongitudeText.getText().toString()));
            place.setPhotos(photoPaths);
            place.setLastVisited(dateEditText.getText().toString());
            return place;
        } else {
            return null;
        }
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPhotoFetched() {
        String newPhoto = RepoApp.getInstance().getNewPhotoPath();
        photoPaths.add(newPhoto);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlacesLoaded() {
        adapter.update();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNewDate(String newDate) {
        dateEditText.setText(newDate);
    }
}
