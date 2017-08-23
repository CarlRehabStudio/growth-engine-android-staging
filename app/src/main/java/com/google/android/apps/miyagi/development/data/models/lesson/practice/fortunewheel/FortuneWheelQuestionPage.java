package com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by jerzyw on 23.11.2016.
 */

@Parcel
public class FortuneWheelQuestionPage {

    @SerializedName("id")
    protected String mId;

    @SerializedName("text")
    protected String mText;

    @SerializedName("correct_option")
    protected String mCorrectOption;

    @SerializedName("label")
    protected String mLabel;

    @SerializedName("options")
    protected List<FortuneWheelAnswerOption> mOptions;

    public String getCorrectOption() {
        return mCorrectOption;
    }

    public String getId() {
        return mId;
    }

    public String getLabel() {
        return mLabel;
    }

    public List<FortuneWheelAnswerOption> getOptions() {
        return mOptions;
    }

    public String getText() {
        return mText;
    }
}
