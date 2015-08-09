package com.edonoxako.geophoto.app.backend.dbtasks;

import android.content.Context;
import com.edonoxako.geophoto.app.backend.DAO;
import com.edonoxako.geophoto.app.backend.PlaceData;


public class ChangePlaceTask extends BaseTask {

    protected PlaceData place;

    public ChangePlaceTask(Context context, PlaceData place) {
        super(context);
        this.place = place;
    }

    @Override
    public void runTask() {
        DAO.getInstance(context).changePlace(place);
    }
}
