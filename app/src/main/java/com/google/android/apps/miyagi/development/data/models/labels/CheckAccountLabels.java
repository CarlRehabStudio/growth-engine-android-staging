package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 21.12.2016.
 */

public class CheckAccountLabels extends RealmObject {

    @SerializedName("sign_in_trouble_url")
    private String mSignInTroubleUrl;

    @SerializedName("sign_in_header")
    private String mSignInHeader;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("enter_email")
    private String mEnterEmail;

    @SerializedName("sign_in_problem")
    private String mSignInProblem;

    @SerializedName("next")
    private String mNext;

    @SerializedName("validation_empty_mail")
    private String mValidationEmptyMail;

    public String getEmail() {
        return mEmail;
    }

    public String getEmailInputHint() {
        return mEnterEmail;
    }

    public String getButtonNext() {
        return mNext;
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

    public String getEmailRequiredError() {
        return mValidationEmptyMail;
    }
}
