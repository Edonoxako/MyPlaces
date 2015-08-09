package com.edonoxako.geophoto.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.ui.editplacefragment.EditPlaceFragment;
import com.edonoxako.geophoto.app.ui.SquaredImageButton;
import com.edonoxako.geophoto.app.ui.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PhotoEditGridAdapter extends BaseAdapter {

    private Context context;
    private EditPlaceFragment.EditPlaceListener listener;
    private List<String> paths;

    public PhotoEditGridAdapter(Context context, List<String> paths, EditPlaceFragment.EditPlaceListener listener) {
        this.context = context;
        this.paths = paths;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return paths.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position == paths.size()) {
            ImageButton addBtn = new SquaredImageButton(context);
            addBtn.setImageResource(R.drawable.ic_add_photo);
            addBtn.setAdjustViewBounds(true);
            addBtn.setPadding(3, 3, 3, 3);
            addBtn.setBackgroundResource(R.drawable.button);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditPhotoClick();
                }
            });

            return addBtn;
        }

        ImageView imageView;
        if (convertView == null || convertView instanceof ImageButton) {
            imageView = new SquaredImageView(context);
        } else {
            imageView = (SquaredImageView) convertView;
        }

        Picasso.with(context)
                .load(paths.get(position))
                .resize((int) context.getResources().getDimension(R.dimen.grid_cell_width),
                        (int) context.getResources().getDimension(R.dimen.grid_cell_width))
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}
