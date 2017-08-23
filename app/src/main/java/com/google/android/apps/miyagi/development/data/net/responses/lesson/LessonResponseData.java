package com.google.android.apps.miyagi.development.data.net.responses.lesson;

import com.google.android.apps.miyagi.development.data.models.lesson.Lesson;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.Practice;
import com.google.android.apps.miyagi.development.data.models.lesson.practice.PracticeFeedback;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class LessonResponseData {

    @SerializedName("lesson")
    protected Lesson mLesson;

    @SerializedName("lesson_xsrf_token")
    protected String mLessonXsrfToken;

    @SerializedName("feedback")
    protected PracticeFeedback mFeedback;

    @SerializedName("activity")
    protected Practice mPractice;

    public PracticeFeedback getFeedback() {
        return mFeedback;
    }

    public Lesson getLesson() {
        return mLesson;
    }

    public XsrfToken getLessonXsrfToken() {
        return new XsrfToken(mLessonXsrfToken);
    }

    public Practice getPractice() {
        return mPractice;
    }
}
