package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lukasz on 27.10.2016.
 */

public class Topics {

    @SerializedName("id")
    protected int mId;

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("cta")
    protected String mCta;

    @SerializedName("description")
    protected String mDescription;

    @SerializedName("lessons_completed")
    protected boolean mLessonCompleted;

    @SerializedName("n_lessons_completed")
    protected int mNumberLessonCompleted;

    @SerializedName("progress_label")
    protected String mProgressLabel;

    @SerializedName("percentage_completed")
    protected float mPercentageCompleted;

    @SerializedName("started")
    protected boolean mStarted;

    @SerializedName("completed")
    protected boolean mCompleted;

    @SerializedName("color")
    protected String mColorString;

    @SerializedName("lessons")
    protected List<Lesson> mLesson;

    @SerializedName("next_lesson")
    protected Lesson mNextLesson;

    @SerializedName("icons")
    protected ImagesBaseModel mIcons;

    @SerializedName("quiz")
    protected Quiz mQuiz;

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getCta() {
        return mCta;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isLessonCompleted() {
        return mLessonCompleted;
    }

    public int getNumberLessonCompleted() {
        return mNumberLessonCompleted;
    }

    public String getProgressLabel() {
        return mProgressLabel;
    }

    public float getPercentageCompleted() {
        return mPercentageCompleted;
    }

    public boolean isStarted() {
        return mStarted;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public int getColor() {
        return ColorHelper.parseColor(mColorString);
    }

    public List<Lesson> getLesson() {
        return mLesson;
    }

    public Lesson getNextLesson() {
        return mNextLesson;
    }

    public ImagesBaseModel getIcons() {
        return mIcons;
    }

    public Quiz getQuiz() {
        return mQuiz;
    }
}
