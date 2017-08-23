package com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class TwitterPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected TwitterPracticeColors mColors;

    @SerializedName("info")
    protected TwitterPracticeInfo mInfo;

    @SerializedName("correct_option")
    protected String mCorrectOption;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<TwitterPracticeOption> mOptions;

    public TwitterPracticeColors getColors() {
        return mColors;
    }

    public String getCorrectOption() {
        return mCorrectOption;
    }

    public TwitterPracticeInfo getInfo() {
        return mInfo;
    }

    public List<TwitterPracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }
}