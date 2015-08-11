package com.edonoxako.geophoto.app.backend;

import java.util.ArrayList;
import java.util.List;


public class PlaceData {

    private int id;
    private double latitude;
    private double longitude;
    private String text;
    private String lastVisited;
    private List<String> photos;

    public PlaceData() {
        photos = new ArrayList<String>();
        text = "";
        lastVisited = "";
    }

    public PlaceData(int id, double latitude, double longitude, String text, String lastVisited, List<String> photos) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.text = text;
        this.lastVisited = lastVisited;
        this.photos = photos;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(String lastVisited) {
        this.lastVisited = lastVisited;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public void addPhoto(String photoPath) {
        photos.add(photoPath);
    }

    public String getPhoto(int photoId) {
        return photos.get(photoId);
    }

    public List<String> getAllPhotos() {
        return photos;
    }

    public void removePhoto(int id) {
        if (id < photos.size()) photos.remove(id);
    }

}
