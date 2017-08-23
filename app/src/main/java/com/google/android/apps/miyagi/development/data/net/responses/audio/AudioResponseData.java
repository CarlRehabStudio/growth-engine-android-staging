package com.google.android.apps.miyagi.development.data.net.responses.audio;

import com.google.android.apps.miyagi.development.data.models.ImagesBaseModel;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.helpers.RealmListParcelConverter;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import org.parceler.Parcel;
import org.parceler.ParcelPropertyConverter;

/**
 * Created by jerzyw on 19.12.2016.
 */

@Parcel
public class AudioResponseData extends RealmObject {

    @SerializedName("topic_id")
    protected int mTopicId;

    @SerializedName("lessons_in_topic_header")
    protected String mLessonsInTopicHeader;

    @SerializedName("download_audio")
    protected String mDownloadAudioLabel;

    @SerializedName("delete_audio")
    protected String mDeleteAudioLabel;

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("play_audio")
    protected String mPlayAudioLabel;

    @SerializedName("pause_audio")
    protected String mPauseAudioLabel;

    @ParcelPropertyConverter(RealmListParcelConverter.class)
    @SerializedName("lessons")
    protected RealmList<AudioLesson> mLessons;

    @SerializedName("file_url")
    protected String mFileUrl;

    @SerializedName("lesson_count")
    protected String mLessonCount;

    @SerializedName("next_topic_header")
    protected String mNextTopicHeader;

    @SerializedName("description")
    protected String mDescription;

    @SerializedName("menu_view_transcript")
    protected String mMenuViewTranscript;

    @SerializedName("icon")
    protected ImagesBaseModel mIcon;

    public String getDownloadAudioLabel() {
        return mDownloadAudioLabel;
    }

    public String getDeleteAudioLabel() {
        return mDeleteAudioLabel;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public ImagesBaseModel getIcon() {
        return mIcon;
    }

    public String getLessonCount() {
        return mLessonCount;
    }

    public RealmList<AudioLesson> getLessons() {
        return mLessons;
    }

    public String getLessonsInTopicHeader() {
        return mLessonsInTopicHeader;
    }

    public String getMenuViewTranscript() {
        return mMenuViewTranscript;
    }

    public String getNextTopicHeader() {
        return mNextTopicHeader;
    }

    public String getPlayAudioLabel() {
        return mPlayAudioLabel;
    }

    public String getPauseAudioLabel() {
        return mPauseAudioLabel;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getTopicId() {
        return mTopicId;
    }
}
