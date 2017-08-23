package com.google.android.apps.miyagi.development.ui.dashboard.common;

import com.google.android.apps.miyagi.development.data.models.dashboard.Topics;
import com.google.android.apps.miyagi.development.data.models.dashboard.UpNextAction;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by lukaszweglinski on 17.01.2017.
 */

public interface DashboardAdapterCallback {

    // TODO: never used?
    DashboardAdapterCallback EMPTY = new DashboardAdapterCallback() {

        @Override
        public void onStartButtonClick(UpNextAction upNextAction, String buttonText) {
            Lh.e(this, "DashboardAdapterCallback is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void onTopicClick(Topics topic) {
            Lh.e(this, "DashboardAdapterCallback is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void onTopicLinkClick(Topics topic) {
            Lh.e(this, "DashboardAdapterCallback is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
            Lh.e(this, "DashboardAdapterCallback is not attached to Activity. Can't change Activity state.");
        }

        @Override
        public void onAudioDownloadError(Throwable t, int topicId, int position) {
            Lh.e(this, "DashboardAdapterCallback is not attached to Activity. Can't change Activity state.");
        }
    };

    void onStartButtonClick(UpNextAction upNextAction, String buttonText);

    void onTopicClick(Topics topic);

    void onTopicLinkClick(Topics topic);

    void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position);

    void onAudioDownloadError(Throwable t, int topicId, int position);
}
