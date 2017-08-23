package com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 05.12.2016.
 */

@Parcel
public class SelectLargePracticeColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("header_color")
    protected String mHeaderColorString;

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    public int getHeaderColor() {
        return ColorHelper.parseColor(mHeaderColorString);
    }
}
