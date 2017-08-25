package org.freefinder.osmdroid;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.freefinder.activities.PlaceDetailActivity;
import org.freefinder.model.Place;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

/**
 * Created by rade on 25.8.17..
 */

public class PlaceInfoWindow extends MarkerInfoWindow {

    private Place place;

    public PlaceInfoWindow(MapView mapView) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);

        Button btn = (Button)(mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo));
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(place != null) {
                    Intent placeDetailIntent = new Intent(view.getContext(), PlaceDetailActivity.class);
                    placeDetailIntent.putExtra("placeName", place.getName());
                    view.getContext().startActivity(placeDetailIntent);
                }
            }
        });

    }

    @Override
    public void onOpen(Object item){
        super.onOpen(item);
        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo).setVisibility(View.VISIBLE);

        Marker marker = (Marker)item;
        place = (Place)marker.getRelatedObject();
    }


}
