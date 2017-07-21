package org.freefinder.http;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rade on 14.7.17..
 */

public class JsonArrayRequestWithToken extends JsonArrayRequest {
    private String authorizationToken;

    public JsonArrayRequestWithToken(int method,
                                     String url,
                                     String authorizationToken,
                                     JSONArray jsonRequest,
                                     Response.Listener<JSONArray> listener,
                                     Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.authorizationToken = authorizationToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();

        headers.put("Content-Type", "application/json");
        headers.put("Authorization", TextUtils.join(" ", new String [] { "Token",
                authorizationToken})
        );

        return headers;
    }
}
