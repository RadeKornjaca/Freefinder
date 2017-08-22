package org.freefinder.api;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.freefinder.BuildConfig;
import org.freefinder.activities.LoginActivity;
import org.freefinder.activities.MainActivity;
import org.freefinder.http.RequestQueueSingleton;
import org.freefinder.shared.SharedPreferencesHelper;
import org.freefinder.shared.UrlBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by rade on 16.8.17..
 */

public class LoginApi {
    public static class UserLoginTask extends AsyncTask<JSONObject, Integer, Boolean> {

        private final Context context;

        public UserLoginTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ((LoginActivity) context).showProgress(true);
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {
            boolean isSuccessful = false;

            final JSONObject loginCredentialsJson = params[0];

            RequestFuture<JSONObject> authFuture = RequestFuture.newFuture();

            JsonObjectRequest authRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    new UrlBuilder().hostname(BuildConfig.API_URL)
                                    .resource(BuildConfig.DEVICE_SIGN_IN)
                                    .getUrl(),
                    loginCredentialsJson,
                    authFuture,
                    authFuture
            );

            RequestQueueSingleton.getInstance(context).enqueueRequest(authRequest);

            try {
                JSONObject response = authFuture.get(60, TimeUnit.SECONDS);
                SharedPreferencesHelper.setAuthorizationToken(context, response.getString("access_token"));
                isSuccessful = true;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
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
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            } else {
                ((LoginActivity) context).formError();
            }

            ((LoginActivity) context).showProgress(false);
        }
    }
}
