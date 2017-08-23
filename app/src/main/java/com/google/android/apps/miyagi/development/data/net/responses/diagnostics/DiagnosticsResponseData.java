package com.google.android.apps.miyagi.development.data.net.responses.diagnostics;

import com.google.android.apps.miyagi.development.data.models.diagnostics.Common;
import com.google.android.apps.miyagi.development.data.models.diagnostics.Loading;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepOne;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepThree;
import com.google.android.apps.miyagi.development.data.models.diagnostics.StepTwo;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class DiagnosticsResponseData {

    @SerializedName("common")
    protected Common mCommon;

    @SerializedName("loading")
    protected Loading mLoading;

    @SerializedName("step_one")
    protected StepOne mStepOne;

    @SerializedName("step_two")
    protected StepTwo mStepTwo;

    @SerializedName("step_three")
    protected StepThree mStepThree;

    @SerializedName("diagnostics_xsrf_token")
    protected String mXsrfToken;

    public Common getCommon() {
        return mCommon;
    }

    public Loading getLoading() {
        return mLoading;
    }

    public StepTwo getStepTwo() {
        return mStepTwo;
    }

    public StepOne getStepOne() {
        return mStepOne;
    }

    public StepThree getStepThree() {
        return mStepThree;
    }

    public String getXsrfToken() {
        return mXsrfToken;
    }
}
