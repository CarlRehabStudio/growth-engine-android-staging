package com.google.android.apps.miyagi.development.data.net.responses.core;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jerzyw on 11.10.2016.
 */

public class ResponseStatus {

    @SerializedName("code")
    private int mCode;

    @SerializedName("message")
    private String mMessage;

    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }
}
