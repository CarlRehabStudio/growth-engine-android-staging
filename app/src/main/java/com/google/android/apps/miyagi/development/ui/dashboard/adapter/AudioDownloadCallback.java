package com.google.android.apps.miyagi.development.ui.dashboard.adapter;

/**
 * Created by lukaszweglinski on 02.02.2017.
 */

public interface AudioDownloadCallback {

    void onStatusChanged(AudioDownloadStatus audioDownloadStatus, int topicId, int position);

    void onDownloadError(Throwable t, int topicId, int position);
}
