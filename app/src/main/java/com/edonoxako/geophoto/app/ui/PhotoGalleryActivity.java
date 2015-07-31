package com.edonoxako.geophoto.app.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.edonoxako.geophoto.app.MainActivity;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.backend.PlaceData;


public class PhotoGalleryActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mPager;
    private PlaceData currentPlace;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_gallery_activity);

        int id = getIntent().getIntExtra(MainActivity.CURRENT_PLACE_ID, 0);
        currentPlace = RepoApp.getInstance().getPlaces().get(id);
        
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.gallery);
        mPager.setAdapter(adapter);
        mPager.setOffscreenPageLimit(2);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final ActionBar bar = getSupportActionBar();

        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayHomeAsUpEnabled(true);

        mPager.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                    bar.hide();
                } else {
                    bar.show();
                }
            }
        });

        int position = getIntent().getIntExtra(MainActivity.PHOTO_GRID_POSITION, -1);
        if (position > -1) {
            mPager.setCurrentItem(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }


    public class PhotoPagerAdapter extends FragmentStatePagerAdapter {

        public PhotoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoGalleryItemFragment.newInstance(currentPlace.getPhoto(position));
        }

        @Override
        public int getCount() {
            return currentPlace.photosCount();
        }
    }

}
