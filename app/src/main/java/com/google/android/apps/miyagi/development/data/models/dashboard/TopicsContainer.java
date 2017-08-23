package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lukaszweglinski on 28.12.2016.
 */

public class TopicsContainer {

    @SerializedName("title")
    protected String mTitle;

    @SerializedName("topics")
    protected List<Topics> mTopics;

    public String getTitle() {
        return mTitle;
    }

    public List<Topics> getTopics() {
        return mTopics;
    }
}
