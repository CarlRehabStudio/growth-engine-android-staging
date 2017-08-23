package com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jerzyw on 13.12.2016.
 */

@Parcel
public class ReorderPracticeColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("numbers_color")
    protected String mPositionNumberColorString;

    @SerializedName("card_active_color")
    protected String mActiveCardColorString;

    @SerializedName("card_inactive_color")
    protected String mInactiveCardColorString;

    @SerializedName("text_active_color")
    protected String mActiveTextColorString;

    @SerializedName("text_inactive_color")
    protected String mInactiveTextColorString;

    @SerializedName("drag_handle_active_color")
    protected String mDragHandleActiveColorString;

    @SerializedName("drag_handle_inactive_color")
    protected String mDragHandleInactiveColorString;

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    public int getPositionNumberColor() {
        return ColorHelper.parseColor(mPositionNumberColorString);
    }

    public int getActiveCardColor() {
        return ColorHelper.parseColor(mActiveCardColorString);
    }

    public int getInactiveCardColor() {
        return ColorHelper.parseColor(mInactiveCardColorString);
    }

    public int getActiveTextColor() {
        return ColorHelper.parseColor(mActiveTextColorString);
    }

    public int getInactiveTextColor() {
        return ColorHelper.parseColor(mInactiveTextColorString);
    }

    public int getDragHandleActiveColor() {
        return ColorHelper.parseColor(mDragHandleActiveColorString);
    }

    public int getDragHandleInactiveColor() {
        return ColorHelper.parseColor(mDragHandleInactiveColorString);
    }
}
