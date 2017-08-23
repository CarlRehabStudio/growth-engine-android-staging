package com.google.android.apps.miyagi.development.data.models.profile;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 11.01.2017.
 */

@Parcel
public class Student {

    @SerializedName("name")
    protected String mName;

    @SerializedName("last_name")
    protected String mLastName;

    @SerializedName("email")
    protected String mEmail;

    @SerializedName("notifications")
    protected boolean mNotifications;

    @SerializedName("last_login_time")
    protected String mLastLoginTime;

    public String getName() {
        return mName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public boolean isNotifications() {
        return mNotifications;
    }

    public String getLastLoginTime() {
        return mLastLoginTime;
    }
}

