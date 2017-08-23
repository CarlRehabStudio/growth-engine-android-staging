package com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by jerzyw on 13.12.2016.
 */

@Parcel
public class ReorderPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected ReorderPracticeColors mColors;

    @SerializedName("correct_order")
    protected List<String> mCorrectAnswers;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<ReorderPracticeAnswerOption> mPracticeAnswers;

    public ReorderPracticeColors getColors() {
        return mColors;
    }

    public List<String> getCorrectAnswers() {
        return mCorrectAnswers;
    }

    public List<ReorderPracticeAnswerOption> getPracticeAnswers() {
        return mPracticeAnswers;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }

}
