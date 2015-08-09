package com.edonoxako.geophoto.app.backend.callbacks;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.loaders.LoadersFactory;


public class PlaceCallback implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface LoaderListener {

        void onDataFetched(Cursor cursor);
    }

    private Context context;
    LoaderListener listener;

    public PlaceCallback(Context context, LoaderListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        int plId = args.getInt(RepoApp.PLACE_ID_EXTRA);
        return new LoadersFactory().getConcreteDataLoader(context, id, plId);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        listener.onDataFetched(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
