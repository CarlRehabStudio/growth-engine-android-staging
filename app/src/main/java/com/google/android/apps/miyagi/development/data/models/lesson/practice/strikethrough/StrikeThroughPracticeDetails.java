package com.google.android.apps.miyagi.development.data.models.lesson.practice.strikethrough;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 09.12.2016.
 */

@Parcel
public class StrikeThroughPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected StrikeThroughPracticeColors mColors;

    @SerializedName("correct_options")
    protected List<String> mCorrectOptions;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<StrikeThroughPracticeOption> mOptions;

    public StrikeThroughPracticeColors getColors() {
        return mColors;
    }

    public List<String> getCorrectOptions() {
        return mCorrectOptions;
    }

    public List<StrikeThroughPracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }

}
