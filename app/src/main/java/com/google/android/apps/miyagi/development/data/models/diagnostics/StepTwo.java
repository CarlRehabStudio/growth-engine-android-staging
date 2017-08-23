package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class StepTwo {

    @SerializedName("certification_title")
    protected String mCertificationTitle;

    @SerializedName("certification_text")
    protected String mCertificationText;

    @SerializedName("plan_title")
    protected String mPlanTitle;

    @SerializedName("plan_text")
    protected String mPlanText;

    @SerializedName("header_text")
    protected String mHeaderText;

    @SerializedName("subheader_text")
    protected String mSubHeaderText;

    public String getSubHeaderText() {
        return mSubHeaderText;
    }

    public String getCertificationTitle() {
        return mCertificationTitle;
    }

    public String getCertificationText() {
        return mCertificationText;
    }

    public String getPlanTitle() {
        return mPlanTitle;
    }

    public String getPlanText() {
        return mPlanText;
    }

    public String getHeaderText() {
        return mHeaderText;
    }
}
