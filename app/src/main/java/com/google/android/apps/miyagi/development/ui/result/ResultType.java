package com.google.android.apps.miyagi.development.ui.result;

import org.parceler.Parcel;

/**
 * Created by marcin on 15.01.2017.
 */

@Parcel
public enum ResultType {
    RIGHT, ALMOST, WRONG, TOPIC_COMPLETED, LESSONS_COMPLETED;

    public static final String KEY = "RESULT_TYPE";
}
