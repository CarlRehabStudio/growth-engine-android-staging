package com.google.android.apps.miyagi.development.ui.offline.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.google.android.apps.miyagi.development.data.models.audio.AudioMetaData;
import com.google.android.apps.miyagi.development.helpers.AudioFileAdapterHelper;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.FileActionCallback;
import com.google.android.apps.miyagi.development.ui.offline.OfflineAdapterAction;
import com.google.android.apps.miyagi.development.ui.offline.adapter.item.OfflineListTopicItem;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import rx.subscriptions.CompositeSubscription;

import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DELETED;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 07.02.2017.
 */

public class OfflineDashboardAdapter extends FlexibleAdapter<OfflineListTopicItem> implements OfflineAdapterAction, OfflineAdapterAction.Callback, FileActionCallback, AudioDownloadCallback {

    private OfflineAdapterAction.Callback mOfflineAdapterCallback;
    private CompositeSubscription mFileSubscription;

    private OfflineDashboardAdapter(@Nullable List<OfflineListTopicItem> items) {
        super(items);
        mFileSubscription = new CompositeSubscription();
    }

    /**
     * Creates adapter for Offline Dashboard.
     */
    public static OfflineDashboardAdapter create(List<AudioMetaData> audioMetaDatas, int mainCtaColor) {

        List<OfflineListTopicItem> item = new ArrayList<>();

        for (AudioMetaData data : audioMetaDatas) {
            item.add(new OfflineListTopicItem(data.getAudioResponseData(), mainCtaColor));
        }
        return new OfflineDashboardAdapter(item);
    }

    @Override
    public void setOfflineAdapterCallback(OfflineAdapterAction.Callback offlineAdapterCallback) {
        mOfflineAdapterCallback = offlineAdapterCallback;
    }

    @Override
    public void onTopicClick(int topicId) {
        mOfflineAdapterCallback.onTopicClick(topicId);
    }

    @Override
    public void onFileActionClick(int position, int topicId) {
        mFileSubscription.add(
                new AudioFileAdapterHelper(AudioDownloadStatus.DELETE, this, topicId, position).handleFileAction());
    }

    @Override
    public void onMenuClick(View view, int topicId, int position) {
        mOfflineAdapterCallback.onMenuClick(view, topicId, position);
    }

    @Override
    public void onAudioDownloadStatusChange(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        mOfflineAdapterCallback.onAudioDownloadStatusChange(audioDownloadStatus, topicId, position);
    }

    @Override
    public void onDownloadError(Throwable t, int topicId, int position) {
    }

    @Override
    public void onStatusChanged(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        if (audioDownloadStatus == DELETED) {
            removeItem(position);
        }
        mOfflineAdapterCallback.onAudioDownloadStatusChange(audioDownloadStatus, topicId, position);
    }

    public void onStop() {
        SubscriptionHelper.unsubscribe(mFileSubscription);
        mFileSubscription = null;
    }
}
