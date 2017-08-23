package com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by lukaszweglinski on 06.12.2016.
 */

@Parcel
public class BooleanSelectorPracticeOption {

    @SerializedName("text")
    protected String mText;

    @SerializedName("correct_option")
    protected String mCorrectOption;

    @SerializedName("options")
    protected List<BooleanSelectorAnswerOption> mBooleanSelectorAnswerOption;

    @SerializedName("id")
    protected String mId;

    public String getText() {
        return mText;
    }

    public String getCorrectOption() {
        return mCorrectOption;
    }

    public List<BooleanSelectorAnswerOption> getBooleanSelectorAnswerOption() {
        return mBooleanSelectorAnswerOption;
    }

    public String getId() {
        return mId;
    }
}
