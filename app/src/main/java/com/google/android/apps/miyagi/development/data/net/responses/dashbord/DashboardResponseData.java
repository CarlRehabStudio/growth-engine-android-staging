package com.google.android.apps.miyagi.development.data.net.responses.dashbord;

import com.google.android.apps.miyagi.development.data.models.dashboard.Certification;
import com.google.android.apps.miyagi.development.data.models.dashboard.TopicGroup;
import com.google.android.apps.miyagi.development.data.models.dashboard.TopicMenu;
import com.google.android.apps.miyagi.development.data.models.dashboard.UpNext;
import com.google.android.apps.miyagi.development.data.models.statistics.Statistics;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lukasz on 14.10.2016.
 */

public class DashboardResponseData {

    @SerializedName("topics")
    protected TopicGroup mTopics;

    @SerializedName("topic_menu")
    protected TopicMenu mTopicMenu;

    @SerializedName("statistics")
    protected Statistics mStatictics;

    @SerializedName("up_next")
    protected UpNext mUpNext;

    @SerializedName("certification")
    protected Certification mCertification;

    @SerializedName("title")
    protected String mTitle;

    public TopicGroup getTopics() {
        return mTopics;
    }

    public TopicMenu getTopicMenu() {
        return mTopicMenu;
    }

    public Statistics getStatictics() {
        return mStatictics;
    }

    public UpNext getUpNext() {
        return mUpNext;
    }

    public Certification getCertification() {
        return mCertification;
    }

    public String getTitle() {
        return mTitle;
    }
}