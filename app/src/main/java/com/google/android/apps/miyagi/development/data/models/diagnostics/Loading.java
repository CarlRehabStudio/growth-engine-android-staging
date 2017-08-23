package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class Loading {

    @SerializedName("image_aria")
    protected String mImageAria;

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("subhead")
    protected String mSubhead;

    public String getImageAria() {
        return mImageAria;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubhead() {
        return mSubhead;
    }
}
