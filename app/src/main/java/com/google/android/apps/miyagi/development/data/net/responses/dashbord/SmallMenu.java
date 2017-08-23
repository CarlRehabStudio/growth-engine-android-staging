package com.google.android.apps.miyagi.development.data.net.responses.dashbord;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jerzyw on 07.11.2016.
 */

public class SmallMenu {

    @SerializedName("play_video")
    private String mPlayVideo;

    @SerializedName("play_audio")
    private String mPlayAudio;

    @SerializedName("delete_audio")
    private String mDeleteAudio;

    @SerializedName("download_audio_file")
    private String mDownloadAudioFile;

    public String getPlayVideo() {
        return mPlayVideo;
    }

    public String getPlayAudio() {
        return mPlayAudio;
    }

    public String getDeleteAudio() {
        return mDeleteAudio;
    }

    public String getDownloadAudioFile() {
        return mDownloadAudioFile;
    }
}
