package com.google.android.apps.miyagi.development.data.net.responses.profile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukaszweglinski on 11.01.2017.
 */

public class ProfileUpdateRequestData {

    @SerializedName("first_name")
    protected String mFirstName;

    @SerializedName("last_name")
    protected String mLastName;

    @SerializedName("email_notifications")
    protected boolean mEmailNotifications;

    @SerializedName("email_notifications_changed")
    protected boolean mEmailNotificationsChanged;

    @SerializedName("xsrf_token")
    protected String mXsrfToken;

    /**
     * Instantiates a new Profile update request data.
     *
     * @param firstName                 first name.
     * @param lastName                  last name.
     * @param emailNotifications        is email notification enable.
     * @param emailNotificationsChanged is email notification setting changed.
     * @param xsrfToken                 xsrf token.
     */
    public ProfileUpdateRequestData(String firstName, String lastName, boolean emailNotifications, boolean emailNotificationsChanged, String xsrfToken) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailNotifications = emailNotifications;
        mEmailNotificationsChanged = emailNotificationsChanged;
        mXsrfToken = xsrfToken;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public boolean isEmailNotifications() {
        return mEmailNotifications;
    }
}
