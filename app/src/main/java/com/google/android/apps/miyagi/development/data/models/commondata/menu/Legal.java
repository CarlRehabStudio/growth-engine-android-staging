package com.google.android.apps.miyagi.development.data.models.commondata.menu;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 28.12.2016.
 */

public class Legal extends RealmObject {

    @SerializedName("page")
    private LegalPage mPage;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("google_text")
    private String mGoogleText;

    @SerializedName("google_image_url")
    private String mGoogleImageUrl;

    @SerializedName("google_image_url_alt_text")
    private String mGoogleImageAltText;

    public LegalPage getPage() {
        return mPage;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getGoogleText() {
        return mGoogleText;
    }

    public String getGoogleImageUrl() {
        return mGoogleImageUrl;
    }

    public String getGoogleImageAltText() {
        return mGoogleImageAltText;
    }
}
