package com.google.android.apps.miyagi.development.data.models.dashboard;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukasz on 28.10.2016.
 */

public class TopicGroup {

    @SerializedName("progress")
    protected TopicsContainer mProgressTopic;

    @SerializedName("completed")
    protected TopicsContainer mCompletedTopic;

    @SerializedName("plan")
    protected TopicsContainer mPlanTopic;

    @SerializedName("out_of_plan")
    protected TopicsContainer mOutOfPlanTopic;

    public TopicsContainer getProgressTopic() {
        return mProgressTopic;
    }

    public TopicsContainer getCompletedTopic() {
        return mCompletedTopic;
    }

    public TopicsContainer getPlanTopic() {
        return mPlanTopic;
    }

    public TopicsContainer getOutOfPlanTopic() {
        return mOutOfPlanTopic;
    }
}
