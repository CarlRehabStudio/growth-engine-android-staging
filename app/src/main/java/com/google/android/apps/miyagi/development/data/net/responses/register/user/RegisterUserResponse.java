package com.google.android.apps.miyagi.development.data.net.responses.register.user;

import com.google.android.apps.miyagi.development.data.net.responses.core.BaseResponse;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public class RegisterUserResponse extends BaseResponse<RegisterUserResponseData> {

    public interface StatusCodes {
        int OK = 1;
        /**
         * Code 2: the request did not have a firebase token attached to it.
         */
        int ERROR_MISSING_FIREBASE_TOKEN = 2;
        /**
         * Code 3: the user in the request already has an account.
         */
        int ERROR_USER_HAS_ALREADY_ACCOUNT = 3;
        /**
         * Code 4: the user’s account is currently being deleted and should wait for it to be completely deleted.
         */
        int ERROR_ACCOUNT_IS_CURRENTLY_BEING_DELETED = 4;
        /**
         * Code 5: the XSRF token included in the request is not valid.
         */
        int ERROR_TOKEN_NOT_VALID = 5;

    }
}
