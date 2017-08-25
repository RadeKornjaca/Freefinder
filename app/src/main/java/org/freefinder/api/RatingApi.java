package org.freefinder.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Place;
import org.freefinder.model.Rating;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 * Created by rade on 24.8.17..
 */

public class RatingApi {
    public static class RatingService extends IntentService {

        public static final String PLACE_ID = "org.freefinder.services.extra.PLACE_ID";
        public static final String TOKEN    = "org.freefinder.services.extra.API_KEY";
        public static final String RATING   = "org.freefinder.services.extra.RATING";

        public RatingService() {
            super("RatingService");
        }

        public void startService(Context context, Place place, String rating) {
            Intent ratingIntent = new Intent(context, RatingService.class);
            ratingIntent.putExtra(PLACE_ID, place.getId());
            ratingIntent.putExtra(TOKEN, SharedPreferencesHelper.getAuthorizationToken(context));
            ratingIntent.putExtra(RATING, rating);

            context.startService(ratingIntent);
        }


        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            if(intent != null) {
                final int placeId = intent.getIntExtra(PLACE_ID, 0);
                final String token = intent.getStringExtra(TOKEN);
                final String rating = intent.getStringExtra(RATING);

                placeRating(placeId, token, rating);
            }
        }

        private void placeRating(int placeId, String token, String rating) {
            Realm realm = Realm.getDefaultInstance();
            final JSONObject requestJson = new JSONObject();

            try {
                requestJson.put("place_id", placeId);
                requestJson.put("api_key", token);
                requestJson.put("rating", rating);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestFuture<JSONObject> ratingFuture = RequestFuture.newFuture();

            final JsonObjectRequestWithToken ratingRequest = new JsonObjectRequestWithToken(
                    Request.Method.PUT,
                    new UrlBuilder().hostname(BuildConfig.API_URL)
                                    .resource(BuildConfig.RATINGS)
                                    .getUrl(),
                    token,
                    requestJson,
                    ratingFuture,
                    ratingFuture
            );

            RequestQueueSingleton.getInstance(getApplicationContext()).enqueueRequest(ratingRequest);

            try {
                final JSONObject response = ratingFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                             Constants.TIME_UNIT);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.createOrUpdateObjectFromJson(Rating.class, response);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            realm.close();
        }
    }
}
