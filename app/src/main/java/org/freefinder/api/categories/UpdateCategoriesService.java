package org.freefinder.api.categories;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.activities.CategoriesActivity;
import org.freefinder.api.Constants;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 * Created by rade on 19.10.17..
 */

public class UpdateCategoriesService extends IntentService {

    public UpdateCategoriesService() {
        super("UpdateCategoriesService");
    }

    public static void startService(Context context) {
        Intent updateCategoriesIntent = new Intent(context,
                                                   UpdateCategoriesService.class);

        context.startService(updateCategoriesIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            updateCategories();
        }
    }

    private void updateCategories() {
        RequestFuture<JSONObject> updateCategoriesFuture = RequestFuture.newFuture();
        final String parameterCategoryUpdateTimestamp = "update_timestamp";
        final String categoryUpdateTimestamp = SharedPreferencesHelper.getCategoryUpdateTimestamp(
                                                                          getApplicationContext()
                                                                       );

        JsonObjectRequestWithToken updateCategoriesRequest = new JsonObjectRequestWithToken(
                Request.Method.GET,
                new UrlBuilder().hostname(BuildConfig.API_URL)
                                .resource(BuildConfig.CATEGORIES)
                                .parameter(parameterCategoryUpdateTimestamp, categoryUpdateTimestamp)
                                .getUrl(),
                SharedPreferencesHelper.getAuthorizationToken(getApplicationContext()),
                null,
                updateCategoriesFuture,
                updateCategoriesFuture
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).enqueueRequest(updateCategoriesRequest);

        final Realm realm = Realm.getDefaultInstance();

        try {
            JSONObject response = updateCategoriesFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                             Constants.TIME_UNIT);

            final JSONArray categoriesJson = response.getJSONArray("categories");
            final String updateTimestamp = response.getString("updateTimestamp");

            realm.createOrUpdateAllFromJson(Category.class, categoriesJson);

            SharedPreferencesHelper.setCategoryUpdateTimestamp(getApplicationContext(), updateTimestamp);
        } catch (JSONException
                | InterruptedException
                | ExecutionException
                | TimeoutException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }
}
