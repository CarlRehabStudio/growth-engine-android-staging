package com.google.android.apps.miyagi.development.data.models.labels;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by marcin on 22.12.2016.
 */
public class FirebaseMessagesLabels extends RealmObject {

    @SerializedName("internal_error")
    private String mInternalError;

    @SerializedName("email_exists")
    private String mEmailExists;

    @SerializedName("invalid_auth")
    private String mInvalidAuth;

    @SerializedName("invalid_email")
    private String mInvalidEmail;

    @SerializedName("invalid_password")
    private String mInvalidPassword;

    @SerializedName("network_request_failed")
    private String mNetworkRequestFailed;

    @SerializedName("token_expired")
    private String mTokenExpired;

    @SerializedName("too_many_attempts")
    private String mTooManyAttempts;

    @SerializedName("weak_password")
    private String mWeakPassword;

    public String getInternalError() {
        return mInternalError;
    }

    public String getEmailExists() {
        return mEmailExists;
    }

    public String getInvalidAuth() {
        return mInvalidAuth;
    }

    public String getInvalidEmail() {
        return mInvalidEmail;
    }

    public String getInvalidPassword() {
        return mInvalidPassword;
    }

    public String getNetworkRequestFailed() {
        return mNetworkRequestFailed;
    }

    public String getTokenExpired() {
        return mTokenExpired;
    }

    public String getTooManyAttempts() {
        return mTooManyAttempts;
    }

    public String getWeakPassword() {
        return mWeakPassword;
    }
}
