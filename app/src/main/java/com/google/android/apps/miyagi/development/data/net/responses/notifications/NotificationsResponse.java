package com.google.android.apps.miyagi.development.data.net.responses.notifications;

import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;

/**
 * Created by emilzawadzki on 08.02.2017.
 */

public class NotificationsResponse extends BaseResponse<NotificationsResponseData> {

    public interface StatusCodes {
        int OK = 1;
        /**
         * Code 2: bad xsrf token.
         */
        int ERROR_BAD_XSRF_TOKEN = 2;
        /**
         * Code 3: missing fields in the request (push_notifications_token).
         */
        int ERROR_MISSING_FIREBASE_TOKEN = 3;
    }
}