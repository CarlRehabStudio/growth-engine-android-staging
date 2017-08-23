package com.google.android.apps.miyagi.development.ui.dashboard.common;

import android.view.View;
import com.google.android.apps.miyagi.development.data.models.dashboard.Topics;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by lukaszweglinski on 07.02.2017.
 */

public interface MenuClickCallback {

    MenuClickCallback EMPTY = new MenuClickCallback() {
        @Override
        public void onMenuClick(View view, Topics topic, int position, AudioDownloadStatus audioDownloadStatus) {
            Lh.e(this, "Callback is not attached");
        }

    };

    void onMenuClick(View view, Topics topic, int position, AudioDownloadStatus audioDownloadStatus);
}
