package com.google.android.apps.miyagi.development.data.models.lesson.practice.switchestext;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 05.12.2016.
 */

@Parcel
public class SwitchesTextPracticeOption {

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
