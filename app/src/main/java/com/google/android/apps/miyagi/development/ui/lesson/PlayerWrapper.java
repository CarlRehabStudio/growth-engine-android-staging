package com.google.android.apps.miyagi.development.ui.lesson;

import android.support.annotation.Nullable;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;

/**
 * Created by jerzyw on 24.10.2016.
 */

public class PlayerWrapper {

    //internal listeners
    private final YouTubePlayer.OnInitializedListener mOnInitializationListener = new OnInitializationInternalListener();
    private final YouTubePlayer.PlayerStateChangeListener mOnPlayerStateChangeListener = new OnPlayerStateChangeListener();
    private final YouTubePlayer.PlaybackEventListener mOnPlaybackEventListener = new OnPlaybackEventListener();
    private PlayerStateListener mExternalPlayerStateListener = PlayerStateListener.NULL;
    private PlaybackState mPlaybackState = PlaybackState.STOPPED;

    @Nullable
    private YouTubePlayer mPlayer;
    private String mCurrentVideoId;
    private boolean mIsInFullscreenMode;


    public PlayerWrapper() {
    }

    public void initialize(YouTubePlayer.Provider playerProvider, String youtubeApiKey) {
        playerProvider.initialize(youtubeApiKey, mOnInitializationListener);
    }

    private void onYtPlayerInitialization(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        mPlayer = youTubePlayer;
        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        mPlayer.setOnFullscreenListener(fullScreen -> {
            mIsInFullscreenMode = fullScreen;
            mExternalPlayerStateListener.onDisplayModeChange(fullScreen);
        });
        mPlayer.setPlayerStateChangeListener(mOnPlayerStateChangeListener);
        mPlayer.setPlaybackEventListener(mOnPlaybackEventListener);
        mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        if (!wasRestored) {
            if (mCurrentVideoId != null) {
                mPlayer.loadVideo(mCurrentVideoId);
            }
        }
        mExternalPlayerStateListener.onInitializationSuccessful(wasRestored);
    }

    private void onYtPlayerInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        mExternalPlayerStateListener.onInitializationFailure(youTubeInitializationResult);
    }

    //..............................................................................................

    /**
     * Init with new video.
     *
     * @param videoId - youtube video id.
     */
    public void initWithNewVideo(String videoId) {
        mCurrentVideoId = videoId;
        //player ot init
        if (mPlayer != null) {
            mPlayer.cueVideo(mCurrentVideoId);
        }
    }

    /**
     * Play new video.
     *
     * @param videoId - youtube video id.
     */
    public void play(String videoId) {
        mCurrentVideoId = videoId;
        mPlaybackState = PlaybackState.PLAYING;
        //player ot init
        if (mPlayer != null) {
            mPlayer.play();
        }
    }

    /**
     * Pause the video.
     */
    public void pause() {
        mPlaybackState = PlaybackState.PAUSED;
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    /**
     * Resume playback of current video.
     */
    public void resume() {
        mPlaybackState = PlaybackState.PLAYING;
        if (mPlayer != null) {
            mPlayer.play();
        }
    }

    //..............................................................................................

    /**
     * Sets PlayerStateListener.
     *
     * @param listener - listener object.
     */
    public void setPlayerStateListener(PlayerStateListener listener) {
        if (listener == null) {
            mExternalPlayerStateListener = PlayerStateListener.NULL;
        } else {
            mExternalPlayerStateListener = listener;
        }
    }

    public boolean isInFullscreenMode() {
        return mIsInFullscreenMode;
    }

    /**
     * Informs the player that it is being laid out fullscreen.
     * @param fullscreen - flag to turn on / off fullscreen mode.
     */
    public void setFullscreenMode(boolean fullscreen) {
        mIsInFullscreenMode = fullscreen;
        if (mPlayer != null) {
            mPlayer.setFullscreen(fullscreen);
        }
    }

    /**
     * Stops playback of current video and resets playback progress.
     */
    public void resetVideo() {
        if (mPlayer != null) {
            mPlayer.pause();
            if (mCurrentVideoId != null) {
                mPlayer.cueVideo(mCurrentVideoId);
            }
        }
    }

    public void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    enum PlaybackState {
        PLAYING, PAUSED, STOPPED
    }

    public interface PlayerStateListener {
        PlayerStateListener NULL = new PlayerStateListener() {
            @Override
            public void onInitializationFailure(YouTubeInitializationResult errorReason) {
            }

            @Override
            public void onInitializationSuccessful(boolean wasRestored) {
            }

            @Override
            public void onDisplayModeChange(boolean fullScreen) {
            }

            @Override
            public void onVideoEnded() {
            }

            @Override
            public void onPlaying() {
            }

            @Override
            public void onPaused() {
            }
        };

        void onInitializationFailure(YouTubeInitializationResult errorReason);

        void onInitializationSuccessful(boolean wasRestored);

        void onDisplayModeChange(boolean fullScreen);

        void onVideoEnded();

        void onPlaying();

        void onPaused();
    }

    /**
     * Calls private callback methods.
     */
    private class OnInitializationInternalListener implements YouTubePlayer.OnInitializedListener {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
            onYtPlayerInitialization(provider, youTubePlayer, wasRestored);
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            onYtPlayerInitializationFailure(provider, youTubeInitializationResult);
        }
    }

    private class OnPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
        }

        @Override
        public void onLoaded(String s) {
        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {
        }

        @Override
        public void onVideoEnded() {
            mExternalPlayerStateListener.onVideoEnded();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
        }
    }

    private class OnPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {


        @Override
        public void onPlaying() {
            mExternalPlayerStateListener.onPlaying();
        }

        @Override
        public void onPaused() {
            mExternalPlayerStateListener.onPaused();
        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    }
}
