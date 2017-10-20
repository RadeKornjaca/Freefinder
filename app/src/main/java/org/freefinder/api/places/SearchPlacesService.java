package org.freefinder.api.places;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.api.Constants;
import org.freefinder.api.PlaceApi;
import org.freefinder.http.JsonArrayRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONArray;
import org.osmdroid.util.GeoPoint;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 * Created by rade on 20.10.17..
 */

public class SearchPlacesService extends IntentService {

    private static final String CATEGORY = "Category";
    private static final String PLACE    = "Place";

    public static final String QUERY                 = "org.freefinder.services.extra.QUERY";
    public static final String QUERY_TYPE            = "org.freefinder.services.extra.QUERY_TYPE";
    public static final String UPPER_LEFT_COORDINATE = "org.freefinder.services.extra.UPPER_LEFT_COORDINATE";
    public static final String DOWN_RIGHT_COORDINATE = "org.freefinder.services.extra.DOWN_RIGHT_COORDINATE";

    public SearchPlacesService() {
        super("SearchPlacesService");
    }

    public static void startService(Context context,
                                    String query,
                                    String queryType,
                                    GeoPoint upperLeft,
                                    GeoPoint downRight) {

        Intent searchIntent = new Intent(context, SearchPlacesService.class);
        searchIntent.putExtra(QUERY, query);
        searchIntent.putExtra(QUERY_TYPE, queryType);
        searchIntent.putExtra(UPPER_LEFT_COORDINATE, (Parcelable) upperLeft);
        searchIntent.putExtra(DOWN_RIGHT_COORDINATE, (Parcelable) downRight);

        context.startService(searchIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            final String query = intent.getStringExtra(QUERY);
            final String queryType = intent.getStringExtra(QUERY_TYPE);
            final GeoPoint upperLeftCoordinates = intent.getParcelableExtra(UPPER_LEFT_COORDINATE);
            final GeoPoint downRightCoordinates = intent.getParcelableExtra(DOWN_RIGHT_COORDINATE);
            fetchPlaces(query, queryType, upperLeftCoordinates, downRightCoordinates);
        }
    }

    private void fetchPlaces(String query,
                             String queryType,
                             GeoPoint upperLeftCoordinates,
                             GeoPoint downRightCoordinates) {
        final Realm realm = Realm.getDefaultInstance();
        final Context context = getApplicationContext();

        String minLatString = String.valueOf(downRightCoordinates.getLatitude());
        String maxLatString = String.valueOf(upperLeftCoordinates.getLatitude());
        String minLngString = String.valueOf(upperLeftCoordinates.getLongitude());
        String maxLngString = String.valueOf(downRightCoordinates.getLongitude());

        UrlBuilder urlBuilder = new UrlBuilder().hostname(BuildConfig.API_URL)
                                                .resource(BuildConfig.PLACES)
                                                .parameter("min_lat", minLatString)
                                                .parameter("max_lat", maxLatString)
                                                .parameter("min_lng", minLngString)
                                                .parameter("max_lng", maxLngString);


        switch(queryType) {
            case CATEGORY:
                Category category = realm.where(Category.class).equalTo("name", query).findFirst();
                if(category != null) {
                    String categoryIdString = String.valueOf(category.getId());
                    urlBuilder.parameter("category_id", categoryIdString);
                }
                break;
            case PLACE:
                urlBuilder.parameter("name", query);
                break;
            default:
                return;
        }

        RequestFuture<JSONArray> placesRequestFuture = RequestFuture.newFuture();

        JsonArrayRequestWithToken placesRequest = new JsonArrayRequestWithToken(
                Request.Method.GET,
                urlBuilder.getUrl(),
                SharedPreferencesHelper.getAuthorizationToken(context),
                null,
                placesRequestFuture,
                placesRequestFuture
        );

        RequestQueueSingleton.getInstance(context).enqueueRequest(placesRequest);

        try {
            JSONArray response = placesRequestFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                    Constants.TIME_UNIT);
            realm.beginTransaction();
            realm.createOrUpdateAllFromJson(Place.class, response);
            realm.commitTransaction();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }
}
