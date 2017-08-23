package com.google.android.apps.miyagi.development.data.models.lesson.practice.reorder;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jerzyw on 13.12.2016.
 */

@Parcel
public class ReorderPracticeAnswerOption {

    @SerializedName("id")
    protected String mId;

    @SerializedName("text")
    protected String mText;

    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

}
