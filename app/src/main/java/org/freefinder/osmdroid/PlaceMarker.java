package org.freefinder.osmdroid;

import android.content.Intent;
import android.view.MotionEvent;

import org.freefinder.activities.PlaceDetailActivity;
import org.freefinder.model.Place;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

/**
 * Created by rade on 23.8.17..
 */

public class PlaceMarker extends Marker {

    private Place place;

    public PlaceMarker(Place place, MapView mapView) {
        super(mapView);

        this.place = place;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e, MapView mapView) {
        Intent placeDetailIntent = new Intent(mapView.getContext(), PlaceDetailActivity.class);
        placeDetailIntent.putExtra("placeName", place.getName());
        mapView.getContext().startActivity(placeDetailIntent);

        return super.onDoubleTap(e, mapView);
    }
}
