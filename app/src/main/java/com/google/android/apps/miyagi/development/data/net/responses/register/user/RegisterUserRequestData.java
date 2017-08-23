package com.google.android.apps.miyagi.development.data.net.responses.register.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukaszweglinski on 09.01.2017.
 */

public class RegisterUserRequestData {

    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("email_notifications")
    private boolean mEmailNotifications;
    @SerializedName("xsrf_token")
    private String mXsrfToken;


    /**
     * Instantiates a new Register user request data.
     *
     * @param firstName          first name.
     * @param lastName           last name.
     * @param emailNotifications is email notifications enable.
     * @param xsrfToken          xsrf token.
     */
    public RegisterUserRequestData(String firstName, String lastName, boolean emailNotifications, String xsrfToken) {
        mFirstName = firstName;
        mLastName = lastName;
        mEmailNotifications = emailNotifications;
        mXsrfToken = xsrfToken;
    }
}
