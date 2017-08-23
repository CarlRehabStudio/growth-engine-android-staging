package com.google.android.apps.miyagi.development.data.models.lesson;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class TopicLesson {

    @SerializedName("lesson_id")
    protected int mLessonId;

    @SerializedName("lesson_header")
    protected String mLessonHeader;

    @SerializedName("lesson_title")
    protected String mLessonTitle;

    @SerializedName("lesson_state")
    protected int mLessonState;

    @SerializedName("lesson_image_url")
    protected ImagesBaseModel mLessonImageUrl;

    public String getLessonHeader() {
        return mLessonHeader;
    }

    public int getLessonId() {
        return mLessonId;
    }

    public ImagesBaseModel getLessonImageUrl() {
        return mLessonImageUrl;
    }

    public int getLessonState() {
        return mLessonState;
    }

    public String getLessonTitle() {
        return mLessonTitle;
    }
}
