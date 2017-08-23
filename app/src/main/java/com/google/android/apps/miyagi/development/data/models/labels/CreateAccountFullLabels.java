package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcinarciszew on 21.12.2016.
 */

public class CreateAccountFullLabels extends RealmObject {

    @SerializedName("sign_up_header")
    private String mSignUpHeader;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("enter_email")
    private String mEnterEmail;

    @SerializedName("first_name")
    private String mFirstName;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("choose_password")
    private String mChoosePassword;

    @SerializedName("confirm_password")
    private String mConfirmPassword;

    @SerializedName("email_notifications")
    private String mEmailNotifications;

    @SerializedName("push_notifications")
    private String mPushNotifications;

    @SerializedName("sign_up")
    private String mSignUp;

    @SerializedName("validation_empty_first_name")
    private String mValidationEmptyFirstName;

    @SerializedName("validation_empty_last_name")
    private String mValidationEmptyLastName;

    @SerializedName("validation_empty_password")
    private String mValidationEmptyPassword;

    @SerializedName("validation_password_mismatch")
    private String mValidationPasswordMismatch;

    public String getChoosePassword() {
        return mChoosePassword;
    }

    public String getConfirmPassword() {
        return mConfirmPassword;
    }

    public String getEmailHint() {
        return mEmail;
    }

    public String getEmailNotifications() {
        return mEmailNotifications;
    }

    public String getPushNotifications() {
        return mPushNotifications;
    }

    public String getEnterEmail() {
        return mEnterEmail;
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

    public String getValidationEmptyPassword() {
        return mValidationEmptyPassword;
    }

    public String getValidationPasswordMismatch() {
        return mValidationPasswordMismatch;
    }
}
