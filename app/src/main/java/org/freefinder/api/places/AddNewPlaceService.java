package org.freefinder.api.places;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;

import org.freefinder.BuildConfig;
import org.freefinder.api.Constants;
import org.freefinder.api.Status;
import org.freefinder.dependencies.GsonComponent;
import org.freefinder.dependencies.GsonModule;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Place;
import org.freefinder.receivers.HttpServiceReceiver;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by rade on 19.11.17..
 */

public class AddNewPlaceService extends IntentService {

    private Gson gson;

    private enum Actions {
        ADD_NEW
    }

    private enum Parameters {
        PLACE,
        RESULT_RECEIVER
    }

    public AddNewPlaceService() {
        super(AddNewPlaceService.class.getName());
    }

    public static void startService(Context context, HttpServiceReceiver httpServiceReceiver) {
        Intent addNewPlaceIntent = new Intent(context, AddNewPlaceService.class);
        addNewPlaceIntent.setAction(Actions.ADD_NEW.name());
        addNewPlaceIntent.putExtra(Parameters.RESULT_RECEIVER.name(), httpServiceReceiver);
        context.startService(addNewPlaceIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null) {
            HttpServiceReceiver resultReceiver = intent.getParcelableExtra(Parameters.RESULT_RECEIVER.name());
            final String action = intent.getAction();

            if(Actions.ADD_NEW.name().equals(action)) {
                final Place place = intent.getParcelableExtra(Parameters.PLACE.name());
                handleAddNewPlace(place, resultReceiver);
            }

        }
    }

    private void handleAddNewPlace(Place place, HttpServiceReceiver httpServiceReceiver) {
        final Context context = getApplicationContext();
        RequestFuture<JSONObject> addPlaceRequestFuture = RequestFuture.newFuture();
//        GsonComponent gsonComponent = DaggerGsonComponent.builder().gsonModule(new GsonModule()).build();

//        Gson gson = gsonComponent.provideGson();

        Gson gson = new Gson();

        JSONObject newPlaceJson = null;
        try {
            newPlaceJson = new JSONObject(gson.toJson(place));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        } catch (InterruptedException
                 | ExecutionException
                 | TimeoutException e) {
            e.printStackTrace();
        }

        if(addPlaceRequest.getStatusCode() == HttpURLConnection.HTTP_CREATED) {
            httpServiceReceiver.send(Status.SUCCESS, Bundle.EMPTY);
        } else {
            httpServiceReceiver.send(Status.FAILURE, Bundle.EMPTY);
        }
    }
}
