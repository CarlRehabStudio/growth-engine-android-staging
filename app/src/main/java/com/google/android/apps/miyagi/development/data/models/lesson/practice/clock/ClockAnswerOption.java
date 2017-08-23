package com.google.android.apps.miyagi.development.data.models.lesson.practice.clock;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jerzyw on 05.12.2016.
 */
@Parcel
public class ClockAnswerOption {

    @SerializedName("id")
    protected int mId;

    @SerializedName("value")
    protected String mValue;

    @SerializedName("face_id")
    protected int mFaceId;

    public int getId() {
        return mId;
    }

    public String getValue() {
        return mValue;
    }

    public int getFaceId() {
        return mFaceId;
    }
}
