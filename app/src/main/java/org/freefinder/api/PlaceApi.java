package org.freefinder.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.freefinder.BuildConfig;
import org.freefinder.http.JsonArrayRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.model.Place;
import org.freefinder.shared.SharedPreferencesHelper;
import org.json.JSONArray;
import org.json.JSONException;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by rade on 9.8.17..
 */

public class PlaceApi {
    private static final String TAG = PlaceApi.class.getSimpleName();

    public static RealmList<Place> searchByCategory(Context context, Category category) {
        Realm realm = Realm.getDefaultInstance();

        JsonArrayRequestWithToken placesRequest = new JsonArrayRequestWithToken(
                Request.Method.GET,
                TextUtils.join("/", new String[]{BuildConfig.API_URL,
                                                 BuildConfig.PLACES }),
                SharedPreferencesHelper.getAuthorizationToken(context),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, response.toString(4));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }

        );

        RequestQueueSingleton.getInstance(context).enqueueRequest(placesRequest);

        return null;
    }
}
