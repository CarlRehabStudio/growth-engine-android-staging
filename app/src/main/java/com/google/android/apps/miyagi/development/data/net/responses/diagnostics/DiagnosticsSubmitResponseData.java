package com.google.android.apps.miyagi.development.data.net.responses.diagnostics;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukaszweglinski on 22.12.2016.
 */

public class DiagnosticsSubmitResponseData {

    @SerializedName("status")
    protected int mStatus;

    public int getStatus() {
        return mStatus;
    }
}
