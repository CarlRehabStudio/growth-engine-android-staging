package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by marcin on 14.01.2017.
 */

public class UpNextAction {

    @SerializedName("exam_id")
    private int mExamId;

    @SerializedName("type")
    private int mType;

    @SerializedName("topic_id")
    private int mTopicId;

    @SerializedName("lesson_id")
    private int mLessonId;

    @SerializedName("certificate_url")
    private String mCertificateUrl;

    public int getExamId() {
        return mExamId;
    }

    public int getType() {
        return mType;
    }

    public int getTopicId() {
        return mTopicId;
    }

    public int getLessonId() {
        return mLessonId;
    }

    public String getCertificateUrl() {
        return mCertificateUrl;
    }
}
