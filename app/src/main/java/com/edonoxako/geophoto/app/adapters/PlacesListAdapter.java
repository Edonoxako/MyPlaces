package com.edonoxako.geophoto.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.backend.DataBase;
import com.edonoxako.geophoto.app.ui.SquaredImageView;
import com.squareup.picasso.Picasso;


public class PlacesListAdapter extends CursorAdapter {

    LayoutInflater inflater;

    static class ViewHolder {
        TextView placeDescriptionItemTextView;
        TextView dateItemTextView;
        SquaredImageView listItemImage;
    }


    public PlacesListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.geo_list_item, null, false);
        ViewHolder holder = new ViewHolder();
        holder.placeDescriptionItemTextView = (TextView) view.findViewById(R.id.placeDescriptionItemTextView);
        holder.dateItemTextView = (TextView) view.findViewById(R.id.dateItemTextView);
        holder.listItemImage = (SquaredImageView) view.findViewById(R.id.listItemImage);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String description = cursor.getString(cursor.getColumnIndex(DataBase.PLACES_TEXT_COLUMN));
        String date = cursor.getString(cursor.getColumnIndex(DataBase.PLACES_LAST_VISITED_COLUMN));
        String path = null;

        if (!cursor.isNull(cursor.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN))) {
            path = cursor.getString(cursor.getColumnIndex(DataBase.PHOTOS_PATH_COLUMN));
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.placeDescriptionItemTextView.setText(description);
        holder.dateItemTextView.setText(date);

        if (path != null) {
            Picasso.with(context).load(path).resize(100, 100).centerCrop().into(holder.listItemImage);
        } else {
            Picasso.with(context).load(R.drawable.ic_add_photo).resize(100, 100).centerCrop().into(holder.listItemImage);
        }
    }
}
