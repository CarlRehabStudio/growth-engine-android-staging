package com.google.android.apps.miyagi.development.data.models.statistics;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 10.01.2017.
 */

@Parcel
public class Badge implements IBadgeItem {

    @SerializedName("icons")
    protected ImagesBaseModel mIcon;

    @SerializedName("badge_complete")
    protected boolean mBadgeComplete;

    @SerializedName("badge_background_color")
    protected String mBadgeBackgroundColorString;

    @SerializedName("badge_title")
    protected String mBadgeTitle;

    public ImagesBaseModel getIcon() {
        return mIcon;
    }

    public boolean isBadgeComplete() {
        return mBadgeComplete;
    }

    public int getBadgeBackgroundColor() {
        return ColorHelper.parseColor(mBadgeBackgroundColorString);
    }

    public String getBadgeTitle() {
        return mBadgeTitle;
    }

    @Override
    public int getType() {
        return TYPE_ITEM;
    }
}
