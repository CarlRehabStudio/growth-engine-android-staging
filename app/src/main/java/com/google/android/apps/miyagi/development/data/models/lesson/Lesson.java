package com.google.android.apps.miyagi.development.data.models.lesson;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.helpers.ColorHelper;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by marcinarciszew on 22.11.2016.
 */

@Parcel
public class Lesson {

    public static final String ARG_ID = Lesson.class.getCanonicalName();
    public static final int ASSESSMENT = -10;

    @SerializedName("lesson_title")
    protected String mLessonTitle;

    @SerializedName("youtube_url")
    protected String mYoutubeUrl;

    @SerializedName("all_lessons_title")
    protected String mAllLessonsTitle;

    @SerializedName("lesson_share_url")
    protected String mLessonShareUrl;

    @SerializedName("topic_color")
    protected String mTopicColorString;

    @SerializedName("topic_title")
    protected String mTopicTitle;

    @SerializedName("lesson_transcript_text")
    protected String mLessonTranscriptText;

    @SerializedName("lesson_practice")
    protected String mLessonPractice;

    @SerializedName("lesson_key_learnings")
    protected String mLessonKeyLearnings;

    @SerializedName("lesson_share_title")
    protected String mLessonShareTitle;

    @SerializedName("lessons_in_topic")
    protected List<TopicLesson> mTopicLessons;

    @SerializedName("lesson_description")
    protected String mLessonDescription;

    @SerializedName("lesson_share_text")
    protected String mLessonShareText;

    @SerializedName("lesson_transcript_title")
    protected String mLessonTranscriptTitle;

    @SerializedName("player_image")
    protected ImagesBaseModel mPlayerImage;

    @SerializedName("lesson_length")
    protected String mLessonLength;

    @SerializedName("lesson_id")
    protected int mLessonId;

    @SerializedName("header")
    protected String mHeader;

    public String getAllLessonsTitle() {
        return mAllLessonsTitle;
    }

    public String getLessonDescription() {
        return mLessonDescription;
    }

    public int getLessonId() {
        return mLessonId;
    }

    public String getLessonKeyLearnings() {
        return mLessonKeyLearnings;
    }

    public String getLessonLength() {
        return mLessonLength;
    }

    public String getLessonPractice() {
        return mLessonPractice;
    }

    public String getLessonShareText() {
        return mLessonShareText;
    }

    public String getLessonShareTitle() {
        return mLessonShareTitle;
    }

    public String getLessonShareUrl() {
        return mLessonShareUrl;
    }

    public String getLessonTitle() {
        return mLessonTitle;
    }

    public String getLessonTranscriptText() {
        return mLessonTranscriptText;
    }

    public String getLessonTranscriptTitle() {
        return mLessonTranscriptTitle;
    }

    public ImagesBaseModel getPlayerImage() {
        return mPlayerImage;
    }

    public int getTopicColor() {
        return ColorHelper.parseColor(mTopicColorString);
    }

    public List<TopicLesson> getTopicLessons() {
        return mTopicLessons;
    }

    public String getTopicTitle() {
        return mTopicTitle;
    }

    public String getYoutubeUrl() {
        return mYoutubeUrl;
    }

    public String getHeader() {
        return mHeader;
    }
}
