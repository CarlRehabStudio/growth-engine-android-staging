package com.google.android.apps.miyagi.development.ui.audio.player.adapter;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.view.View;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.helpers.AudioFileAdapterHelper;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.items.FileActionCallback;
import com.google.android.apps.miyagi.development.ui.offline.OfflineAdapterAction;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import rx.subscriptions.CompositeSubscription;

import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DELETED;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marcinarciszew on 11.01.2017.
 */

public class AudioPlayerListAdapter
        extends FlexibleAdapter<AbstractFlexibleItem>
        implements AudioPlayerListAdapterCallback,
        OfflineAdapterAction,
        OfflineAdapterAction.Callback,
        FileActionCallback,
        AudioDownloadCallback {

    private AudioPlayerListAdapterCallback mAdapterCallback;
    private boolean mPlaying;
    private OfflineAdapterAction.Callback mOfflineAdapterCallback;
    private CompositeSubscription mFileSubscription;

    public AudioPlayerListAdapter(@Nullable List<AbstractFlexibleItem> items, @Nullable OnItemClickListener listeners) {
        super(new ArrayList<>(items), listeners, true);
        mFileSubscription = new CompositeSubscription();
    }

    public void setAudioPlayerListAdapterCallback(AudioPlayerListAdapterCallback callback) {
        mAdapterCallback = callback;
    }

    @Override
    public void setOfflineAdapterCallback(Callback callback) {
        mOfflineAdapterCallback = callback;
    }

    @Override
    public void onLessonItemClick(AudioLesson audioLesson) {
        mAdapterCallback.onLessonItemClick(audioLesson);
    }

    @Override
    public void onNextLessonChange(int position) {
        mAdapterCallback.onNextLessonChange(position);
    }

    @Override
    public void toggleSelection(@IntRange(from = 0L) int position) {
        super.toggleSelection(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void onPlayStatusChanged(boolean isPlaying) {
        mPlaying = isPlaying;
        notifyItemRangeChanged(0, getItemCount());
    }

    public boolean isPlaying() {
        return mPlaying;
    }

    @Override
    public void onTopicClick(int topicId) {
        mOfflineAdapterCallback.onTopicClick(topicId);
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
    public void onFileActionClick(int position, int topicId) {
        mFileSubscription.add(
                new AudioFileAdapterHelper(AudioDownloadStatus.DELETE, this, topicId, position).handleFileAction());
    }

    @Override
    public void onStatusChanged(AudioDownloadStatus audioDownloadStatus, int topicId, int position) {
        if (audioDownloadStatus == DELETED) {
            mAdapterCallback.onNextLessonChange(position);
        }
    }

    public void onStop() {
        SubscriptionHelper.unsubscribe(mFileSubscription);
        mFileSubscription = null;
    }
}
