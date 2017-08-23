package com.google.android.apps.miyagi.development.ui.offline;

import android.view.View;

import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by lukaszweglinski on 07.02.2017.
 */

public interface OfflineAdapterAction {

    void setOfflineAdapterCallback(Callback callback);

    interface Callback {
        Callback EMPTY = new Callback() {

            @Override
            public void onTopicClick(int topicId) {
                Lh.e(this, "Callback is not attached");
            }

            @Override
            public void onMenuClick(View view, int topicId, int position) {
                Lh.e(this, "Callback is not attached");
            }

            @Override
            public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
                Lh.e(this, "DashboardAdapterCallback is not attached to Activity. Can't change Activity state.");
            }
        };

        void onTopicClick(int topicId);

        void onMenuClick(View view, int topicId, int position);

        void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position);
    }
}
