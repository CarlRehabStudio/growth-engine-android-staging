package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lukasz on 27.12.2016.
 */

public class Lesson {

    @SerializedName("id")
    protected int mId;

    @SerializedName("completed")
    protected boolean mCompleted;

    public int getId() {
        return mId;
    }

    public boolean isCompleted() {
        return mCompleted;
    }
}
