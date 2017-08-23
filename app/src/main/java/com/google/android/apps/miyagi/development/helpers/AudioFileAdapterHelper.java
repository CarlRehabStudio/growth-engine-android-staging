package com.google.android.apps.miyagi.development.helpers;

import com.google.android.apps.miyagi.development.GoogleApplication;
import com.google.android.apps.miyagi.development.data.error.AudioDownloadException;
import com.google.android.apps.miyagi.development.ui.audio.service.AudioDownloadService;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadCallback;
import com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DELETE;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DELETED;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DOWNLOAD;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DOWNLOADED;
import static com.google.android.apps.miyagi.development.ui.dashboard.adapter.AudioDownloadStatus.DOWNLOADING;

import javax.inject.Inject;

/**
 * Created by lukaszweglinski on 02.02.2017.
 */

public class AudioFileAdapterHelper {

    private final AudioDownloadStatus mInitialStatus;
    private final AudioDownloadCallback mAudioDownloadCallback;
    private final int mTopicId;
    private final int mPosition;
    @Inject AudioDownloadService mAudioDownloadService;

    /**
     * Constructs AudioFileAdapterHelper.
     */
    public AudioFileAdapterHelper(AudioDownloadStatus initialStatus, AudioDownloadCallback audioDownloadCallback, int topicId, int position) {
        GoogleApplication.getInstance().getAppComponent().inject(this);
        mInitialStatus = initialStatus;
        mAudioDownloadCallback = audioDownloadCallback;
        mTopicId = topicId;
        mPosition = position;
    }

    /**
     * Handle file download action.
     *
     * @return the subscription
     */
    public Subscription handleFileAction() {
        return Observable.just(mInitialStatus)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(audioDownloadStatus -> {
                    Observable<Boolean> actionObservable = Observable.empty();

                    if (audioDownloadStatus == DOWNLOAD) {
                        audioDownloadStatus = DOWNLOADING;
                        mAudioDownloadCallback.onStatusChanged(audioDownloadStatus, mTopicId, mPosition);
                        actionObservable = mAudioDownloadService.downloadAudio(mTopicId);
                    } else if (audioDownloadStatus == AudioDownloadStatus.DELETE) {
                        actionObservable = mAudioDownloadService.deleteAudio(mTopicId);
                    }
                    return actionObservable;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (mInitialStatus == DOWNLOAD) {
                            mAudioDownloadCallback.onStatusChanged(DOWNLOADED, mTopicId, mPosition);
                        } else if (mInitialStatus == DELETE) {
                            mAudioDownloadCallback.onStatusChanged(DELETED, mTopicId, mPosition);
                        }
                    } else {
                        onError(new AudioDownloadException());
                    }
                }, this::onError);
    }

    private void onError(Throwable t) {
        mAudioDownloadCallback.onStatusChanged(DOWNLOAD, mTopicId, mPosition);
        mAudioDownloadCallback.onDownloadError(t, mTopicId, mPosition);
    }
}
