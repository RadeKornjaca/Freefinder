package org.freefinder.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import static org.freefinder.activities.Constants.RESOURCE_ID;
import static org.freefinder.activities.Constants.REVISION_TYPE;

public class PlaceDetailActivity extends AppCompatActivity {

    @BindView(R.id.place_image)         ImageView placeImageView;
    @BindView(R.id.place_name)          TextView placeNameTextView;
    @BindView(R.id.place_category)      TextView placeCategoryTextView;
    @BindView(R.id.place_description)   TextView placeDescriptionTextView;
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

        long placeId = getIntent().getLongExtra("id", 0);
        place = realm.where(Place.class)
                     .equalTo("id", placeId)
                     .findFirst();

        placeImageView.setImageBitmap(ImageEncoder.decodeImage(place.getEncodedImage()));
        placeNameTextView.setText(place.getName());
        placeCategoryTextView.setText(place.getCategory().getName());
        placeDescriptionTextView.setText(place.getDescription());
        ButterKnife.apply(textViews, INITIALIZE_COUNTERS, place);

        final Rating rating = place.getRating();

        if(rating != null) {
            switch (rating.getRating()) {
                case "like":
                    buttons.get(0).getDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                    break;
                case "dislike":
                    buttons.get(1).getDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                    break;
            }

            ButterKnife.apply(buttons, DISABLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.revision:
                Intent revisionDetailIntent = new Intent(this, RevisionDetailActivity.class);
                revisionDetailIntent.putExtra(RESOURCE_ID, getIntent().getLongExtra(RESOURCE_ID, 0));
                revisionDetailIntent.putExtra(REVISION_TYPE, "place");
                startActivity(revisionDetailIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.revision_menu, menu);
        return true;
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

    @OnClick({ R.id.fab })
    public void provideRevision() {
        Intent revisionIntent = new Intent(this, RevisionActivity.class);
        revisionIntent.putExtra("revisionType", "place");
        revisionIntent.putExtra("id", place.getId());
        startActivity(revisionIntent);
    }

    @OnClick({ R.id.like_button, R.id.dislike_button })
    public void rateSubmit(ImageButton button) {
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
        RatingApi.RatingService.startService(this, place, rating);
    }

    private void updateCounter(int counterId, int count) {
        TextView countTextView = ButterKnife.findById(this, counterId);
        countTextView.setText(String.valueOf(count));
    }
}
