package com.google.android.apps.miyagi.development.data.models.audio;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

/**
 * Created by jerzyw on 19.12.2016.
 */

@Parcel
public class AudioLesson extends RealmObject {

    @PrimaryKey
    @SerializedName("lesson_id")
    protected int mLessonId;

    @SerializedName("cue_point")
    protected float mCuePoint;

    @SerializedName("lesson_header")
    protected String mHeader;

    @SerializedName("lesson_subheader")
    protected String mSubHeader;

    @SerializedName("lesson_title")
    protected String mTitle;

    @SerializedName("lesson_key_learnings")
    protected String mKeyLearnigns;

    @SerializedName("lesson_description")
    protected String mDescription;

    @SerializedName("lesson_transcript_title")
    protected String mTranscriptTitle;

    @SerializedName("lesson_transcript_text")
    protected String mTranscriptText;

    public int getLessonId() {
        return mLessonId;
    }

    public float getCuePoint() {
        return mCuePoint;
    }

    public String getHeader() {
        return mHeader;
    }

    public String getSubHeader() {
        return mSubHeader;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getKeyLearnings() {
        return mKeyLearnigns;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getTranscriptTitle() {
        return mTranscriptTitle;
    }

    public String getTranscriptText() {
        return mTranscriptText;
    }
}
