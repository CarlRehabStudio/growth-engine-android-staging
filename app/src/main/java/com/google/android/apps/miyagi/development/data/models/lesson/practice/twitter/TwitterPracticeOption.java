package com.google.android.apps.miyagi.development.data.models.lesson.practice.twitter;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class TwitterPracticeOption {

    @SerializedName("id")
    protected String mId;

    @SerializedName("text")
    protected String mText;

    @SerializedName("handle")
    protected String mHandle;

    public String getId() {
        return mId;
    }

    public String getText() {
        return mText;
    }

    public String getHandle() {
        return mHandle;
    }
}