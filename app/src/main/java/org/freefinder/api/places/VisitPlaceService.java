package org.freefinder.api.places;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.api.Constants;
import org.freefinder.api.categories.VisitCategoryService;
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

public class VisitPlaceService extends IntentService {

    private static final String PLACE_ID = "org.freefinder.services.extra.PLACE_ID";

    public VisitPlaceService() {
        super("VisitPlaceService");
    }

    public static void startService(Context context, long placeId) {
        Intent visitPlaceServiceIntent = new Intent(context, VisitPlaceService.class);

        visitPlaceServiceIntent.putExtra(PLACE_ID, placeId);

        context.startService(visitPlaceServiceIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            final long categoryId = intent.getLongExtra(PLACE_ID, 0);
            visitPlace(categoryId);
        }
    }

    private void visitPlace(long placeId) {
        RequestFuture<JSONObject> visitPlaceRequestFuture = RequestFuture.newFuture();

        JsonObjectRequestWithToken visitPlaceReqest = new JsonObjectRequestWithToken(
                Request.Method.POST,
                new UrlBuilder().hostname(BuildConfig.API_URL)
                                .resource(BuildConfig.PLACES)
                                .resource(String.valueOf(placeId))
                                .resource(BuildConfig.VISIT)
                                .getUrl(),
                SharedPreferencesHelper.getAuthorizationToken(getApplicationContext()),
                null,
                visitPlaceRequestFuture,
                visitPlaceRequestFuture
        );

        RequestQueueSingleton.getInstance(getApplicationContext()).enqueueRequest(visitPlaceReqest);

        try {
            JSONObject response = visitPlaceRequestFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                              Constants.TIME_UNIT);
        } catch (InterruptedException
                | ExecutionException
                | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
