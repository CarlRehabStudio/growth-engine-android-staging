package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class Common {

    @SerializedName("button_next_text")
    protected String mButtonNextText;

    @SerializedName("button_skip_text")
    protected String mButtonSkipText;

    @SerializedName("header_text")
    protected String mHeaderText;

    public String getButtonNextText() {
        return mButtonNextText;
    }

    public String getButtonSkipText() {
        return mButtonSkipText;
    }

    public String getHeaderText() {
        return mHeaderText;
    }
}

