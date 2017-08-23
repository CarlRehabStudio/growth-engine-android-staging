package com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class TagCloudPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("correct_options")
    protected List<String> mCorrectOptions;

    @SerializedName("colors")
    protected TagCloudPracticeColors mColors;

    @SerializedName("options")
    protected List<TagCloudPracticeOption> mOptions;

    public TagCloudPracticeColors getColors() {
        return mColors;
    }

    public List<String> getCorrectOptions() {
        return mCorrectOptions;
    }

    public List<TagCloudPracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }
}
