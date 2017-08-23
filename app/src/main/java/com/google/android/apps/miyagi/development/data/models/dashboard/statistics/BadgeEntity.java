package com.google.android.apps.miyagi.development.data.models.dashboard.statistics;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class BadgeEntity {
    @SerializedName("badge_url")
    protected String mBadgeUrl;

    @SerializedName("badge_background_color")
    protected String mBadgeBackgroundColorString;

    @SerializedName("badge_title")
    protected String mBadgeTitle;

    public String getBadgeUrl() {
        return mBadgeUrl;
    }
    // TODO: set badge icon from url!!!

    public int getBadgeBackgroundColor() {
        return ColorHelper.parseColor(mBadgeBackgroundColorString);
    }

    public String getBadgeTitle() {
        return mBadgeTitle;
    }
}
