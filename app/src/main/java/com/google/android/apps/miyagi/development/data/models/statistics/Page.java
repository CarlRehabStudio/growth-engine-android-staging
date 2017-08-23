package com.google.android.apps.miyagi.development.data.models.statistics;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

@Parcel
public class Page {

    @SerializedName("background_color")
    protected String mBackgroundColorString;

    @SerializedName("section_header_title")
    protected String mSectionHeaderTitle;

    @SerializedName("progress_title")
    protected String mProgressTitle;

    @SerializedName("progress")
    protected int mProgress;

    @SerializedName("type")
    protected String mType;

    @SerializedName("badges")
    protected List<Badge> mBadges;

    @SerializedName("speedometer_color_empty")
    protected String mSpeedometerColorEmptyString;

    @SerializedName("speedometer_color_full")
    protected String mSpeedometerColorFullString;

    public int getSpeedometerColorEmpty() {
        return ColorHelper.parseColor(mSpeedometerColorEmptyString);
    }

    public int getSpeedometerColorFull() {
        return ColorHelper.parseColor(mSpeedometerColorFullString);
    }

    public int getBackgroundColor() {
        return ColorHelper.parseColor(mBackgroundColorString);
    }

    public String getSectionHeaderTitle() {
        return mSectionHeaderTitle;
    }

    public String getProgressTitle() {
        return mProgressTitle;
    }

    public int getProgress() {
        return mProgress;
    }

    public String getType() {
        return mType;
    }

    public List<Badge> getBadges() {
        return mBadges;
    }

}
