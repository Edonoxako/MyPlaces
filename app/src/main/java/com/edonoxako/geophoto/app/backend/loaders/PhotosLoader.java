package com.edonoxako.geophoto.app.backend.loaders;

import android.content.Context;

import android.content.CursorLoader;
import android.database.Cursor;
import com.edonoxako.geophoto.app.backend.DAO;


public class PhotosLoader extends CursorLoader {

    private int placeId;
    private Context context;

    public PhotosLoader(Context context, int placeId) {
        super(context);
        this.placeId = placeId;
        this.context = context;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = DAO.getInstance(context).loadPhotos(placeId);
        return cursor;
    }
}
