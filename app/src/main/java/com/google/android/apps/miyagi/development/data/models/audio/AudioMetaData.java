package com.google.android.apps.miyagi.development.data.models.audio;

import com.google.android.apps.miyagi.development.data.net.responses.audio.AudioResponseData;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import org.parceler.Parcel;

/**
 * Created by jerzyw on 22.12.2016.
 */

@Parcel
public class AudioMetaData extends RealmObject {

    @PrimaryKey
    protected int mTopicId;
    protected AudioResponseData mAudioResponseData;
    protected String mAudioFilePath;

    public AudioMetaData() {
    }

    public void setAudioResponseData(AudioResponseData audioResponseData) {
        mAudioResponseData = audioResponseData;
    }

    public AudioResponseData getAudioResponseData() {
        return mAudioResponseData;
    }

    public void setAudioFilePath(String audioFilePath) {
        mAudioFilePath = audioFilePath;
    }

    public String getAudioFilePath() {
        return mAudioFilePath;
    }

    public int getTopicId() {
        return mTopicId;
    }

    public AudioMetaData setTopicId(int topicId) {
        mTopicId = topicId;
        return this;
    }
}
