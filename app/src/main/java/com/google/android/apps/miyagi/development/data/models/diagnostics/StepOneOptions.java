package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class StepOneOptions {

    @SerializedName("option_id")
    protected String mId;

    @SerializedName("option_text")
    protected String mOptionText;

    public String getId() {
        return mId;
    }

    public String getOptionText() {
        return mOptionText;
    }
}
