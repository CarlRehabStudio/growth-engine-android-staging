package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 23.03.2017.
 */

public class ResetPasswordLabels extends RealmObject {

    @SerializedName("cta_text")
    private String mCtaText;

    @SerializedName("validation_empty_mail")
    private String mValidationEmptyMail;

    @SerializedName("reset_header")
    private String mResetHeader;

    @SerializedName("feedback_header")
    private String mFeedbackHeader;

    @SerializedName("enter_email")
    private String mEnterEmail;

    @SerializedName("feedback_text")
    private String mFeedbackText;

    @SerializedName("email")
    private String mEmail;

    public String getCtaText() {
        return mCtaText;
    }

    public String getValidationEmptyMail() {
        return mValidationEmptyMail;
    }

    public String getResetHeader() {
        return mResetHeader;
    }

    public String getFeedbackHeader() {
        return mFeedbackHeader;
    }

    public String getEnterEmail() {
        return mEnterEmail;
    }

    public String getFeedbackText() {
        return mFeedbackText;
    }

    public String getEmail() {
        return mEmail;
    }

}
