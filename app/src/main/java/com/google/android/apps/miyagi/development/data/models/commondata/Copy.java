package com.google.android.apps.miyagi.development.data.models.commondata;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by lukaszweglinski on 13.02.2017.
 */

public class Copy extends RealmObject {

    @SerializedName("offline_header")
    private String mOfflineHeader;

    @SerializedName("audio_header")
    private String mAudioHeader;

    @SerializedName("file_deleting")
    private String mFileDeletingMessage;

    @SerializedName("file_downloading")
    private String mFileDownloadingMessage;

    @SerializedName("first_audio_download")
    private String mFirstAudioDownloadMessage;

    @SerializedName("offline_info")
    private String mOfflineInfo;

    @SerializedName("audio_info")
    private String mAudioInfo;

    public String getOfflineHeader() {
        return mOfflineHeader;
    }

    public String getAudioHeader() {
        return mAudioHeader;
    }

    public String getFileDeletingMessage() {
        return mFileDeletingMessage;
    }

    public String getFileDownloadingMessage() {
        return mFileDownloadingMessage;
    }

    public String getFirstAudioDownloadMessage() {
        return mFirstAudioDownloadMessage;
    }

    public String getOfflineInfo() {
        return mOfflineInfo;
    }

    public String getAudioInfo() {
        return mAudioInfo;
    }
}
