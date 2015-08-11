package com.edonoxako.geophoto.app.ui.editplacefragment;

import android.content.Context;
import android.text.format.DateFormat;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.adapters.PhotoEditGridAdapter;
import com.edonoxako.geophoto.app.backend.PlaceData;

import java.util.Date;
import java.util.List;


public class NewPlaceEditorStrategy implements PlaceEditorStrategy {

    private double longitude;
    private double latitude;
    private Context context;

    private EditPlaceFragment.ViewHolder holder;
    private EditPlaceFragment.EditPlaceListener listener;
    private PhotoEditGridAdapter adapter;


    public NewPlaceEditorStrategy(Context context, double longitude, double latitude, EditPlaceFragment.EditPlaceListener listener) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void init() {
        adapter = new PhotoEditGridAdapter(context, RepoApp.getInstance().obtainPhotos(), listener);
    }

    @Override
    public void setViewHolder(EditPlaceFragment.ViewHolder holder) {
        this.holder = holder;
    }

    @Override
    public void displayData() {
        holder.editPhotoGridView.setAdapter(adapter);
        holder.editLatitudeText.setText(String.valueOf(latitude));
        holder.editLongitudeText.setText(String.valueOf(longitude));
        holder.dateEditText.setText(DateFormat.format("dd.MM.yyyy", new Date()).toString());
    }

    @Override
    public void performEdit(PlaceData changedPlace) {
        if (listener != null) listener.onSaveNewPlace(changedPlace);
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
