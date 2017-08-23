package com.google.android.apps.miyagi.development.data.error;

import android.support.annotation.Nullable;

/**
 * Created by jerzyw on 03.11.2016.
 */

public class ResponseStatusException extends RuntimeException {

    private final int mStatusCode;
    private String mServerMessage;

    /**
     * @param statusCode    - response status code (not http code)
     * @param serverMessage - message server. Not null if statusCode!= OK
     */
    public ResponseStatusException(int statusCode, @Nullable String serverMessage) {
        super("Wrong status code: " + statusCode);
        mStatusCode = statusCode;
        mServerMessage = serverMessage;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getServerMessage() {
        return mServerMessage;
    }
}
