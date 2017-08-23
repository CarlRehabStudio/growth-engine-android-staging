package com.google.android.apps.miyagi.development.ui.audio.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.google.android.apps.miyagi.development.utils.Lh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukaszweglinski on 24.01.2017.
 */

public class Player implements
        IPlayback,
        AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener {

    //MediaSessionCompact TAG
    private static final String SESSION_TAG = "SESSION_TAG";
    // Default value for seek audio
    public static final int SEEK_TIME = 15 * 1000;
    // The volume we set the media player to when we lose audio focus, but are
    // allowed to reduce the volume instead of stopping playback.
    private static final float VOLUME_DUCK = 0.2f;
    // The volume we set the media player when we have audio focus.
    private static final float VOLUME_NORMAL = 1.0f;
    // we don't have audio focus, and can't duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    // we don't have focus, but can duck (play at a low volume)
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    // we have full audio focus
    private static final int AUDIO_FOCUSED = 2;

    private final Context mContext;
    private final AudioManager mAudioManager;
    private final WifiManager.WifiLock mWifiLock;
    private MediaPlayer mMediaPlayer;
    private List<Callback> mCallback;

    private int mState;
    private boolean mPlayOnFocusGain;
    private volatile boolean mAudioNoisyReceiverRegistered;
    // Type of audio focus we have:
    private int mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private volatile int mCurrentPosition;
    private volatile int mCurrentDuration = 0;
    private volatile String mCurrentFile;
    private boolean mPlayAfterPrepare;
    private MediaSessionCompat mMediaSession;
    private final BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (isPlaying()) {
                    pause();
                }
            }
        }
    };

    /**
     * Instantiates a new Player.
     *
     * @param context the context.
     */
    public Player(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mWifiLock = ((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "miyagi_lock");
        mCallback = new ArrayList<>();

        mMediaSession = new MediaSessionCompat(context, SESSION_TAG);
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP && !keyEvent.isLongPress()) {
                        togglePlayPause();
                    }
                    return true;
                } else {
                    return super.onMediaButtonEvent(mediaButtonEvent);
                }
            }
        });

        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(mContext, MediaButtonReceiver.class);
        PendingIntent mbrIntent = PendingIntent.getBroadcast(mContext, 0, mediaButtonIntent, 0);
        mMediaSession.setMediaButtonReceiver(mbrIntent);
        mMediaSession.setActive(true);
    }

    @Override
    public void prepare(String file, boolean playAfterPrepare) {
        mPlayAfterPrepare = playAfterPrepare;
        boolean mediaHasChanged = !TextUtils.equals(mCurrentFile, file);
        if (mediaHasChanged) {
            // if song is ended we play from start position
            if (mState == PlaybackStateCompat.STATE_STOPPED && mCurrentPosition == mCurrentDuration) {
                mCurrentPosition = 0;
            }
            mCurrentFile = file;
            try {
                createMediaPlayerIfNeeded();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mState = PlaybackStateCompat.STATE_CONNECTING;

                mMediaPlayer.setDataSource(mCurrentFile);
                mMediaPlayer.prepareAsync();

                if (isStreaming(mCurrentFile)) {
                    mWifiLock.acquire();
                }

                notifyOnPlaybackStatusChanged(mState);
            } catch (IOException ex) {
                //TODO handle error file not exist or something else
                Lh.e(this, ex.getLocalizedMessage());
            }
        }
    }

    private void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while
            // playing. If we don't do that, the CPU might go to sleep while the
            // song is playing, causing playback to stop.
            mMediaPlayer.setWakeMode(mContext.getApplicationContext(),
                    PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing,
            // and when it's done playing:
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnSeekCompleteListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnInfoListener((mp, what, extra) -> {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        notifyOnPlaybackStatusChanged(PlaybackStateCompat.STATE_BUFFERING);
                        return true;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        notifyOnPlaybackStatusChanged(mState);
                        return true;
                    default:
                        return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
    }

    private void configMediaPlayerState() {
        if (mAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // If we don't have audio focus and can't duck, we have to pause,
            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                pause();
            }
        } else {  // we have audio focus:
            registerAudioNoisyReceiver();
            if (mAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                mMediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK); // we'll be relatively quiet
            } else {
                if (mMediaPlayer != null) {
                    mMediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL); // we can be loud again
                } // else do something for remote client.
            }
            // If we were playing when we lost focus, we need to resume playing.
            if (mPlayOnFocusGain) {
                if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                    if (mCurrentPosition == mMediaPlayer.getCurrentPosition()) {
                        mMediaPlayer.start();
                        mState = PlaybackStateCompat.STATE_PLAYING;
                    } else {
                        mMediaPlayer.seekTo(mCurrentPosition);
                        mState = PlaybackStateCompat.STATE_BUFFERING;
                    }
                }
                mPlayOnFocusGain = false;
            }
        }
        notifyOnPlaybackStatusChanged(mState);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public int getCurrentStreamPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return mCurrentPosition;
        }
    }

    @Override
    public void updateLastKnownStreamPosition() {
        if (mMediaPlayer != null) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
        }
    }

    public int getCurrentStreamDuration() {
        return mCurrentDuration;
    }

    @Override
    public void play() {
        mPlayOnFocusGain = true;
        tryToGetAudioFocus();
        registerAudioNoisyReceiver();

        if (mMediaPlayer != null) {
            configMediaPlayerState();
        }

        notifyOnPlaybackStatusChanged(mState);
    }

    @Override
    public void pause() {
        if (mState == PlaybackStateCompat.STATE_PLAYING) {
            // Pause media player and cancel the 'foreground service' state.
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentPosition = mMediaPlayer.getCurrentPosition();
            }
            // while paused, retain the MediaPlayer but give up audio focus
            releaseResources(false);
        }
        mState = PlaybackStateCompat.STATE_PAUSED;
        notifyOnPlaybackStatusChanged(mState);
        unregisterAudioNoisyReceiver();
    }

    @Override
    public void togglePlayPause() {
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    @Override
    public void forward() {
        // get current song position
        int currentPosition = getCurrentStreamPosition();
        // check if seekForward time is lesser than song duration
        if (currentPosition + SEEK_TIME <= getCurrentStreamDuration()) {
            // forward song
            int newPosition = currentPosition + SEEK_TIME;
            if (mMediaPlayer != null) {
                seekTo(newPosition);
            }
        } else {
            // forward to end position
            int newPosition = getCurrentStreamDuration();
            if (mMediaPlayer != null) {
                seekTo(newPosition);
            }
        }
    }

    @Override
    public void backward() {
        // get current song position
        int currentPosition = getCurrentStreamPosition();
        // check if seekBackward time is greater than 0
        if (currentPosition - SEEK_TIME >= 0) {
            // backward song
            int newPosition = currentPosition - SEEK_TIME;
            if (mMediaPlayer != null) {
                seekTo(newPosition);
            }
        } else {
            // backward to start position
            int newPosition = 0;
            if (mMediaPlayer != null) {
                seekTo(newPosition);
            }
        }
    }

    @Override
    public void seekTo(int position) {
        mCurrentPosition = position;
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mState = PlaybackStateCompat.STATE_BUFFERING;
            } else {
                mState = PlaybackStateCompat.STATE_CONNECTING;
            }
            registerAudioNoisyReceiver();
            mMediaPlayer.seekTo(position);
            notifyOnPlaybackStatusChanged(mState);
        }
    }

    void stop(boolean notifyListeners) {
        mState = PlaybackStateCompat.STATE_STOPPED;
        if (notifyListeners) {
            notifyOnPlaybackStatusChanged(mState);
        }
        mCurrentFile = null;
        // Give up Audio focus
        giveUpAudioFocus();
        unregisterAudioNoisyReceiver();
        // Relax all resources
        releaseResources(true);
    }

    @Override
    public void addCallback(Callback callback) {
        mCallback.add(callback);
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = PlaybackStateCompat.STATE_PAUSED;

        if (mPlayAfterPrepare) {
            mMediaPlayer.seekTo(mCurrentPosition);
            play();
            mPlayAfterPrepare = false;
        }

        // The media player is done preparing. That means we can start playing if we
        // have audio focus.
        tryToGetAudioFocus();
        configMediaPlayerState();

        mCurrentDuration = mMediaPlayer.getDuration();
        notifyOnPrepared();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mCurrentPosition = mp.getCurrentPosition();

        if (mState == PlaybackStateCompat.STATE_BUFFERING) {
            registerAudioNoisyReceiver();
            mMediaPlayer.start();
            mState = PlaybackStateCompat.STATE_PLAYING;
        } else if (mState == PlaybackStateCompat.STATE_CONNECTING) {
            if (mp.isPlaying()) {
                mState = PlaybackStateCompat.STATE_PLAYING;
            } else {
                mState = PlaybackStateCompat.STATE_PAUSED;
            }
        }
        notifyOnPlaybackStatusChanged(mState);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // The media player finished playing the current song, so we go ahead
        // and start the next.
        stop(true);
        mCurrentPosition = getCurrentStreamDuration();
        notifyOnCompleted();
    }

    /**
     * Try to get the system audio focus.
     */
    private void tryToGetAudioFocus() {
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AUDIO_FOCUSED;
        } else {
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    /**
     * Give up the audio focus.
     */
    private void giveUpAudioFocus() {
        if (mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // We have gained focus:
            mAudioFocus = AUDIO_FOCUSED;
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // We have lost focus. If we can duck (low playback volume), we can keep playing.
            // Otherwise, we need to pause the playback.
            boolean canDuck = focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
            mAudioFocus = canDuck ? AUDIO_NO_FOCUS_CAN_DUCK : AUDIO_NO_FOCUS_NO_DUCK;

            // If we are playing, we need to reset media player by calling configMediaPlayerState
            // with mAudioFocus properly set.
            if (mState == PlaybackStateCompat.STATE_PLAYING && !canDuck) {
                // If we don't have audio focus and can't duck, we save the information that
                // we were playing, so that we can resume playback once we get the focus back.
                mPlayOnFocusGain = true;
            }
        }
        configMediaPlayerState();
    }

    private void registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            IntentFilter audioNoisyIntentFilter =
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            mContext.registerReceiver(mAudioNoisyReceiver, audioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mContext.unregisterReceiver(mAudioNoisyReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    private void releaseResources(boolean releaseMediaPlayer) {
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }

        mMediaSession.release();
    }

    private boolean isStreaming(String file) {
        return file.startsWith("https://");
    }

    private void notifyOnPrepared() {
        if (mCallback != null) {
            for (Callback callback : mCallback) {
                callback.onPrepared();
            }
        }
    }

    private void notifyOnPlaybackStatusChanged(int state) {
        if (mCallback != null) {
            for (Callback callback : mCallback) {
                callback.onPlaybackStatusChanged(state);
            }
        }
    }

    private void notifyOnCompleted() {
        if (mCallback != null) {
            for (Callback callback : mCallback) {
                callback.onComplete();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mCallback != null) {
            for (Callback callback : mCallback) {
                callback.onError();
            }
        }
        return true;
    }
}
