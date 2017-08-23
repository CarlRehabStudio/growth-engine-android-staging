package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class StepOne {

    @SerializedName("header_text")
    protected String mHeaderText;

    @SerializedName("subheader_text")
    protected String mSubHeaderText;

    @SerializedName("options")
    protected List<StepOneOptions> mOptions;

    public String getSubHeaderText() {
        return mSubHeaderText;
    }

    public String getHeaderText() {
        return mHeaderText;
    }

    public List<StepOneOptions> getOptions() {
        return mOptions;
    }
}
