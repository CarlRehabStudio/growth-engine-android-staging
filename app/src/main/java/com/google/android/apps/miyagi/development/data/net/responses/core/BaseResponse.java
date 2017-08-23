package com.google.android.apps.miyagi.development.data.net.responses.core;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jerzyw on 11.10.2016.
 */

public abstract class BaseResponse<R> {

    public interface BaseStatusCodes {
        int UNKNOWN_CODE = -1;
        int OK = 1;
    }

    @SerializedName("status")
    private ResponseStatus mStatus;

    @SerializedName("response")
    private R mResponse;

    public ResponseStatus getStatus() {
        return mStatus;
    }

    public int getStatusCode() {
        return mStatus == null ? BaseStatusCodes.UNKNOWN_CODE : mStatus.getCode();
    }

    public String getStatusMessage() {
        return mStatus == null ? "" : mStatus.getMessage();
    }

    public R getResponseData() {
        return mResponse;
    }
}
