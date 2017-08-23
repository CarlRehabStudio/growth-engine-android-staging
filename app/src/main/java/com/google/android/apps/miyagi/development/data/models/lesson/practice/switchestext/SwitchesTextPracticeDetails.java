package com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 05.12.2016.
 */

@Parcel
public class SwitchesTextPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected SwitchesTextPracticeColors mColors;

    @SerializedName("correct_options")
    protected List<String> mCorrectOptions;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<SwitchesTextPracticeOption> mOptions;

    public SwitchesTextPracticeColors getColors() {
        return mColors;
    }

    public List<String> getCorrectOptions() {
        return mCorrectOptions;
    }

    public List<SwitchesTextPracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }

}
