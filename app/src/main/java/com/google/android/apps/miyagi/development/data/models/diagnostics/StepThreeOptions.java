package com.google.android.apps.miyagi.development.data.models.diagnostics;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Lukasz on 17.12.2016.
 */

@Parcel
public class StepThreeOptions {

    @SerializedName("option_id")
    protected String mId;

    @SerializedName("option_text")
    protected String mText;

    @SerializedName("option_color")
    protected String mColor;

    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public int getColor() {
        return ColorHelper.parseColor(mColor);
    }
}
