package com.google.android.apps.miyagi.development.data.models.lesson.practice.tagcloud;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class TagCloudPracticeOption {

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
