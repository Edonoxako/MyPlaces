package com.edonoxako.geophoto.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.edonoxako.geophoto.app.R;
import com.squareup.picasso.Picasso;


public class PhotoGalleryItemFragment extends Fragment {

    private static final String PHOTO_PATH_EXTRA = "photoPath";

    private String photoPath;
    private ImageView imgView;
    private View.OnClickListener listener;

    public PhotoGalleryItemFragment(){
    }

    public static PhotoGalleryItemFragment newInstance(String path) {
        PhotoGalleryItemFragment f = new PhotoGalleryItemFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_PATH_EXTRA, path);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (View.OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoPath = getArguments().getString(PHOTO_PATH_EXTRA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_gallery_item_fragment, container, false);
        imgView = (ImageView) view.findViewById(R.id.photo);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imgView.setOnClickListener(listener);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = (height > width ? height : width);

        Picasso.with(getActivity()).load(photoPath).resize(longest, longest).centerInside().into(imgView);
    }
}
