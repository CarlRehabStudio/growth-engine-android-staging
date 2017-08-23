package com.google.android.apps.miyagi.development.data.net.responses.diagnostics;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukaszweglinski on 22.12.2016.
 */

class DiagnosticsSkipResponseData {

    @SerializedName("first_lesson_id")
    protected int mFirstLessonId;

    public int getFirstLessonId() {
        return mFirstLessonId;
    }
}
