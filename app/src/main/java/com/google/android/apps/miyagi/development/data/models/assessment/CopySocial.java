package com.google.android.apps.miyagi.development.data.models.assessment;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 09.01.2017.
 */

@Parcel
public class CopySocial {

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("image_url")
    protected String mImageUrl;

    @SerializedName("aria_label")
    protected String mAriaLabel;

    @SerializedName("share_url")
    protected String mShareUrl;

    public String getShareUrl() {
        return mShareUrl;
    }

    public String getAriaLabel() {
        return mAriaLabel;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }
}
