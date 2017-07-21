package org.freefinder.http;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by rade on 2.7.17..
 */

public interface IResponse {
    public void notifySuccess(JSONObject responseObject);
    public void notifyFailure(VolleyError responseError);
}
