package com.edonoxako.geophoto.app.ui.editplacefragment;

import com.edonoxako.geophoto.app.backend.PlaceData;

import java.util.List;


public interface PlaceEditorStrategy {
    void init();
    void setViewHolder(EditPlaceFragment.ViewHolder holder);
    void performEdit(PlaceData changedPlace);
    void displayData();
    void removePhoto(int position);
    void addPhoto(String newPhotoPath);
    List<String> getPhotos();
    void updateDate(String newDate);
}
