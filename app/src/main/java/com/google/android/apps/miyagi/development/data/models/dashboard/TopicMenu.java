package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lukasz on 27.12.2016.
 */

public class TopicMenu {

    @SerializedName("play_audio")
    private String mPlayAudio;

    @SerializedName("play_video")
    private String mPlayVideo;

    @SerializedName("download_audio")
    private String mDownloadAudio;

    @SerializedName("delete_audio")
    private String mDeleteAudio;

    public String getPlayAudio() {
        return mPlayAudio;
    }

    public String getPlayVideo() {
        return mPlayVideo;
    }

    public String getDownloadAudio() {
        return mDownloadAudio;
    }

    public String getDeleteAudio() {
        return mDeleteAudio;
    }
}
