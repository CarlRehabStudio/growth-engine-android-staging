package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jerzyw on 07.11.2016.
 */

public class DashbordHeaderAction {
    @SerializedName("type")
    private int mRawType;

    @SerializedName("topic_id")
    private long mTopicId;

    @SerializedName("lesson_id")
    private long mLessonId;

    @SerializedName("exam_id")
    private long mExamId;

    @SerializedName("certificate_url")
    private String mCertificateUrl;

    public int getRawType() {
        return mRawType;
    }

    public long getTopicId() {
        return mTopicId;
    }

    public long getLessonId() {
        return mLessonId;
    }

    public long getExamId() {
        return mExamId;
    }

    public String getCertificateUrl() {
        return mCertificateUrl;
    }
}
