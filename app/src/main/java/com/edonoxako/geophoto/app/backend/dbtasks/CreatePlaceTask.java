package com.edonoxako.geophoto.app.backend.dbtasks;

import android.content.Context;
import com.edonoxako.geophoto.app.backend.DAO;
import com.edonoxako.geophoto.app.backend.PlaceData;


public class CreatePlaceTask extends ChangePlaceTask {

    public CreatePlaceTask(Context context, PlaceData place) {
        super(context, place);
    }

    @Override
    public void runTask() {
        DAO.getInstance(context).save(place);
    }
}
