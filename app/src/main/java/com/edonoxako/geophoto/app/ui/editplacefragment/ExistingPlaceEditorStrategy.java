package com.edonoxako.geophoto.app.ui.editplacefragment;

import android.app.Activity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.os.Bundle;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.adapters.PhotoEditGridAdapter;
import com.edonoxako.geophoto.app.backend.DataBase;
import com.edonoxako.geophoto.app.backend.PlaceData;
import com.edonoxako.geophoto.app.backend.callbacks.PlaceCallback;
import com.edonoxako.geophoto.app.backend.loaders.LoadersFactory;

import java.util.List;

public class ExistingPlaceEditorStrategy implements PlaceEditorStrategy {

    private int placeId;
    private EditPlaceFragment.EditPlaceListener listener;
    private Activity activity;
    private EditPlaceFragment.ViewHolder holder;
    private PhotoEditGridAdapter adapter;

    public ExistingPlaceEditorStrategy(Activity activity, int placeId, EditPlaceFragment.EditPlaceListener listener) {
        this.placeId = placeId;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    public void init() {
        adapter = new PhotoEditGridAdapter(activity, RepoApp.getInstance().obtainPhotos(), listener);
    }

    @Override
    public void setViewHolder(EditPlaceFragment.ViewHolder holder) {
        this.holder = holder;
    }

    @Override
    public void performEdit(PlaceData changedPlace) {
        if (listener != null) listener.onSavePlaceChanges(placeId, changedPlace);
    }

    @Override
    public void displayData() {
        holder.editPhotoGridView.setAdapter(adapter);
        if (RepoApp.getInstance().obtainPhotos().isEmpty()) {
            LoaderManager.LoaderCallbacks photoCallbacks = new PlaceCallback(activity, new PlaceCallback.LoaderListener() {
                @Override
                public void onDataFetched(Cursor cursor) {
                    cursor.moveToFirst();
                    if (cursor.getCount() != 0) {
                        RepoApp.getInstance().obtainPhotos().add(cursor.getString(cursor.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN)));
                        while (cursor.moveToNext()) {
                            RepoApp.getInstance().obtainPhotos().add(cursor.getString(cursor.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN)));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            Bundle photoArgs = new Bundle();
            photoArgs.putInt(RepoApp.PLACE_ID_EXTRA, placeId);
            activity.getLoaderManager().initLoader(LoadersFactory.FETCH_PHOTOS_LOADER, photoArgs, photoCallbacks);
        }

        LoaderManager.LoaderCallbacks placeCallback = new PlaceCallback(activity, new PlaceCallback.LoaderListener() {
            @Override
            public void onDataFetched(Cursor cursor) {
                cursor.moveToFirst();
                holder.editLatitudeText.setText(cursor.getString(cursor.getColumnIndex(DataBase.PLACES_LATITUDE_COLUMN)));
                holder.editLongitudeText.setText(cursor.getString(cursor.getColumnIndex(DataBase.PLACES_LONGITUDE_COLUMN)));
                holder.dateEditText.setText(cursor.getString(cursor.getColumnIndex(DataBase.PLACES_LAST_VISITED_COLUMN)));
                holder.editDescriptionText.setText(cursor.getString(cursor.getColumnIndex(DataBase.PLACES_TEXT_COLUMN)));
            }
        });

        Bundle placeArgs = new Bundle();
        placeArgs.putInt(RepoApp.PLACE_ID_EXTRA, placeId);
        activity.getLoaderManager().initLoader(LoadersFactory.FETCH_PLACE_LOADER, placeArgs, placeCallback);
    }

    @Override
    public void removePhoto(int position) {
        RepoApp.getInstance().obtainPhotos().remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addPhoto(String newPhotoPath) {
        RepoApp.getInstance().obtainPhotos().add(newPhotoPath);
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<String> getPhotos() {
        return RepoApp.getInstance().obtainPhotos();
    }

    @Override
    public void updateDate(String newDate) {
        holder.dateEditText.setText(newDate);
    }
}
