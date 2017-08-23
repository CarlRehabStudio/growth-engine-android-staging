package com.google.android.apps.miyagi.development.data.models.profile;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 11.01.2017.
 */

@Parcel
public class Personal {

    @SerializedName("header")
    protected String mHeader;

    @SerializedName("first_name_label")
    protected String mFirstNameLabel;

    @SerializedName("last_name_label")
    protected String mLastNameLabel;

    public String getHeader() {
        return mHeader;
    }

    public String getFirstNameLabel() {
        return mFirstNameLabel;
    }

    public String getLastNameLabel() {
        return mLastNameLabel;
    }
}
