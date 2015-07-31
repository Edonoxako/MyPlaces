package com.edonoxako.geophoto.app.backend;


import android.text.format.DateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PlaceDataMapper {

    public static List<PlaceData> mapFromJSON(JSONObject rawData) {
        try {
            ArrayList<PlaceData> places =  new ArrayList<PlaceData>();
            JSONArray placesArray = rawData.getJSONArray("places");
            for (int i = 0; i < placesArray.length(); i++) {
                JSONObject rawPlace = placesArray.getJSONObject(i);
                PlaceData place = new PlaceData();

                place.setLatitude(rawPlace.getDouble("latitude"));
                place.setLongitude(rawPlace.getDouble("longtitude"));
                place.setText(rawPlace.getString("text"));

                String dateString = rawPlace.getString("lastVisited");
                SimpleDateFormat format = new SimpleDateFormat("E MMM w HH:mm:ss Z yyyy", Locale.ENGLISH);
                Date date = format.parse(dateString);
                place.setLastVisited(DateFormat.format("dd.MM.yyyy", date).toString());

                place.addPhoto(rawPlace.getString("image"));

                places.add(place);
            }

            return places;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
