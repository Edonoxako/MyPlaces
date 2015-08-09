package com.edonoxako.geophoto.app.backend.loaders;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;


public class LoadersFactory {

    public static final int FETCH_PLACE_LOADER = 2;
    public static final int FETCH_PHOTOS_LOADER = 3;

    public Loader getConcreteDataLoader(Context context, int loaderId, int placeId) {

        switch (loaderId) {
            case FETCH_PLACE_LOADER:
                return new ConcretePlaceLoader(context, placeId);

            case FETCH_PHOTOS_LOADER:
                return new PhotosLoader(context, placeId);

            default:
                return null;
        }
    }

    public Loader getAllDataLoader(Context context) {
        return new PlacesLoader(context);
    }

}
