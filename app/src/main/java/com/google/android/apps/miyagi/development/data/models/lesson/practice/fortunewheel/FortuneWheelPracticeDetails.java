package com.google.android.apps.miyagi.development.data.models.lesson.practice.fortunewheel;


import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by jerzyw on 16.11.2016.
 */

@Parcel
public class FortuneWheelPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected FortuneWheelPracticeColors mColors;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<FortuneWheelQuestionPage> mPages;

    @SerializedName("next_cta")
    protected String mPracticeNextStep;

    public FortuneWheelPracticeColors getColors() {
        return mColors;
    }

    public List<FortuneWheelQuestionPage> getPages() {
        return mPages;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }

    public String getPracticeNextStep() {
        return mPracticeNextStep;
    }
}
