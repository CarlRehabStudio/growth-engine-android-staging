package com.google.android.apps.miyagi.development.data.models.lesson.practice.swipe;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class SwipePracticeDetails {


    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("correct_option")
    protected String mCorrectOption;

    @SerializedName("colors")
    protected SwipePracticeColors mColors;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<SwipePracticeOption> mOptions;

    public SwipePracticeColors getColors() {
        return mColors;
    }

    public String getCorrectOption() {
        return mCorrectOption;
    }

    public List<SwipePracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }

}
