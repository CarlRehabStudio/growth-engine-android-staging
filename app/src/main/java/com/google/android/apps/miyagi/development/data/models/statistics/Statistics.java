package com.google.android.apps.miyagi.development.data.models.statistics;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Statistics {
    public static final String ARG_KEY = Statistics.class.getCanonicalName();

    @SerializedName("screen_title")
    protected String mScreenTitle;

    @SerializedName("pages")
    protected PagesWrapper mPages;

    public String getScreenTitle() {
        return mScreenTitle;
    }

    public PagesWrapper getPages() {
        return mPages;
    }
}
