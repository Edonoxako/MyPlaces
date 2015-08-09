package com.edonoxako.geophoto.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.DataBase;
import com.edonoxako.geophoto.app.ui.SquaredImageView;
import com.squareup.picasso.Picasso;


public class PhotoGridAdapter extends CursorAdapter {

    public PhotoGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new SquaredImageView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SquaredImageView imageView = (SquaredImageView) view;
        Picasso.with(context)
                .load(cursor.getString(cursor.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN)))
                .resize((int) context.getResources().getDimension(R.dimen.grid_cell_width),
                        (int) context.getResources().getDimension(R.dimen.grid_cell_width))
                .centerCrop()
                .into(imageView);
    }
}
