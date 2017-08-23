package com.google.android.apps.miyagi.development.data.models.onboarding;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 20.12.2016.
 */

@Parcel
public class OnboardingStep {

    public static final String ARG_KEY = OnboardingStep.class.getCanonicalName();

    @SerializedName("images")
    protected ImagesBaseModel mImages;

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("description")
    protected String mDescription;

    @SerializedName("background_color")
    protected String mBackgroundString;

    public ImagesBaseModel getImages() {
        return mImages;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getBackgroundColor() {
        return ColorHelper.parseColor(mBackgroundString);
    }
}
