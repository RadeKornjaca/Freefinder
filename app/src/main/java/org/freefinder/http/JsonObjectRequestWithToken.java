package org.freefinder.http;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rade on 24.6.17..
 */

public class JsonObjectRequestWithToken extends JsonObjectRequest {
    private String authorizationToken;
    private int statusCode;

    public JsonObjectRequestWithToken(int method,
                                      String url,
                                      String authorizationToken,
                                      JSONObject jsonRequest,
                                      Response.Listener<JSONObject> listener,
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

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        this.statusCode = response.statusCode;

        return super.parseNetworkResponse(response);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
