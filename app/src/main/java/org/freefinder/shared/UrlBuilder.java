package org.freefinder.shared;

import android.text.TextUtils;

/**
 * Created by rade on 13.8.17..
 */

public class UrlBuilder {

    private String url;
    private String query;

    public UrlBuilder hostname(String hostname) {
        if(TextUtils.isEmpty(url)) {
            url = hostname;
        } else {
            throw new RuntimeException("Already has a hostname!");
        }

        return this;
    }

    public UrlBuilder resource(String resource) {
        if(TextUtils.isEmpty(url)) {
            throw new RuntimeException("No URL hostname provided!");
        }

        url = TextUtils.join("/", new String[] { url, resource });

        return this;
    }

    public UrlBuilder parameter(String key, String value) {
        final String p = TextUtils.join("=", new String[] { key, value });
        query = TextUtils.isEmpty(query) ? p : TextUtils.join("&", new String[] { query, p });

        return this;
    }

    public String getUrl() {
        if(TextUtils.isEmpty(url)) {
            throw new RuntimeException("No URL provided!");
        }

        return TextUtils.isEmpty(query) ? url : TextUtils.join("?", new String[] { url, query });
    }

}
