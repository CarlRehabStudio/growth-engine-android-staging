package com.google.android.apps.miyagi.development.data.models.profile;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

@Parcel
public class Settings {

    @SerializedName("header")
    protected String mHeader;

    @SerializedName("push_notifications")
    protected String mPushNotification;

    @SerializedName("email_notifications")
    protected String mEmailNotification;

    public String getHeader() {
        return mHeader;
    }

    public String getPushNotification() {
        return mPushNotification;
    }

    public String getEmailNotification() {
        return mEmailNotification;
    }
}
