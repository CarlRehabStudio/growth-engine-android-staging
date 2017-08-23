package com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jerzyw on 23.11.2016.
 */
@Parcel
public class FortuneWheelPracticeColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("marker_color")
    protected String mMarkerColorString;

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    public int getMarkerColor() {
        return ColorHelper.parseColor(mMarkerColorString);
    }

}
