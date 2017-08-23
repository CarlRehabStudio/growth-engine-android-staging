package com.google.android.apps.miyagi.development.data.models.dashboard.statistics;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 08.12.2016.
 */

@Parcel
public class PageEntity {

    @SerializedName("type")
    protected String mType;

    @SerializedName("progress")
    protected int mProgress;

    @SerializedName("background_color")
    protected String mBackgroundColorString;

    @SerializedName("progress_title")
    protected String mProgressTitle;

    @SerializedName("section_header_title")
    protected String mSectionHeaderTitle;

    @SerializedName("badges")
    protected List<BadgeEntity> mBadges;

    public int getBackgroundColor() {
        return ColorHelper.parseColor(mBackgroundColorString);
    }

    public List<BadgeEntity> getBadges() {
        return mBadges;
    }

    public int getProgress() {
        return mProgress;
    }

    public String getProgressTitle() {
        return mProgressTitle;
    }

    public String getSectionHeaderTitle() {
        return mSectionHeaderTitle;
    }

    public String getType() {
        return mType;
    }
}
