package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lukasz on 27.12.2016.
 */

public class Quiz {

    @SerializedName("id")
    protected int mId;

    @SerializedName("questions")
    protected List<String> mQuestions;

    public int getId() {
        return mId;
    }

    public List<String> getQuestions() {
        return mQuestions;
    }
}
