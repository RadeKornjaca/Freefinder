package org.freefinder.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.freefinder.R;

/**
 * Created by rade on 14.7.17..
 */

public class SharedPreferencesHelper {

    public static String getAuthorizationToken(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String accessToken = sharedPreferences.getString(context.getString(R.string.settings_access_token), null);

        return accessToken;
    }

    public static void setAuthorizationToken(Context context, String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(context.getString(R.string.settings_access_token), token);
        editor.commit();
    }

    public static String getCategoryUpdateTimestamp(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String categoryUpdateTimestamp = sharedPreferences.getString(context.getString(R.string.category_update_timestamp), null);

        return categoryUpdateTimestamp;
    }

    public static void setCategoryUpdateTimestamp(Context context, String categoryUpdateTimestamp) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putString(context.getString(R.string.category_update_timestamp), categoryUpdateTimestamp);
        editor.commit();
    }
}
