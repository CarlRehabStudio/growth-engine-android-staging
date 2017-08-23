package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lukasz on 27.12.2016.
 */

public class Certification {

    @SerializedName("label")
    private String mLabel;

    @SerializedName("title")
    private String mTitle;

    public String getLabel() {
        return mLabel;
    }

    public String getTitle() {
        return mTitle;
    }
}
