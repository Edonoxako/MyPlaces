package com.edonoxako.geophoto.app.backend.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import com.edonoxako.geophoto.app.backend.DAO;


public class PlacesLoader extends CursorLoader {

    private Context context;

    public PlacesLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = DAO.getInstance(context).loadPlacesWhithPhotoThumbnail();
        return cursor;
    }
}
