package com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 05.12.2016.
 */

@Parcel
public class SwitchesTextPracticeColors {
    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("border_color")
    protected String mBorderColorString;

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    public int getBorderColor() {
        return ColorHelper.parseColor(mBorderColorString);
    }
}
