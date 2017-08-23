package com.google.android.apps.miyagi.development.data.models.assessment;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcin on 29.12.2016.
 */

@Parcel
public class CopyItem {

    @SerializedName("subhead")
    protected String mSubhead;

    @SerializedName("cta")
    protected String mCta;

    @SerializedName("title")
    protected String mTitle;

    public String getCta() {
        return mCta;
    }

    public String getSubhead() {
        return mSubhead;
    }

    public String getTitle() {
        return mTitle;
    }

}
