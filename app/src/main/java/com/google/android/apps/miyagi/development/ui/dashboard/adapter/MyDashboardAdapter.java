package com.google.android.apps.miyagi.development.ui.dashboard.adapter;

import android.util.SparseArray;
import android.view.View;

import com.google.android.apps.miyagi.development.data.models.dashboard.TopicGroup;
import com.google.android.apps.miyagi.development.data.models.dashboard.Topics;
import com.google.android.apps.miyagi.development.data.models.dashboard.UpNextAction;
import com.google.android.apps.miyagi.development.helpers.AudioFileAdapterHelper;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.FileActionCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.common.DashboardAdapterCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.common.MenuClickCallback;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.AUDIO_STATUS_PAYLOAD;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DELETE;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DELETED;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DOWNLOAD;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DOWNLOADED;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyDashboardAdapter extends FlexibleAdapter<AbstractFlexibleItem> implements DashboardAdapterCallback, FileActionCallback, AudioDownloadCallback, MenuClickCallback {

    private SparseArray<AudioDownloadStatus> mFileAudioStatus;

    private DashboardAdapterCallback mDashboardAdapterCallback;
    private MenuClickCallback mMenuClickAdapterCallback;

    private CompositeSubscription mFileSubscription;

    /**
     * Instantiates a new My dashboard adapter.
     *
     * @param items                dashboard list items.
     * @param downloadedTopicAudio downloaded topics audio.
     * @param topics               topic group.
     */
    public MyDashboardAdapter(List<AbstractFlexibleItem> items, List<Integer> downloadedTopicAudio, TopicGroup topics) {
        super(new ArrayList<>(items), null, true);
        mFileAudioStatus = new SparseArray<>();
        mFileSubscription = new CompositeSubscription();

        List<Topics> topicsItems = new ArrayList<>();
        topicsItems.addAll(topics.getPlanTopic().getTopics());
        topicsItems.addAll(topics.getProgressTopic().getTopics());
        topicsItems.addAll(topics.getOutOfPlanTopic().getTopics());
        topicsItems.addAll(topics.getCompletedTopic().getTopics());

        for (Topics item : topicsItems) {
            mFileAudioStatus.put(item.getId(), DOWNLOAD);
            if (downloadedTopicAudio != null) {
                Iterator<Integer> downloadedTopicsAudioIterator = downloadedTopicAudio.iterator();
                while (downloadedTopicsAudioIterator.hasNext()) {
                    Integer downloadedTopicId = downloadedTopicsAudioIterator.next();
                    if (downloadedTopicId == item.getId()) {
                        mFileAudioStatus.put(item.getId(), AudioDownloadStatus.DELETE);
                        downloadedTopicsAudioIterator.remove();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStartButtonClick(UpNextAction upNextAction, String buttonText) {
        mDashboardAdapterCallback.onStartButtonClick(upNextAction, buttonText);
    }

    @Override
    public void onTopicClick(Topics topic) {
        mDashboardAdapterCallback.onTopicClick(topic);
    }

    @Override
    public void onTopicLinkClick(Topics topic) {
        mDashboardAdapterCallback.onTopicLinkClick(topic);
    }

    @Override
    public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        mDashboardAdapterCallback.onAudioDownloadStatusChange(audioDownloadStatus, topicId, position);
    }

    public void setDashboardAdapterCallback(DashboardAdapterCallback dashboardAdapterCallback) {
        mDashboardAdapterCallback = dashboardAdapterCallback;
    }

    public void setMenuClickAdapterCallback(MenuClickCallback menuClickAdapterCallback) {
        mMenuClickAdapterCallback = menuClickAdapterCallback;
    }

    public AudioDownloadStatus getAudioStatusForId(int topicId) {
        return mFileAudioStatus.get(topicId);
    }

    @Override
    public void onFileActionClick(int position, int topicId) {
        AudioDownloadStatus status = getAudioStatusForId(topicId);
        mFileSubscription.add(
                new AudioFileAdapterHelper(status, this, topicId, position).handleFileAction());
    }

    @Override
    public void onMenuClick(View view, Topics topic, int position, AudioDownloadStatus audioDownloadStatus) {
        mMenuClickAdapterCallback.onMenuClick(view, topic, position, audioDownloadStatus);
    }

    @Override
    public void onStatusChanged(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        if (audioDownloadStatus == DELETED) {
            mFileAudioStatus.put(topicId, DOWNLOAD);
        } else if (audioDownloadStatus == DOWNLOADED) {
            mFileAudioStatus.put(topicId, DELETE);
        } else {
            mFileAudioStatus.put(topicId, audioDownloadStatus);
        }
        notifyItemChanged(position, AUDIO_STATUS_PAYLOAD);
        mDashboardAdapterCallback.onAudioDownloadStatusChange(audioDownloadStatus, topicId, position);
    }

    @Override
    public void onDownloadError(Throwable t, int topicId, int position) {
        mDashboardAdapterCallback.onAudioDownloadError(t, topicId, position);
    }

    public void onStart() {
        mFileSubscription = new CompositeSubscription();
    }

    public void onStop() {
        SubscriptionHelper.unsubscribe(mFileSubscription);
        mFileSubscription = null;
    }

    @Override
    public void onAudioDownloadError(Throwable t, int topicId, int position) {

    }
}