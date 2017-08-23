package com.google.android.apps.miyagi.development.ui.lesson;

/**
 * Created by Lukasz on 22.01.2017.
 */

public enum LessonState {
    NEW(0), WATCHED(1), COMPLETED(2);

    private final int mValue;

    LessonState(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
