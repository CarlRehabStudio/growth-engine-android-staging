package com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector;

import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 06.12.2016.
 */

@Parcel
public class BooleanSelectorColors {

    @SerializedName("lesson_background_color")
    protected String mLessonBackgroundColorString;

    @SerializedName("card_active_color")
    protected String mCardActiveColorString;

    @SerializedName("card_inactive_color")
    protected String mCardInactiveColorString;

    @SerializedName("text_active_color")
    protected String mTextActiveColorString;

    @SerializedName("text_inactive_color")
    protected String mTextInactiveColorString;

    @SerializedName("button_border_color")
    protected String mButtonBorderColorString;

    @SerializedName("button_background_color")
    protected String mButtonBackgroundColorString;

    public int getButtonBackgroundColor() {
        return ColorHelper.parseColor(mButtonBackgroundColorString);
    }

    public int getButtonBorderColor() {
        return ColorHelper.parseColor(mButtonBorderColorString);
    }

    public int getLessonBackgroundColor() {
        return ColorHelper.parseColor(mLessonBackgroundColorString);
    }

    public int getCardActiveColor() {
        return ColorHelper.parseColor(mCardActiveColorString);
    }

    public int getCardInactiveColor() {
        return ColorHelper.parseColor(mCardInactiveColorString);
    }

    public int getTextActiveColor() {
        return ColorHelper.parseColor(mTextActiveColorString);
    }

    public int getTextInactiveColor() {
        return ColorHelper.parseColor(mTextInactiveColorString);
    }
}
