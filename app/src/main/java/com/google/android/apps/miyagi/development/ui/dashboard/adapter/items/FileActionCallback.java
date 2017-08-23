package com.google.android.apps.miyagi.development.ui.dashboard.adapter.items;

import com.google.android.apps.miyagi.development.utils.Lh;

/**
 * Created by lukaszweglinski on 02.02.2017.
 */

public interface FileActionCallback {

    FileActionCallback EMPTY = new FileActionCallback() {
        @Override
        public void onFileActionClick(int position, int topicId) {
            Lh.e(this, "Callback is not attached");
        }
    };

    void onFileActionClick(int position, int topicId);
}
