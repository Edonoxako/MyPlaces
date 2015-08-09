package com.edonoxako.geophoto.app.backend.dbtasks;

import android.content.Context;
import com.edonoxako.geophoto.app.backend.DAO;


public class DeletePlaceTask extends BaseTask {

    private int placeId;

    public DeletePlaceTask(Context context, int placeId) {
        super(context);
        this.placeId = placeId;
    }

    @Override
    public void runTask() {
        DAO.getInstance(context).delete(placeId);
    }
}
