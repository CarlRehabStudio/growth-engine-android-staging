package com.google.android.apps.miyagi.development.data.models.lesson.practice.selectlarge;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 05.12.2016.
 */

@Parcel
public class SelectLargePracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected SelectLargePracticeColors mColors;

    @SerializedName("correct_options")
    protected List<String> mCorrectOptions;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<SelectLargePracticeOption> mOptions;

    public SelectLargePracticeColors getColors() {
        return mColors;
    }

    public String getCorrectOptions() {
        return mCorrectOptions.get(0);
    }

    public List<SelectLargePracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }
}
