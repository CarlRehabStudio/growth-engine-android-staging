package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 21.12.2016.
 */

public class SignInSelectionLabels extends RealmObject {

    @SerializedName("google_header_text")
    private String mGoogleHeaderText;

    @SerializedName("google_image_alt")
    private String mGoogleImageAlt;

    @SerializedName("background_image_alt")
    private String mBackgroundImageAlt;

    @SerializedName("signin_google")
    private String mSignInGoogle;

    @SerializedName("signin_email")
    private String mSignInEmail;

    @SerializedName("footer_legal")
    private String mFooterLegal;

    @SerializedName("footer_legal_url")
    private String mFooterLegalUrl;

    @SerializedName("signin_google_used")
    private String mSignInGoogleUsed;

    @SerializedName("signin_email_used")
    private String mSignInEmailUsed;

    public String getBackgroundImageAlt() {
        return mBackgroundImageAlt;
    }

    public String getFooterLegal() {
        return mFooterLegal;
    }

    public String getGoogleHeaderText() {
        return mGoogleHeaderText;
    }

    public String getGoogleImageAlt() {
        return mGoogleImageAlt;
    }

    public String getSignInEmail() {
        return mSignInEmail;
    }

    public String getSignInGoogle() {
        return mSignInGoogle;
    }

    public String getFooterLegalUrl() {
        return mFooterLegalUrl;
    }

    public String getSignInGoogleUsed() {
        return mSignInGoogleUsed;
    }

    public String getSignInEmailUsed() {
        return mSignInEmailUsed;
    }

}
