package com.google.android.apps.miyagi.development.data.models.lesson.practice.selectright;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by lukaszweglinski on 13.12.2016.
 */

@Parcel
public class SelectRightPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected SelectRightPracticeDetailsColors mColors;

    @SerializedName("correct_options")
    protected List<Integer> mCorrectOptionId;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<SelectRightPracticeOption> mOptions;

    public SelectRightPracticeDetailsColors getColors() {
        return mColors;
    }

    public List<Integer> getCorrectOptions() {
        return mCorrectOptionId;
    }

    public List<SelectRightPracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }
}
