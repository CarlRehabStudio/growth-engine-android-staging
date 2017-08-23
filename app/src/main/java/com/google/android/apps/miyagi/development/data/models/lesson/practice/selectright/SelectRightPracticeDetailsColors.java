package com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 12.01.2017.
 */

@Parcel
public class SelectRightPracticeDetailsColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }
}
