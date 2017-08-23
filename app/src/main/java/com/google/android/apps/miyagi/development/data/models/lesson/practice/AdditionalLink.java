package com.google.android.apps.miyagi.development.data.models.lesson.practice;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class AdditionalLink {

    @SerializedName("link_text")
    protected String mLinkText;

    @SerializedName("link_url")
    protected String mLinkUrl;

    public String getLinkText() {
        return mLinkText;
    }

    public String getLinkUrl() {
        return mLinkUrl;
    }

}
