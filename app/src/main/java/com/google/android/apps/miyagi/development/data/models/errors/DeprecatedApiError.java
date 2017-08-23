package com.google.android.apps.miyagi.development.data.models.errors;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emilzawadzki on 17.02.2017.
 */

public class DeprecatedApiError {

    @SerializedName("message")
    protected String mMessage;

    @SerializedName("cta_text")
    protected String mCtaText;

    @SerializedName("android_url")
    protected String mAndroidUrl;

    public String getMessage() {
        return mMessage;
    }

    public String getCtaText() {
        return mCtaText;
    }

    public String getAndroidUrl() {
        return mAndroidUrl;
    }
}
