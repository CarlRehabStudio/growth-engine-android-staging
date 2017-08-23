package com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.topic;

import com.google.android.apps.miyagi.development.data.models.dashboard.TopicGroup;
import com.google.android.apps.miyagi.development.data.models.dashboard.Topics;
import com.google.android.apps.miyagi.development.data.models.dashboard.TopicsContainer;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 28.10.2016.
 */

public class TopicGroupToFlexibleItemMapper {

    private final int mSectionBackgroundColor;

    public TopicGroupToFlexibleItemMapper(int sectionBackgroundColor) {
        mSectionBackgroundColor = sectionBackgroundColor;
    }

    /**
     * Creates topic groups to display in dashboard.
     */
    public Observable<List<AbstractFlexibleItem>> getTopicListItem(TopicGroup topicGroups) {
        TopicsContainer progressTopic = topicGroups.getProgressTopic();
        TopicsContainer planTopic = topicGroups.getPlanTopic();
        TopicsContainer completedTopic = topicGroups.getCompletedTopic();
        TopicsContainer outOfPlanTopic = topicGroups.getOutOfPlanTopic();

        List<TopicsContainer> topics = new ArrayList<>();
        topics.add(progressTopic);
        topics.add(planTopic);

        topics.add(outOfPlanTopic);

        List<AbstractFlexibleItem> items = new ArrayList<>();
        boolean expandIfFirstTopicGroup = true;
        for (int i = 0; i < topics.size(); i++) {
            TopicsContainer topicsContainer = topics.get(i);
            TopicGroupItem renderedTopic = renderTopicGroup(topicsContainer, expandIfFirstTopicGroup, false, mSectionBackgroundColor);
            if (renderedTopic != null) {
                items.add(renderedTopic);
                // first topic group should be always expanded
                expandIfFirstTopicGroup = false;
            }
        }

        TopicGroupItem renderedTopic = renderTopicGroup(completedTopic, expandIfFirstTopicGroup, true, mSectionBackgroundColor);
        if (renderedTopic != null) {
            items.add(renderedTopic);
        }
        return Observable.just(items);
    }

    private TopicGroupItem renderTopicGroup(TopicsContainer topicsContainer, boolean expanded, boolean completed, int sectionBackgroundColor) {
        if (topicsContainer.getTopics().size() > 0) {
            TopicGroupItem groupItem = new TopicGroupItem(topicsContainer.getTitle(), sectionBackgroundColor);
            groupItem.setExpanded(expanded);

            List<Topics> topicsList = topicsContainer.getTopics();
            int index = 0;
            for (Topics topic : topicsList) {
                if (completed) {
                    CompletedTopicItem item = new CompletedTopicItem(topic, index == topicsList.size() - 1);
                    item.setHeader(groupItem);
                    groupItem.addSubItem(item);
                } else {
                    TopicItem item = new TopicItem(topic, index == topicsList.size() - 1);
                    item.setHeader(groupItem);
                    groupItem.addSubItem(item);
                }
                index++;
            }
            return groupItem;
        }
        return null;
    }
}