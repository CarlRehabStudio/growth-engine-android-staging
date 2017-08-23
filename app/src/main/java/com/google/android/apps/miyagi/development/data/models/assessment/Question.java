package com.google.android.apps.miyagi.development.data.models.assessment;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Question {

    @SerializedName("instanceid")
    protected String mInstanceId;

    @SerializedName("index_label")
    protected String mIndexLabel;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("correct")
    protected boolean mCorrect;

    @SerializedName("choices")
    protected List<String> mChoices;

    public List<String> getChoices() {
        return mChoices;
    }

    public String getIndexLabel() {
        return mIndexLabel;
    }

    public String getInstanceId() {
        return mInstanceId;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public boolean isCorrect() {
        return mCorrect;
    }
}
