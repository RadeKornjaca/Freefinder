package org.freefinder.api.categories;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.api.CategoriesApi;
import org.freefinder.api.Constants;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by rade on 17.10.17..
 */

public class VisitCategoryService extends IntentService {

    private static final String CATEGORY_ID = "org.freefinder.services.extra.CATEGORY_ID";

    public VisitCategoryService() {
        super("VisitCategoryService");
    }

    public static void startService(Context context, long categoryId) {
        Intent visitCategoryServiceIntent = new Intent(context, VisitCategoryService.class);

        visitCategoryServiceIntent.putExtra(CATEGORY_ID, categoryId);

        context.startService(visitCategoryServiceIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            final long categoryId = intent.getLongExtra(CATEGORY_ID, 0);
            visitCategory(categoryId);
        }
    }

    private void visitCategory(long categoryId) {
        RequestFuture<JSONObject> visitCategoryRequestFuture = RequestFuture.newFuture();

        JsonObjectRequestWithToken visitCategoryReqest = new JsonObjectRequestWithToken(
                Request.Method.POST,
                new UrlBuilder().hostname(BuildConfig.API_URL)
                                .resource(BuildConfig.CATEGORIES)
                                .resource(String.valueOf(categoryId))
                                .resource(BuildConfig.VISIT)
                                .getUrl(),
                SharedPreferencesHelper.getAuthorizationToken(getApplicationContext()),
                null,
                visitCategoryRequestFuture,
                visitCategoryRequestFuture
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).enqueueRequest(visitCategoryReqest);

        try {
            JSONObject response = visitCategoryRequestFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                                 Constants.TIME_UNIT);
        } catch (InterruptedException
                 | ExecutionException
                 | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
