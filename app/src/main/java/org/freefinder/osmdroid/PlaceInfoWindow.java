package org.freefinder.osmdroid;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.activities.PlaceDetailActivity;
import org.freefinder.model.Place;
import org.freefinder.shared.ImageEncoder;
import org.freefinder.shared.Util;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

/**
 * Created by rade on 25.8.17..
 */

public class PlaceInfoWindow extends MarkerInfoWindow {
    private static final String TAG = PlaceInfoWindow.class.getSimpleName();

    public PlaceInfoWindow(MapView mapView) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);
    }

    @Override
    public void onOpen(Object item){
        super.onOpen(item);

        Marker marker = (Marker) item;
        final Place place = (Place) marker.getRelatedObject();

        TextView txtTitle = (TextView) mView.findViewById(R.id.bubble_title);
        TextView txtDescription = (TextView) mView.findViewById(R.id.bubble_description);
        TextView txtSubdescription = (TextView) mView.findViewById(R.id.bubble_subdescription);
        ImageView imgThumbnail = (ImageView) mView.findViewById(R.id.bubble_image);

        txtTitle.setText(place.getName());
        final String shortenedDescription = Util.descriptionShortener(place.getDescription());
        txtDescription.setText(shortenedDescription);

        imgThumbnail.setImageBitmap(ImageEncoder.decodeImage(place.getEncodedImage()));
        imgThumbnail.setVisibility(View.VISIBLE);

        Button btnMoreInfo = (Button) (mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo));
        btnMoreInfo.setVisibility(Button.VISIBLE);
        ViewGroup.LayoutParams layoutParams = btnMoreInfo.getLayoutParams();
        layoutParams.height = 80;
        layoutParams.width = 80;
        btnMoreInfo.setLayoutParams(layoutParams);
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent placeDetailIntent = new Intent(view.getContext(), PlaceDetailActivity.class);
                placeDetailIntent.putExtra("id", place.getId());
                view.getContext().startActivity(placeDetailIntent);
            }
        });
    }

}
