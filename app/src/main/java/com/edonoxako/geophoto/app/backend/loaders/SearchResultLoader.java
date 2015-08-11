package com.edonoxako.geophoto.app.backend.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import com.edonoxako.geophoto.app.backend.DAO;


public class SearchResultLoader extends CursorLoader {

    String placeName;

    public SearchResultLoader(Context context, String placeName) {
        super(context);
        this.placeName = placeName;
    }

    @Override
    public Cursor loadInBackground() {
        return DAO.getInstance(getContext()).loadSearchResultPlaces(placeName);
    }
}
