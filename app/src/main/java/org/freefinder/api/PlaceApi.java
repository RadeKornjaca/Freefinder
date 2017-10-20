package org.freefinder.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.activities.AddPlaceActivity;
import org.freefinder.activities.MainActivity;
import org.freefinder.http.JsonArrayRequestWithToken;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 * Created by rade on 9.8.17..
 */

public class PlaceApi {
    private static final String TAG = PlaceApi.class.getSimpleName();

    public static class AddNewPlaceTask extends AsyncTask<JSONObject, Void, Boolean> {
        private Context context;

        public AddNewPlaceTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((AddPlaceActivity) context).showProgress(true);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            boolean isSuccessful = false;

            final JSONObject newPlaceJson = params[0];

            RequestFuture<JSONObject> addPlaceRequestFuture = RequestFuture.newFuture();

            final JsonObjectRequestWithToken addPlaceRequest = new JsonObjectRequestWithToken(
                    Request.Method.POST,
                    new UrlBuilder().hostname(BuildConfig.API_URL).resource(BuildConfig.PLACES).getUrl(),
                    SharedPreferencesHelper.getAuthorizationToken(context),
                    newPlaceJson,
                    addPlaceRequestFuture,
                    addPlaceRequestFuture
            );

            RequestQueueSingleton.getInstance(context).enqueueRequest(addPlaceRequest);

            try {
                JSONObject response = addPlaceRequestFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                                Constants.TIME_UNIT);
                if(response.get("id") != null) {
                    isSuccessful = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            super.onPostExecute(isSuccessful);

            if(isSuccessful) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            } else {
                Snackbar.make( ((AddPlaceActivity) context).findViewById(android.R.id.content),
                        "Couldn't add new place.",
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            ((AddPlaceActivity) context).showProgress(false);
        }
    }

    public static class SearchAreaByCategoryService extends IntentService {

        public static final String CATEGORY_ID = "org.freefinder.services.extra.QUERY";
        public static final String UPPER_LEFT_COORDINATE = "org.freefinder.services.extra.UPPER_LEFT_COORDINATE";
        public static final String DOWN_RIGHT_COORDINATE = "org.freefinder.services.extra.DOWN_RIGHT_COORDINATE";

        public SearchAreaByCategoryService() {
            super("SearchAreaByCategoryService");
        }

        public static void startService(Context context,
                                        Category category,
                                        GeoPoint upperLeft,
                                        GeoPoint downRight) {

            Intent searchIntent = new Intent(context, SearchAreaByCategoryService.class);
            searchIntent.putExtra(CATEGORY_ID, category.getId());
            searchIntent.putExtra(UPPER_LEFT_COORDINATE, (Parcelable) upperLeft);
            searchIntent.putExtra(DOWN_RIGHT_COORDINATE, (Parcelable) downRight);

            context.startService(searchIntent);
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            if(intent != null) {
                final long categoryId = intent.getLongExtra(CATEGORY_ID, 0);
                final GeoPoint upperLeftCoordinates = intent.getParcelableExtra(UPPER_LEFT_COORDINATE);
                final GeoPoint downRightCoordinates = intent.getParcelableExtra(DOWN_RIGHT_COORDINATE);
                fetchPlaces(categoryId, upperLeftCoordinates, downRightCoordinates);
            }
        }

        private void fetchPlaces(long categoryId, GeoPoint upperLeftCoordinates, GeoPoint downRightCoordinates) {
            final Realm realm = Realm.getDefaultInstance();
            final Context context = getApplicationContext();

            String minLatString = String.valueOf(downRightCoordinates.getLatitude());
            String maxLatString = String.valueOf(upperLeftCoordinates.getLatitude());
            String minLngString = String.valueOf(upperLeftCoordinates.getLongitude());
            String maxLngString = String.valueOf(downRightCoordinates.getLongitude());
            String categoryIdString = String.valueOf(categoryId);

            RequestFuture<JSONArray> placesRequestFuture = RequestFuture.newFuture();

            JsonArrayRequestWithToken placesRequest = new JsonArrayRequestWithToken(
                    Request.Method.GET,
                    new UrlBuilder().hostname(BuildConfig.API_URL)
                                    .resource(BuildConfig.PLACES)
                                    .parameter("min_lat", minLatString)
                                    .parameter("max_lat", maxLatString)
                                    .parameter("min_lng", minLngString)
                                    .parameter("max_lng", maxLngString)
                                    .parameter("category_id", categoryIdString)
                                    .getUrl(),
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
}
