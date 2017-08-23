package com.google.android.apps.miyagi.development.data.models.assessment;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 26.01.2017.
 */
@Parcel
public class ExamPassedAction {

    @SerializedName("type")
    protected int mType;

    @SerializedName("lesson_id")
    protected int mLessonId;

    @SerializedName("topic_id")
    protected int mTopicId;

    public int getLessonId() {
        return mLessonId;
    }

    public int getTopicId() {
        return mTopicId;
    }

    public int getType() {
        return mType;
    }
}
