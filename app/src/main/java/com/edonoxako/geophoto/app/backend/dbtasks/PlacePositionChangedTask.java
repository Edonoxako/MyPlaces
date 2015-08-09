package com.edonoxako.geophoto.app.backend.dbtasks;

import android.content.Context;
import com.edonoxako.geophoto.app.backend.DAO;


public class PlacePositionChangedTask extends BaseTask {

    int placeId;
    double longitude;
    double latitude;

    public PlacePositionChangedTask(Context context, int placeId, double longitude, double latitude) {
        super(context);
        this.placeId = placeId;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public void runTask() {
        DAO.getInstance(context).changePlaceCoords(placeId, latitude, longitude);
    }
}
