package com.edonoxako.geophoto.app.ui.editplacefragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.edonoxako.geophoto.app.InputValidator;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.PlaceData;
import com.edonoxako.geophoto.app.ui.interfaces.BackNavigateListener;
import com.edonoxako.geophoto.app.ui.interfaces.DateSetterListener;
import com.edonoxako.geophoto.app.ui.interfaces.PhotoFetcherListener;


public class EditPlaceFragment extends Fragment implements PhotoFetcherListener, DateSetterListener {

    public interface EditPlaceListener {
        void setEditPlaceFragment(EditPlaceFragment fragment);
        void onEditPhotoClick();
        void onSavePlaceChanges(int id, PlaceData changedPlace);
        void onSaveNewPlace(PlaceData newPlace);
        void onChooseDate(String oldDate);
    }

    private EditPlaceListener listener;
    private BackNavigateListener backNavigateListener;
    private PlaceEditorStrategy editor;
    private ViewHolder holder;

    private int mPlaceId;

    public static final String PLACE_ID_ARGUMENT = "placeId";

    public static final String LATITUDE_ARGUMENT = "latitude";
    public static final String LONGITUDE_ARGUMENT = "longitude";

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
            editor = new NewPlaceEditorStrategy(getActivity(), getArguments().getDouble(LONGITUDE_ARGUMENT), getArguments().getDouble(LATITUDE_ARGUMENT), listener);
        } else {
            editor = new ExistingPlaceEditorStrategy(getActivity(), mPlaceId, listener);
        }
        editor.init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_geo_fragment_layout, container, false);

        holder = new ViewHolder();

        holder.editDescriptionText = (EditText) view.findViewById(R.id.editDescriptionText);
        holder.editLatitudeText = (EditText) view.findViewById(R.id.latitudeEditText);
        holder.editLongitudeText = (EditText) view.findViewById(R.id.longitudeEditText);
        holder.dateEditText = (EditText) view.findViewById(R.id.dateEditText);

        view.findViewById(R.id.calendarBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChooseDate(holder.dateEditText.getText().toString());
            }
        });

        holder.editPhotoGridView = (GridView) view.findViewById(R.id.editPhotoGridView);
        registerForContextMenu(holder.editPhotoGridView);
        editor.setViewHolder(holder);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listener.setEditPlaceFragment(this);
        editor.displayData();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.grid_context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int pos = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        editor.removePhoto(pos);
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
            PlaceData place = gatherData();
            if (place != null) editor.performEdit(place);
        } else if (item.getItemId() == android.R.id.home) {
            hideKeyboard();
            backNavigateListener.onNavigateBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private PlaceData gatherData() {
        PlaceData place = new PlaceData();

        InputValidator validator = new InputValidator(getActivity());
        validator.addTexts(holder.editDescriptionText);
        validator.addNumbers(holder.editLongitudeText, -180, 180);
        validator.addNumbers(holder.editLatitudeText, -90, 90);
        validator.addDates(holder.dateEditText);

        if (validator.validate()) {
            place.setId(mPlaceId);
            place.setText(holder.editDescriptionText.getText().toString());
            place.setLatitude(Double.valueOf(holder.editLatitudeText.getText().toString()));
            place.setLongitude(Double.valueOf(holder.editLongitudeText.getText().toString()));
            place.setPhotos(editor.getPhotos());
            place.setLastVisited(holder.dateEditText.getText().toString());
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
        editor.addPhoto(newPhoto);
    }

    @Override
    public void onNewDate(String newDate) {
        editor.updateDate(newDate);
    }

    public class ViewHolder {
        EditText editDescriptionText;
        EditText editLatitudeText;
        EditText editLongitudeText;
        GridView editPhotoGridView;
        EditText dateEditText;
    }
}
