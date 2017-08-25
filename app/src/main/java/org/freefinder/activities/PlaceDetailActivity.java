package org.freefinder.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.freefinder.R;
import org.freefinder.api.RatingApi;
import org.freefinder.model.Place;
import org.freefinder.model.Rating;
import org.freefinder.shared.ImageEncoder;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class PlaceDetailActivity extends AppCompatActivity {

    @BindView(R.id.place_image)         ImageView placeImageView;
    @BindView(R.id.place_name)          TextView placeNameTextView;
    @BindView(R.id.place_category)      TextView placeCategoryTextView;
    @BindViews({ R.id.like_button,
                 R.id.dislike_button }) List<ImageButton> buttons;
    @BindViews({ R.id.likes_count,
                 R.id.dislikes_count }) List<TextView> textViews;

    @BindView(R.id.toolbar)             Toolbar toolbar;
    @BindView(R.id.fab)                 FloatingActionButton fab;

    private Realm realm = Realm.getDefaultInstance();
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String placeName = getIntent().getStringExtra("placeName");
        place = realm.where(Place.class)
                     .equalTo("name", placeName)
                     .findFirst();

        placeImageView.setImageBitmap(ImageEncoder.decodeImage(place.getEncodedImage()));
        placeNameTextView.setText(place.getName());
        placeCategoryTextView.setText(place.getCategory().getName());
        ButterKnife.apply(textViews, INITIALIZE_COUNTERS, place);

        Rating rating = realm.where(Rating.class)
                             .equalTo("place_id", place.getId())
                             .findFirst();

        if(rating != null) {
            int id = 0;
            switch (rating.getRating()) {
                case "like":
                    id = R.id.like_button;
                    break;
                case "dislike":
                    id = R.id.dislike_button;
                    break;
            }

            final ImageButton button = ButterKnife.findById(this, id);
            button.getDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            ButterKnife.apply(buttons, DISABLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    static final ButterKnife.Setter<View, Place> INITIALIZE_COUNTERS = new ButterKnife.Setter<View, Place>() {
        @Override
        public void set(View view, Place place, int index) {
            switch(view.getId()) {
                case R.id.likes_count:
                    ((TextView) view).setText(String.valueOf(place.getLikes()));
                    break;
                case R.id.dislikes_count:
                    ((TextView) view).setText(String.valueOf(place.getDislikes()));
                    break;
            }
        }
    };

    static final ButterKnife.Action<View> DISABLE = new ButterKnife.Action<View>() {
        @Override public void apply(View view, int index) {
            view.setEnabled(false);
        }
    };

    @OnClick({ R.id.like_button, R.id.dislike_button })
    public void rateSubmit(ImageButton button) {
        RatingApi.RatingService ratingService = new RatingApi.RatingService();
        String rating;

        switch(button.getId()) {
            case R.id.like_button:
                rating = "like";
                updateCounter(R.id.likes_count, place.getLikes() + 1);
                break;
            case R.id.dislike_button:
                rating = "dislike";
                updateCounter(R.id.dislikes_count, place.getDislikes() + 1);
                break;
            default:
                throw new RuntimeException("No provided action for the given view id!");
        }

        button.getDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        ButterKnife.apply(buttons, DISABLE);
        ratingService.startService(this, place, rating);
    }

    private void updateCounter(int counterId, int count) {
        TextView countTextView = ButterKnife.findById(this, counterId);
        countTextView.setText(String.valueOf(count));
    }
}
