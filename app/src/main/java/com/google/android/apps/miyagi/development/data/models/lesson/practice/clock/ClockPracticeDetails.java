package com.google.android.apps.miyagi.development.data.models.lesson.practice.clock;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by jerzyw on 05.12.2016.
 */

@Parcel
public class ClockPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("activity_id")
    protected String mActivityId;

    @SerializedName("correct_option")
    protected String mCorrectOptionId;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("colors")
    protected ClockPracticeColors mColors;

    @SerializedName("options")
    protected List<ClockAnswerOption> mOptions;

    public String getActivityId() {
        return mActivityId;
    }

    public ClockPracticeColors getColors() {
        return mColors;
    }

    public String getCorrectOptionId() {
        return mCorrectOptionId;
    }

    public List<ClockAnswerOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }
}
