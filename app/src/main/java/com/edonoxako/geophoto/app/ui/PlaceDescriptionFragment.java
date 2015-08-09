package com.edonoxako.geophoto.app.ui;

import android.app.Activity;


import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.adapters.PhotoGridAdapter;
import com.edonoxako.geophoto.app.backend.DataBase;
import com.edonoxako.geophoto.app.backend.callbacks.PlaceCallback;
import com.edonoxako.geophoto.app.backend.loaders.LoadersFactory;
import com.edonoxako.geophoto.app.ui.interfaces.BackNavigateListener;
import com.edonoxako.geophoto.app.ui.interfaces.PresenterActivityListener;


public class PlaceDescriptionFragment extends Fragment implements PresenterActivityListener {

    public interface PlaceDescriptionListener {

        void setPlaceDescriptionFragment(PlaceDescriptionFragment fragment);
        void onEditGeoClick(int id);
        void onShowPhotoClick(int placeId, int index);
        void onPlaceRemove(int placeId);
    }
    private PlaceDescriptionListener listener;
    private BackNavigateListener backNavigateListener;
    private PhotoGridAdapter adapter;

    private GridView photoGridView;
    private TextView descriptionTextView;

    private TextView lastVisitedTextView;
    private int mPlaceId;

    public PlaceDescriptionFragment() {
    }

    public static PlaceDescriptionFragment newInstance(int placeId) {
        PlaceDescriptionFragment fragment = new PlaceDescriptionFragment();
        Bundle args = new Bundle();
        args.putInt(RepoApp.PLACE_ID_EXTRA, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (PlaceDescriptionListener) activity;
            backNavigateListener = (BackNavigateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement PlaceDescriptionListener and BackNavigateListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPlaceId = getArguments().getInt(RepoApp.PLACE_ID_EXTRA);
        adapter = new PhotoGridAdapter(getActivity(), null, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.place_description_fragment, container, false);

        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        lastVisitedTextView = (TextView) view.findViewById(R.id.lastVisitTextView);

        photoGridView = (GridView) view.findViewById(R.id.photoGridView);
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onShowPhotoClick(mPlaceId, position);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listener.setPlaceDescriptionFragment(this);

        LoaderManager.LoaderCallbacks placeCallback = new PlaceCallback(getActivity(), new PlaceCallback.LoaderListener() {
            @Override
            public void onDataFetched(Cursor cursor) {
                if (cursor.moveToFirst()) {
                    descriptionTextView.setText(cursor.getString(cursor.getColumnIndex(DataBase.PLACES_TEXT_COLUMN)));
                    lastVisitedTextView.setText(cursor.getString(cursor.getColumnIndex(DataBase.PLACES_LAST_VISITED_COLUMN)));
                }
            }
        });
        getActivity().getLoaderManager().restartLoader(LoadersFactory.FETCH_PLACE_LOADER, getArguments(), placeCallback);

        LoaderManager.LoaderCallbacks photosCallback = new PlaceCallback(getActivity(), new PlaceCallback.LoaderListener() {
            @Override
            public void onDataFetched(Cursor cursor) {
                adapter.swapCursor(cursor);
            }
        });
        getActivity().getLoaderManager().restartLoader(LoadersFactory.FETCH_PHOTOS_LOADER, getArguments(), photosCallback);

        photoGridView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.place_description_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_edit_place) {
            listener.onEditGeoClick(mPlaceId);
        } else if (id == R.id.remove_place){
            listener.onPlaceRemove(mPlaceId);
        } else if (id == android.R.id.home) {
            backNavigateListener.onNavigateBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlacesLoaded() {
        getActivity().getLoaderManager().getLoader(LoadersFactory.FETCH_PHOTOS_LOADER).forceLoad();
        getActivity().getLoaderManager().getLoader(LoadersFactory.FETCH_PLACE_LOADER).forceLoad();
        adapter.notifyDataSetChanged();
    }

}
