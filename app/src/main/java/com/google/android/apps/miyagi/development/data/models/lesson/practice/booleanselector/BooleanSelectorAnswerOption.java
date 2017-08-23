package com.google.android.apps.miyagi.development.data.models.lesson.practice.booleanselector;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by lukaszweglinski on 07.12.2016.
 */

@Parcel
public class BooleanSelectorAnswerOption {

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
