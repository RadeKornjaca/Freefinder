package org.freefinder.api;

import android.os.ResultReceiver;
import android.support.v4.app.FragmentManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.freefinder.BuildConfig;
import org.freefinder.activities.CategoryDetailActivity;
import org.freefinder.activities.PlaceDetailActivity;
import org.freefinder.activities.RevisionDetailActivity;
import org.freefinder.adapters.ScreenSlidePagerAdapter;
import org.freefinder.fragments.RevisionFragment;
import org.freefinder.http.JsonArrayRequestWithToken;
import org.freefinder.http.JsonObjectRequestWithToken;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.model.Category;
import org.freefinder.model.deserializers.ParcelableDeserializer;
import org.freefinder.model.Place;
import org.freefinder.model.Revision;
import org.freefinder.receivers.HttpServiceReceiver;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;

/**
 * Created by rade on 23.9.17..
 */

public class RevisionApi {
    public static class AddRevisionTask extends AsyncTask<JSONObject, Void, Boolean> {
        private Context context;
        private long id;
        private String revisionType;

        public AddRevisionTask(Context context, long id, String revisionType) {
            this.context = context;
            this.id = id;
            this.revisionType = revisionType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            boolean isSuccessful = false;

            final JSONObject revisionJson = params[0];

            RequestFuture<JSONObject> revisionRequestFuture = RequestFuture.newFuture();

            JsonObjectRequestWithToken revisionRequest = new JsonObjectRequestWithToken(
                    Request.Method.POST,
                    new UrlBuilder().hostname(BuildConfig.API_URL)
                                    .resource(revisionType.equals("category") ? BuildConfig.CATEGORIES
                                                                              : BuildConfig.PLACES)
                                    .resource(String.valueOf(id))
                                    .resource(BuildConfig.REVISIONS)
                                    .getUrl(),
                    SharedPreferencesHelper.getAuthorizationToken(context),
                    revisionJson,
                    revisionRequestFuture,
                    revisionRequestFuture
            );

            RequestQueueSingleton.getInstance(context).enqueueRequest(revisionRequest);

            try {
                JSONObject response = revisionRequestFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                                Constants.TIME_UNIT);

                isSuccessful = true;
            } catch (InterruptedException
                    | ExecutionException
                    | TimeoutException e) {
                e.printStackTrace();
            }

            return isSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {

            if(isSuccessful) {
                Intent detailsIntent = new Intent();
                switch(revisionType) {
                    case "category":
                        detailsIntent.setClass(context, CategoryDetailActivity.class);
                        break;
                    case "place":
                        detailsIntent.setClass(context, PlaceDetailActivity.class);
                        break;
                }

                detailsIntent.putExtra("id", id);
                detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(detailsIntent);
            } else {
                // TODO: Fill with some error messages
            }

            super.onPostExecute(isSuccessful);
        }
    }

    public static class RevisionFetchTask extends AsyncTask<Bundle, Void, Revision[]> {
        private final static String RESOURCE_ID = "id";
        private final static String REVISION_TYPE = "revisionType";

        private Context context;

        public RevisionFetchTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((RevisionDetailActivity) context).showProgress(true);
        }

        @Override
        protected Revision[] doInBackground(Bundle... params) {
            final Bundle bundle = params[0];
            RequestFuture<JSONArray> revisionRequestFuture = RequestFuture.newFuture();

            final long revisionableId = bundle.getLong(RESOURCE_ID);
            final String revisionableType = bundle.getString(REVISION_TYPE);

            JsonArrayRequestWithToken revisionRequest = new JsonArrayRequestWithToken(
                    Request.Method.GET,
                    new UrlBuilder().hostname(BuildConfig.API_URL)
                            .resource(revisionableType.equals("category") ? BuildConfig.CATEGORIES
                                    : BuildConfig.PLACES)
                            .resource(String.valueOf(revisionableId))
                            .resource(BuildConfig.REVISIONS)
                            .getUrl(),
                    SharedPreferencesHelper.getAuthorizationToken(context),
                    null,
                    revisionRequestFuture,
                    revisionRequestFuture
            );

            RequestQueueSingleton.getInstance(context).enqueueRequest(revisionRequest);

            try {
                JSONArray revisionsJson = revisionRequestFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                                    Constants.TIME_UNIT);

//                Class<? extends Parcelable[]> persistenceClass = revisionableType.equals("category")
//                                                                 ? Category[].class : Place[].class;
//                Parcelable[] revisionables = new Parcelable[revisionsJson.length()];


                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Parcelable.class, new ParcelableDeserializer());

                Gson gson = gsonBuilder.create();
//                Log.d("JSON", revisionsJson.toString());
                Revision[] revisionables = gson.fromJson(revisionsJson.toString(), Revision[].class);
//                Log.d("DESERIALIZED", revisionables.toString());

                return revisionables;

            } catch (InterruptedException
                    | ExecutionException
                    | TimeoutException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Revision[] revisionables) {
            FragmentManager sfm = ((RevisionDetailActivity) context).getSupportFragmentManager();
            Parcelable resource = ((RevisionDetailActivity) context).getResource();

            ((RevisionDetailActivity) context).setPagerAdapter(new ScreenSlidePagerAdapter(sfm, resource, revisionables));
            ((RevisionDetailActivity) context).getPager().setAdapter(((RevisionDetailActivity) context).getPagerAdapter());
            ((RevisionDetailActivity) context).showProgress(false);
        }

    }

    public static class RevisionApprovalService extends IntentService {
        private static final String REVISIONABLE_ID   = "org.freefinder.services.extra.REVISIONABLE_ID";
        private static final String REVISIONABLE_TYPE = "org.freefinder.services.extra.REVISIONABLE_TYPE";
        private static final String REVISION_ID       = "org.freefinder.services.extra.REVISION_ID";
        private static final String VOTE              = "org.freefinder.services.extra.VOTE";

        public RevisionApprovalService() {
            super("RevisionApprovalService");
        }

        public static void startService(Context context,
                                        long resourceId,
                                        String revisionableType,
                                        long revisionId,
                                        boolean vote) {

            Intent ratingIntent = new Intent(context, RevisionApprovalService.class);
            ratingIntent.putExtra("receiver", ((RevisionDetailActivity) context).getHttpServiceReceiver());

            ratingIntent.putExtra(REVISIONABLE_ID, resourceId);
            ratingIntent.putExtra(REVISIONABLE_TYPE, revisionableType);
            ratingIntent.putExtra(REVISION_ID, revisionId);
            ratingIntent.putExtra(VOTE, vote);

            context.startService(ratingIntent);
        }


        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            if(intent != null) {
                final long resourceId = intent.getLongExtra(REVISIONABLE_ID, 0);
                final String revisionableType = intent.getStringExtra(REVISIONABLE_TYPE);
                final long revisionId = intent.getLongExtra(REVISION_ID, 0);
                final boolean vote = intent.getBooleanExtra(VOTE, false);

                final ResultReceiver httpServiceReceiver = intent.getParcelableExtra("receiver");

                revisionApproval(resourceId, revisionableType, revisionId, vote, httpServiceReceiver);
            }
        }

        private void revisionApproval(long resourceId,
                                      String revisionableType,
                                      long revisionId,
                                      boolean vote,
                                      ResultReceiver httpServiceReceiver) {
            RequestFuture<JSONObject> revisionApprovalFuture = RequestFuture.newFuture();

            String resourceType = revisionableType.equals("Category") ? BuildConfig.CATEGORIES : BuildConfig.PLACES;
            String voteType = vote ? "approve" : "disprove";

            final JsonObjectRequestWithToken ratingRequest = new JsonObjectRequestWithToken(
                    Request.Method.PUT,
                    new UrlBuilder().hostname(BuildConfig.API_URL)
                            .resource(resourceType)
                            .resource(String.valueOf(resourceId))
                            .resource(BuildConfig.REVISIONS)
                            .resource(String.valueOf(revisionId))
                            .resource(voteType)
                            .getUrl(),
                    SharedPreferencesHelper.getAuthorizationToken(getApplicationContext()),
                    null,
                    revisionApprovalFuture,
                    revisionApprovalFuture
            );

            RequestQueueSingleton.getInstance(getApplicationContext()).enqueueRequest(ratingRequest);

            try {
                final JSONObject response = revisionApprovalFuture.get(Constants.STANDARD_REQUEST_TIMEOUT,
                                                                       Constants.TIME_UNIT);
            } catch (InterruptedException
                    | ExecutionException
                    | TimeoutException e) {
                e.printStackTrace();
            }

            if(ratingRequest.getStatusCode() == HttpURLConnection.HTTP_OK) {
                httpServiceReceiver.send(Status.SUCCESS, Bundle.EMPTY);
            } else {
                httpServiceReceiver.send(Status.FAILURE, Bundle.EMPTY);
            }
        }
    }
}
