package com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class SwipePracticeColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("phone_color")
    protected String mPhoneColorString;

    public int getLessonColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    public int getPhoneColor() {
        return ColorHelper.parseColor(mPhoneColorString);
    }

}
