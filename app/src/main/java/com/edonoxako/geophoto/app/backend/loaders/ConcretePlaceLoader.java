package com.edonoxako.geophoto.app.backend.loaders;

import android.content.Context;

import android.content.CursorLoader;
import android.database.Cursor;
import com.edonoxako.geophoto.app.backend.DAO;

public class ConcretePlaceLoader extends CursorLoader {

    int placeId;
    Context context;

    public ConcretePlaceLoader(Context context, int placeId) {
        super(context);
        this.context = context;
        this.placeId = placeId;
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = DAO.getInstance(context).loadPlace(placeId);
        return cursor;
    }
}
