package com.edonoxako.geophoto.app.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.ui.SquaredImageView;
import com.squareup.picasso.Picasso;


public class PhotoGridAdapter extends BaseAdapter {

    private Context context;
    private int placeId;

    public PhotoGridAdapter(Context context, int placeId) {
        this.context = context;
        this.placeId = placeId;
    }

    @Override
    public int getCount() {
        return RepoApp.getInstance().getPlaces().get(placeId).getAllPhotos().size();
    }

    @Override
    public Object getItem(int position) {
        return RepoApp.getInstance().getPlaces().get(placeId).getPhoto(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView imageView;
        if (convertView == null) {
            imageView = new SquaredImageView(context);
        } else {
            imageView = (SquaredImageView) convertView;
        }

        Picasso.with(context)
                .load(RepoApp.getInstance().getPlaces().get(placeId).getPhoto(position))
                .resize((int) context.getResources().getDimension(R.dimen.grid_cell_width),
                        (int) context.getResources().getDimension(R.dimen.grid_cell_width))
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}
