package com.edonoxako.geophoto.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.PlaceData;
import com.edonoxako.geophoto.app.ui.SquaredImageView;
import com.squareup.picasso.Picasso;


public class GeoAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;

    static class ViewHolder {
        TextView placeDescriptionItemTextView;
        TextView dateItemTextView;
        SquaredImageView listItemImage;
    }

    public GeoAdapter(Activity activity) {
        this.activity = activity;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return RepoApp.getInstance().getPlaces().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.geo_list_item, parent, false);
            holder = new ViewHolder();
            holder.placeDescriptionItemTextView = (TextView) view.findViewById(R.id.placeDescriptionItemTextView);
            holder.dateItemTextView = (TextView) view.findViewById(R.id.dateItemTextView);
            holder.listItemImage = (SquaredImageView) view.findViewById(R.id.listItemImage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        PlaceData place = RepoApp.getInstance().getPlaces().get(position);
        holder.placeDescriptionItemTextView.setText(place.getText());
        holder.dateItemTextView.setText(place.getLastVisited());

        if (!place.getAllPhotos().isEmpty()) {
            Picasso.with(activity).load(place.getPhoto(0)).resize(100, 100).centerCrop().into(holder.listItemImage);
        } else {
            Picasso.with(activity).load(R.drawable.ic_add_photo).resize(100, 100).centerCrop().into(holder.listItemImage);
        }

        return view;
    }
}
