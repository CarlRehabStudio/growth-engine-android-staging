package com.google.android.apps.miyagi.development.data.net.responses.notifications;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public class NotificationsRequestData {

    @SerializedName("push_notifications_token")
    private String mPushToken;
    @SerializedName("xsrf_token")
    private String mXsrfToken;


    /**
     * Instantiates a new Register user request data.
     *
     * @param pushToken token from firebase.
     * @param xsrfToken          xsrf token.
     */
    public NotificationsRequestData(String pushToken, String xsrfToken) {
        mPushToken = pushToken;
        mXsrfToken = xsrfToken;
    }

}
