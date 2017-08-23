package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 21.12.2016.
 */

public class CreateAccountMissingInfoLabels extends RealmObject {

    @SerializedName("sign_up_header")
    private String mSignUpHeader;

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("email_notifications")
    private String mEmailNotifications;

    @SerializedName("sign_up")
    private String mSignUp;

    @SerializedName("validation_empty_first_name")
    private String mValidationEmptyFirstName;

    @SerializedName("validation_empty_last_name")
    private String mValidationEmptyLastName;

    @SerializedName("push_notifications")
    private String mPushNotifications;

    public String getPushNotifications() {
        return mPushNotifications;
    }

    public String getEmailNotifications() {
        return mEmailNotifications;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getButtonNext() {
        return mSignUp;
    }

    public String getToolbarTitle() {
        return mSignUpHeader;
    }

    public String getValidationEmptyFirstName() {
        return mValidationEmptyFirstName;
    }

    public String getValidationEmptyLastName() {
        return mValidationEmptyLastName;
    }

}
