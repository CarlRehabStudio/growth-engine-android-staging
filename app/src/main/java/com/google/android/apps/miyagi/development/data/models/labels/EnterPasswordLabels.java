package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 21.12.2016.
 */

public class EnterPasswordLabels extends RealmObject {

    @SerializedName("sign_in_trouble_url")
    private String mSignInTroubleUrl;

    @SerializedName("sign_in_header")
    private String mSignInHeader;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("sign_in_problem")
    private String mSignInProblem;

    @SerializedName("next")
    private String mNext;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("validation_empty_password")
    private String mValidationEmptyPassword;

    public String getEmailHint() {
        return mEmail;
    }

    public String getButtonNext() {
        return mNext;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getToolbarTitle() {
        return mSignInHeader;
    }

    public String getProblemLinkText() {
        return mSignInProblem;
    }

    public String getProblemLinkUrl() {
        return mSignInTroubleUrl;
    }

    public String getValidationEmptyPassword() {
        return mValidationEmptyPassword;
    }

}
