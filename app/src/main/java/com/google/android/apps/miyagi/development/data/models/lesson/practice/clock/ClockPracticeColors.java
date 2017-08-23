package com.google.android.apps.miyagi.development.data.models.lesson.practice.clock;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jerzyw on 05.12.2016.
 */

@Parcel
public class ClockPracticeColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }
}
