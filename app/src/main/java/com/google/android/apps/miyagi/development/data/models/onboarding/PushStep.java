package com.google.android.apps.miyagi.development.data.models.onboarding;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 06.02.2017.
 */

@Parcel
public class PushStep {

    public static final String ARG_KEY = PushStep.class.getCanonicalName();

    @SerializedName("images")
    protected ImagesBaseModel mImages;

    @SerializedName("header_text")
    protected String mTitle;

    @SerializedName("background_color")
    protected String mBackgroundString;

    public ImagesBaseModel getImages() {
        return mImages;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getBackgroundColor() {
        return ColorHelper.parseColor(mBackgroundString);
    }
}