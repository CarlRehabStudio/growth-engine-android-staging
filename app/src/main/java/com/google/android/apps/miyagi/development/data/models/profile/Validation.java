package com.google.android.apps.miyagi.development.data.models.profile;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

@Parcel
public class Validation {

    @SerializedName("empty_first_name")
    protected String mEmptyFirstName;

    @SerializedName("empty_last_name")
    protected String mEmptyLastName;

    public String getEmptyFirstName() {
        return mEmptyFirstName;
    }

    public String getEmptyLastName() {
        return mEmptyLastName;
    }
}
