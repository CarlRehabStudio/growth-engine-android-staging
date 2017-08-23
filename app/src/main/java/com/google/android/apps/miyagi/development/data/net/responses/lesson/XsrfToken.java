package com.google.android.apps.miyagi.development.data.net.responses.lesson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcinarciszew on 05.01.2017.
 */

public class XsrfToken {
    @SerializedName("xsrf_token")
    final String mXsrfToken;

    public XsrfToken(String token) {
        this.mXsrfToken = token;
    }
}
