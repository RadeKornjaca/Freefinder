package org.freefinder.http;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by rade on 24.6.17..
 */

public class JsonObjectRequestWithToken extends JsonObjectRequest {
    private String authorizationToken;

    public JsonObjectRequestWithToken(String url,
                                      JSONObject jsonRequest,
                                      String authorizationToken,
                                      Response.Listener<JSONObject> listener,
                                      Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        this.authorizationToken = authorizationToken;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        headers.put("Authorization", TextUtils.join(" ", new String[] { "Token",
                                                                        authorizationToken })
        );

        return headers;
    }
}