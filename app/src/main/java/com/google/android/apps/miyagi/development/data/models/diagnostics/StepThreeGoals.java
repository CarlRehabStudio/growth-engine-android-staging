package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class StepThreeGoals {

    @SerializedName("header_text")
    protected String mHeaderText;

    @SerializedName("subheader_text")
    protected String mSubHeaderText;

    @SerializedName("options")
    protected List<StepThreeOptions> mOptions;

    public String getHeaderText() {
        return mHeaderText;
    }

    public String getSubHeaderText() {
        return mSubHeaderText;
    }

    public List<StepThreeOptions> getOptions() {
        return mOptions;
    }
}
