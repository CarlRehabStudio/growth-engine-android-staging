package com.google.android.apps.miyagi.development.data.net.responses.next.step;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcinarciszew on 09.11.2016.
 */

public class NextStepResponseData {
    public interface NextStep {
        int COMPLETE_PROFILE = 1;
        int DIAGNOSTICS = 2;
        int DASHBOARD = 3;
        int ACCOUNT_IS_BEING_DELETED = 4;
    }

    @SerializedName("next_step")
    protected int mNextStep;

    @SerializedName("xsrf_token")
    protected String mXsrfToken;

    public int getNextStep() {
        return mNextStep;
    }

    public String getXsrfToken() {
        return mXsrfToken;
    }
}
