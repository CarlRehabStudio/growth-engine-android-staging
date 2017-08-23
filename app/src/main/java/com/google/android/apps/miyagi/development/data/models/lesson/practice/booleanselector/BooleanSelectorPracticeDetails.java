package com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by lukaszweglinski on 06.12.2016.
 */

@Parcel
public class BooleanSelectorPracticeDetails {

    @SerializedName("activity_type")
    protected String mType;

    @SerializedName("colors")
    protected BooleanSelectorColors mColors;

    @SerializedName("question")
    protected String mQuestion;

    @SerializedName("options")
    protected List<BooleanSelectorPracticeOption> mOptions;

    public BooleanSelectorColors getColors() {
        return mColors;
    }

    public List<BooleanSelectorPracticeOption> getOptions() {
        return mOptions;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getType() {
        return mType;
    }
}
