package com.edonoxako.geophoto.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.ListView;
import com.edonoxako.geophoto.app.RepoApp;
import com.edonoxako.geophoto.app.adapters.PlacesListAdapter;
import com.edonoxako.geophoto.app.ui.interfaces.DetailedDescriptionInterface;
import com.edonoxako.geophoto.app.R;
import com.edonoxako.geophoto.app.ui.interfaces.PresenterActivityListener;


public class GeoListFragment extends ListFragment implements PresenterActivityListener {

    public interface GeoListListener extends DetailedDescriptionInterface {

        void setListFragment(GeoListFragment fragment);
        void onViewMap();
    }
    private PlacesListAdapter adapter;

    private GeoListListener listener;
    public GeoListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (GeoListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement GeoListListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        listener.setListFragment(this);
        setEmptyText(getActivity().getString(R.string.empty_list_msg));
        adapter = new PlacesListAdapter(getActivity(), RepoApp.getInstance().getPlacesCursor(), true);
        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setBackgroundResource(R.color.listBackground);
    }

    @Override
    public void onStop() {
        super.onStop();
        listener.setListFragment(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listener.onShowPlaceInfo((int) id);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.geo_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_map) {
            listener.onViewMap();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlacesLoaded() {
        adapter.swapCursor(RepoApp.getInstance().getPlacesCursor());
    }
}
