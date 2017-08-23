package com.google.android.apps.miyagi.development.ui.audio.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import com.google.android.apps.miyagi.development.R;
import com.google.android.apps.miyagi.development.data.models.audio.AudioLesson;
import com.google.android.apps.miyagi.development.helpers.ViewUtils;
import com.google.android.apps.miyagi.development.ui.audio.player.AudioPlayerActivity;
import com.google.android.apps.miyagi.development.ui.audio.player.AudioPlayerTabletActivity;
import com.google.android.apps.miyagi.development.utils.SubscriptionHelper;

import org.parceler.Parcels;
import rx.Observable;
import rx.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lukaszweglinski on 18.01.2017.
 */

public class AudioPlaybackService extends Service implements IPlayback, IPlayback.Callback {

    public static final String ACTION_STOP_SERVICE = "com.google.android.apps.miyagi.development.ui.audio.service.ACTION.STOP_SERVICE";
    private static final String ACTION_PLAY_TOGGLE = "com.google.android.apps.miyagi.development.ui.audio.service.ACTION.PLAY_TOGGLE";
    private static final String ACTION_FORWARD = "com.google.android.apps.miyagi.development.ui.audio.service.ACTION.ACTION_FORWARD";
    private static final String ACTION_BACKWARD = "com.google.android.apps.miyagi.development.ui.audio.service.ACTION.ACTION_BACKWARD";
    private static final int NOTIFICATION_ID = 5564;
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    private final Binder mBinder = new LocalBinder();
    private Player mPlayer;
    private RemoteViews mContentViewSmall;
    private RemoteViews mContentViewBig;
    private int mCurrentLessonIndex;
    private String mTopicTitle;
    private List<AudioLesson> mLessons;
    private Subscription mPlayerTimerSubscription;
    private String mCurrentFileUrl;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mLessons = Parcels.unwrap(intent.getParcelableExtra(ArgKey.AUDIO_LESSONS));
        mTopicTitle = intent.getStringExtra(ArgKey.TOPIC_TITLE);

        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = new Player(this);
        mPlayer.addCallback(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_PLAY_TOGGLE:
                    if (mPlayer.getState() == PlaybackStateCompat.STATE_STOPPED) {
                        mPlayer.prepare(mCurrentFileUrl, true);
                    } else {
                        togglePlayPause();
                    }
                    showNotification();
                    break;
                case ACTION_BACKWARD:
                    backward();
                    break;
                case ACTION_FORWARD:
                    forward();
                    break;
                case ACTION_STOP_SERVICE:
                    mPlayer.stop(true);
                    mPlayer.removeCallback();
                    stopForeground(true);
                    stopSelf();
                    break;
                case Intent.ACTION_MEDIA_BUTTON:
                    MediaButtonReceiver.handleIntent(mPlayer.getMediaSession(), intent);
                    mPlayer.togglePlayPause();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopObservablePlayerChanges();
        stopForeground(true);
        mPlayer.removeCallback();
        mPlayer.stop(false);
    }

    @Override
    public void prepare(String file, boolean playAfterPrepare) {
        mCurrentFileUrl = file;
        mPlayer.prepare(file, playAfterPrepare);
    }

    @Override
    public void play() {
        mPlayer.play();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void togglePlayPause() {
        mPlayer.togglePlayPause();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getCurrentStreamPosition() {
        return mPlayer.getCurrentStreamPosition();
    }

    @Override
    public int getCurrentStreamDuration() {
        return mPlayer.getCurrentStreamDuration();
    }

    @Override
    public void updateLastKnownStreamPosition() {
        mPlayer.updateLastKnownStreamPosition();
    }

    @Override
    public void seekTo(int progress) {
        mPlayer.seekTo(progress);
    }

    @Override
    public void forward() {
        mPlayer.forward();
    }

    @Override
    public void backward() {
        mPlayer.backward();
    }

    @Override
    public void addCallback(IPlayback.Callback callback) {
        mPlayer.addCallback(callback);
    }

    @Override
    public void removeCallback() {
        mPlayer.removeCallback();
    }

    @Override
    public int getState() {
        return mPlayer.getState();
    }

    @Override
    public MediaSessionCompat getMediaSession() {
        return mPlayer.getMediaSession();
    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            startObservablePlayerChanges();
        } else {
            stopObservablePlayerChanges();
        }

        if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
            showNotification();
        }
    }

    @Override
    public void onComplete() {
        showNotification();
    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onError() {

    }

    private void startObservablePlayerChanges() {
        SubscriptionHelper.unsubscribe(mPlayerTimerSubscription);
        mPlayerTimerSubscription = Observable.interval(UPDATE_PROGRESS_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    int currentLessonIndex = 0;
                    float currentTime = getCurrentStreamPosition();
                    for (int i = 0; i < mLessons.size(); i++) {
                        float cuePoint = (mLessons.get(i).getCuePoint()) * 1000;

                        if (currentTime >= cuePoint) {
                            currentLessonIndex = i;
                        }
                    }

                    if (mCurrentLessonIndex != currentLessonIndex) {
                        mCurrentLessonIndex = currentLessonIndex;
                        showNotification();
                    }
                });
    }

    private void stopObservablePlayerChanges() {
        SubscriptionHelper.unsubscribe(mPlayerTimerSubscription);
        mPlayerTimerSubscription = null;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent;
        if (ViewUtils.isTablet(this)) {
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AudioPlayerTabletActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, AudioPlayerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_noti_small_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setShowWhen(false)
                .setContentIntent(contentIntent)
                .setCustomContentView(getSmallContentView())
                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification);
    }

    // Notification

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private RemoteViews getBigContentView() {
        if (mContentViewBig == null) {
            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
            setUpRemoteView(mContentViewBig);
        }
        updateRemoteViews(mContentViewBig);
        return mContentViewBig;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setOnClickPendingIntent(R.id.button_backward, getPendingIntent(ACTION_BACKWARD));
        remoteView.setOnClickPendingIntent(R.id.button_forward, getPendingIntent(ACTION_FORWARD));
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_TOGGLE));
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        AudioLesson currentLesson = mLessons.get(mCurrentLessonIndex);
        if (currentLesson != null) {
            remoteView.setTextViewText(R.id.text_view_name, mTopicTitle);
            remoteView.setTextViewText(R.id.text_view_artist, currentLesson.getTitle());
        }

        if (isPlaying()) {
            remoteView.setImageViewResource(R.id.image_view_play_toggle, R.drawable.ic_pause_notification);
        } else {
            remoteView.setImageViewResource(R.id.image_view_play_toggle, R.drawable.ic_play_notification);
        }
    }

    // PendingIntent
    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getService(this, 1, new Intent(action), 0);
    }

    public interface ArgKey {
        String TOPIC_TITLE = "TOPIC_TITLE";
        String AUDIO_LESSONS = "AUDIO_LESSONS";
    }

    public class LocalBinder extends Binder {
        public AudioPlaybackService getService() {
            return AudioPlaybackService.this;
        }
    }
}